package se.diversify.multivaders;


public class TrivialDecisionStrategy implements DecisionStrategy {

    @Override
    public KeyEvent process(KeyEvent event) {
        return event;
    }
}
