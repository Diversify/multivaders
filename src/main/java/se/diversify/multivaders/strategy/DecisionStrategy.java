package se.diversify.multivaders.strategy;

import se.diversify.multivaders.DecisionMaker;
import se.diversify.multivaders.event.KeyEvent;

public interface DecisionStrategy {

    void process(KeyEvent event);

    void tick();

    void setDecisionMaker(DecisionMaker decisionMaker);

}
