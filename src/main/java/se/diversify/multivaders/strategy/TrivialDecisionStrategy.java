package se.diversify.multivaders.strategy;


import se.diversify.multivaders.event.KeyEvent;

public class TrivialDecisionStrategy extends AbstractStrategy {

    @Override
    public void process(KeyEvent event) {
        decisionMaker.sendResponse(event);
    }
}
