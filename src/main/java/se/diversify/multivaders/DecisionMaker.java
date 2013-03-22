package se.diversify.multivaders;

import se.diversify.multivaders.event.KeyEvent;
import se.diversify.multivaders.strategy.DecisionStrategy;

public class DecisionMaker implements Runnable {

    private DecisionStrategy strategy;

    public DecisionMaker(DecisionStrategy strategy) {
        this.strategy = strategy;
    }

    public void process(KeyEvent event) {
        strategy.process(event);
    }

    public void sendResponse(KeyEvent keyEvent) {

    }

    @Override
    public void run() {

    }
}
