package se.diversify.multivaders;

import se.diversify.multivaders.event.KeyEvent;
import se.diversify.multivaders.strategy.DecisionStrategy;

public class DecisionMaker implements Runnable {

    private DecisionStrategy strategy;
    private final EventCallback callback;

    public DecisionMaker(DecisionStrategy strategy, EventCallback callback) {
        this.strategy = strategy;
        this.callback = callback;

        strategy.setDecisionMaker(this);
    }

    public void process(KeyEvent event) {
        strategy.process(event);
    }

    public void sendResponse(KeyEvent keyEvent) {
        if (callback != null) {
            callback.sendEvent(keyEvent);
        }
    }

    @Override
    public void run() {

    }

    public interface EventCallback {
        void sendEvent(KeyEvent event);
    }
}
