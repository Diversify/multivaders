package se.diversify.multivaders.thread;


import se.diversify.multivaders.strategy.DecisionStrategy;

public class IntervalKeeper implements Runnable {

    private DecisionStrategy decisionStrategy;



    public IntervalKeeper(DecisionStrategy decisionStrategy) {
        this.decisionStrategy = decisionStrategy;
    }

    @Override
    public void run() {

    }
}
