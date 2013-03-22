package se.diversify.multivaders;

import static se.diversify.multivaders.KeyEvent.*;
import static se.diversify.multivaders.KeyEvent.ClickType.*;
import static se.diversify.multivaders.KeyEvent.Function.*;

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
