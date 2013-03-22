package se.diversify.multivaders;

import se.diversify.multivaders.event.KeyEvent;
import se.diversify.multivaders.strategy.Constants;
import se.diversify.multivaders.strategy.DecisionStrategy;

import java.util.Timer;

public class DecisionMaker implements Runnable {

    private DecisionStrategy strategy;
    private final EventCallback callback;
    private boolean running;

    public DecisionMaker(DecisionStrategy strategy, EventCallback callback) {
        this.strategy = strategy;
        this.callback = callback;

        strategy.setDecisionMaker(this);

        new Thread(this).start();
    }

    public void process(KeyEvent event) {
        synchronized (strategy) {
            strategy.process(event);
        }
    }

    public void sendResponse(KeyEvent keyEvent) {
        if (callback != null) {
            callback.sendEvent(keyEvent);
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        while(running) {
            try {
                Thread.sleep(Constants.TIME_SLICE);
                synchronized (strategy) {
                    strategy.tick();
                }
            } catch (InterruptedException e) {
            }
        }
    }

    public interface EventCallback {
        void sendEvent(KeyEvent event);
    }
}
