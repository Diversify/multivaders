package se.diversify.multivaders.strategy;

import se.diversify.multivaders.event.KeyEvent;

import static se.diversify.multivaders.event.KeyEvent.*;
import static se.diversify.multivaders.event.KeyEvent.ClickType.*;

public class BasedOnPreviousStrategy extends AbstractStrategy {

    private KeyEvent oldEvent;

    @Override
    public void process(KeyEvent newEvent) {
        KeyEvent chosenEvent = null;
        if(oldEvent == null) {
            chosenEvent = newEvent;
        }
        else {
            if (oldEvent.getClickType() == down) {
                if (oldEvent.getFunction() == newEvent.getFunction() && newEvent.getClickType() == up) {
                    chosenEvent = newEvent;
                }
            } else {
                if (newEvent.getClickType() == down) {
                    chosenEvent = newEvent;
                }
            }
        }
        oldEvent = newEvent;
        decisionMaker.sendResponse(chosenEvent);
    }
}
