package se.diversify.multivaders.strategy;

import se.diversify.multivaders.event.KeyEvent;

import static se.diversify.multivaders.event.KeyEvent.*;
import static se.diversify.multivaders.event.KeyEvent.ClickType.*;

public class SumDecisionStrategy implements DecisionStrategy {

    private KeyEvent oldEvent;

    @Override
    public KeyEvent process(KeyEvent newEvent) {
        KeyEvent toReturn = NothingKeyEvent;
        if (oldEvent.getClickType() == down) {
            if (oldEvent.getFunction() == newEvent.getFunction() && newEvent.getClickType() == up) {
                toReturn = newEvent;
            }
        } else {
            if (newEvent.getClickType() == down) {
                toReturn = newEvent;
            }
        }
        return toReturn;
    }
}
