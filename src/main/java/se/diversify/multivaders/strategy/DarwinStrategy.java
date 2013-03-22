package se.diversify.multivaders.strategy;


import org.joda.time.DateTime;
import se.diversify.multivaders.DecisionMaker;
import se.diversify.multivaders.event.KeyEvent;
import sun.nio.cs.ext.MacThai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DarwinStrategy extends AbstractStrategy {

    private Map<KeyEvent, DateTime> events = new HashMap<KeyEvent, DateTime>();

    @Override
    public void process(KeyEvent event) {
        events.put(event, DateTime.now());
    }

    @Override
    public void tick() {

        DateTime lastLeft = null;
        DateTime lastRight = null;
        DateTime lastShoot = null;
        long l = 0;
        long r = 0;
        long s = 0;

        for (Map.Entry<KeyEvent, DateTime> entry : events.entrySet()) {
            KeyEvent event = (KeyEvent)entry.getKey();
            DateTime curr = (DateTime)entry.getValue();

            switch(event.getClickType()) {
                case down:
                    switch (event.getFunction()) {
                        case left:
                            lastLeft = curr;
                            break;
                        case right:
                            lastRight = curr;
                            break;
                        case shoot:
                            lastShoot = curr;
                            break;
                    }
                case up:
                    switch (event.getFunction()) {
                        case left:
                            l += curr.minus(lastLeft.getMillis()).getMillis();
                            lastLeft = null;
                            break;
                        case right:
                            r += curr.minus(lastRight.getMillis()).getMillis();
                            lastRight = null;
                            break;
                        case shoot:
                            s += curr.minus(lastShoot.getMillis()).getMillis();
                            lastShoot = null;
                            break;
                    }
        }
    }

    KeyEvent toReturn = null;
        long result = Math.max(r, Math.max(l, s));
        KeyEvent eventDown = null;
        KeyEvent eventUp = null;
        if(result == l) {
            eventDown = new KeyEvent(KeyEvent.Function.left, KeyEvent.ClickType.down);
            eventUp = new KeyEvent(KeyEvent.Function.left, KeyEvent.ClickType.up);
        }
        else if(result == r) {
            eventDown = new KeyEvent(KeyEvent.Function.left, KeyEvent.ClickType.down);
            eventUp = new KeyEvent(KeyEvent.Function.left, KeyEvent.ClickType.up);
        }
        else {
            eventDown = new KeyEvent(KeyEvent.Function.left, KeyEvent.ClickType.down);
            eventUp = new KeyEvent(KeyEvent.Function.left, KeyEvent.ClickType.up);
        }




    decisionMaker.sendResponse(eventDown);
        try {
            Thread.sleep(result);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        decisionMaker.sendResponse(eventUp);
    }
}
