package se.diversify.multivaders.server.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.activation.MimetypesFileTypeMap;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.*;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * A handler for static file serving.
 *
 * @author Christer Sandberg, Diversify Stockholm.
 */
public final class HttpStaticFileServerHandler extends SimpleChannelUpstreamHandler {

    /** HTTP date format. */
    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

    /** HTTP date timezone. */
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";

    /** How long to cache. */
    public static final int HTTP_CACHE_SECONDS = 60;

    /** Logger */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** The root directory for file serving. */
    private final File rootDir;

    /** Channels handling. */
    private final ChannelGroup channelGroup;

    /**
     * Create a new instance.
     *
     * @param rootDir The root directory for file serving.
     * @param channelGroup A channel group for handling all the channels.
     */
    public HttpStaticFileServerHandler(File rootDir, ChannelGroup channelGroup) {
        if (rootDir == null)
            throw new IllegalArgumentException("rootDir is null!");

        if (channelGroup == null)
            throw new IllegalArgumentException("channelGroup is null!");

        this.rootDir = rootDir;
        this.channelGroup = channelGroup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        logger.info("Client {} connected...", e.getChannel());
        channelGroup.add(e.getChannel());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        logger.debug("Message received from: {}", e.getChannel());

        HttpRequest request = (HttpRequest) e.getMessage();
        if (request.getMethod() != GET) {
            sendError(ctx, METHOD_NOT_ALLOWED);
            return;
        }

        final File path = sanitizeUri(request.getUri());
        if (path == null) {
            sendError(ctx, FORBIDDEN);
            return;
        }

        if (path.isHidden() || !path.exists()) {
            sendError(ctx, NOT_FOUND);
            return;
        }
        if (!path.isFile()) {
            sendError(ctx, FORBIDDEN);
            return;
        }

        // Cache calidation.
        String ifModifiedSince = request.getHeader(IF_MODIFIED_SINCE);
        if (ifModifiedSince != null && ifModifiedSince.length() != 0) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
            Date ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince);

            // Only compare up to the second because the datetime format we send to the client does
            // not have milliseconds.
            long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
            long fileLastModifiedSeconds = path.lastModified() / 1000;
            if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds) {
                sendNotModified(ctx);
                return;
            }
        }

        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(path, "r");
        } catch (FileNotFoundException fex) {
            // Should not be possible.
            sendError(ctx, NOT_FOUND);
            return;
        }

        long fileLength = raf.length();

        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);

        setContentLength(response, fileLength);
        setContentTypeHeader(response, path);
        setDateAndCacheHeaders(response, path);

        Channel channel = e.getChannel();

        // Write the initial line and the header.
        channel.write(response);

        // Write the content.
        ChannelFuture writeFuture;
        // No encryption - use zero-copy.
        final FileRegion region = new DefaultFileRegion(raf.getChannel(), 0, fileLength);
        writeFuture = channel.write(region);
        writeFuture.addListener(new ChannelFutureProgressListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                region.releaseExternalResources();
            }

            @Override
            public void operationProgressed(ChannelFuture future, long amount, long current, long total) {
                logger.debug(String.format("%s: %d / %d (+%d)%n", path, current, total, amount));
            }
        });

        // Decide whether to close the connection or not.
        if (!isKeepAlive(request)) {
            // Close the connection when the whole content is written out.
            writeFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception {
        Channel channel = e.getChannel();
        Throwable cause = e.getCause();

        logger.warn("Got exception in static file handler", cause);

        if (cause instanceof TooLongFrameException) {
            sendError(ctx, BAD_REQUEST);
            return;
        }

        if (channel.isConnected())
            sendError(ctx, INTERNAL_SERVER_ERROR);
    }

    /**
     * Check if the specified URI is bogus etc.
     *
     * @param uri The URI to sanitize.
     * @return A sanitized URI as a file relative to our root dir or {@code null} if it was bogus.
     */
    private File sanitizeUri(String uri) {
        // Decode the path.
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            try {
                uri = URLDecoder.decode(uri, "ISO-8859-1");
            } catch (UnsupportedEncodingException e1) {
                throw new Error();
            }
        }

        // Convert file separators.
        uri = uri.replace('/', File.separatorChar);

        // Simplistic dumb security check.
        if (uri.contains(File.separator + '.') ||
                uri.contains('.' + File.separator) ||
                uri.startsWith(".") || uri.endsWith("."))
            return null;

        // Convert to absolute path relative our root directory.
        return new File(rootDir, uri);
    }

    /**
     * Send an error to the browser.
     *
     * @param ctx Channel handler context.
     * @param status Response status.
     */
    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
        response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.setContent(ChannelBuffers.copiedBuffer(
                "Failure: " + status.toString() + "\r\n",
                CharsetUtil.UTF_8));

        // Close the connection as soon as the error message is sent.
        ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * When file timestamp is the same as what the browser is sending up, send a "304 Not Modified".
     *
     * @param ctx Channel handler context.
     */
    private static void sendNotModified(ChannelHandlerContext ctx) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, NOT_MODIFIED);
        setDateHeader(response);

        // Close the connection as soon as the error message is sent.
        ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * Sets the Date header for the HTTP response.
     *
     * @param response HTTP response
     */
    private static void setDateHeader(HttpResponse response) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        response.setHeader(DATE, dateFormatter.format(time.getTime()));
    }

    /**
     * Sets the Date and Cache headers for the HTTP response.
     *
     * @param response HTTP response
     * @param fileToCache File to extract last modified timestamp from.
     */
    private static void setDateAndCacheHeaders(HttpResponse response, File fileToCache) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        // Date header.
        Calendar time = new GregorianCalendar();
        response.setHeader(DATE, dateFormatter.format(time.getTime()));

        // Add cache headers.
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        response.setHeader(EXPIRES, dateFormatter.format(time.getTime()));
        response.setHeader(CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        response.setHeader(LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
    }

    /**
     * Sets the content type header for the HTTP response.
     *
     * @param response HTTP response
     * @param file File to extract content type from.
     */
    private static void setContentTypeHeader(HttpResponse response, File file) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.setHeader(CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()));
    }

}
