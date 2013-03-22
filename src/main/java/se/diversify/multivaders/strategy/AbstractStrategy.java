package se.diversify.multivaders.strategy;

import se.diversify.multivaders.DecisionMaker;
import se.diversify.multivaders.event.KeyEvent;

/**
 * Created with IntelliJ IDEA.
 * User: uzilan
 * Date: 2013-03-22
 * Time: 12:04
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractStrategy implements DecisionStrategy {

    protected DecisionMaker decisionMaker;

    public void tick() {

    }

    @Override
    public void setDecisionMaker(DecisionMaker decisionMaker) {
        this.decisionMaker = decisionMaker;
    }
}
