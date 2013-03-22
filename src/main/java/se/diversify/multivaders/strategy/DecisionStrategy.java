package se.diversify.multivaders.strategy;

import se.diversify.multivaders.event.KeyEvent;

public interface DecisionStrategy {

    KeyEvent process(KeyEvent event);

}
