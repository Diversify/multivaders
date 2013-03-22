package se.diversify.multivaders;

public class DecisionMaker {

    private DecisionStrategy strategy;

    public DecisionMaker(DecisionStrategy strategy) {
        this.strategy = strategy;
    }

    public KeyEvent process(KeyEvent event) {
        return strategy.process(event);
    }
}
