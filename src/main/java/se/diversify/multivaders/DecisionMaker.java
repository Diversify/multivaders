package se.diversify.multivaders;

import se.diversify.multivaders.event.KeyEvent;
import se.diversify.multivaders.strategy.DecisionStrategy;

public class DecisionMaker {

    private DecisionStrategy strategy;

    public DecisionMaker(DecisionStrategy strategy) {
        this.strategy = strategy;
    }

    public KeyEvent process(KeyEvent event) {
        return strategy.process(event);
    }
}
