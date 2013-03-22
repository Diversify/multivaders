package se.diversify.multivaders.strategy;


import se.diversify.multivaders.event.KeyEvent;

public class TrivialDecisionStrategy implements DecisionStrategy {

    @Override
    public KeyEvent process(KeyEvent event) {
        return event;
    }
}
