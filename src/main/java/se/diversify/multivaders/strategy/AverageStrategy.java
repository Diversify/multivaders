package se.diversify.multivaders.strategy;


import se.diversify.multivaders.DecisionMaker;
import se.diversify.multivaders.event.KeyEvent;

import java.util.ArrayList;
import java.util.List;

public class AverageStrategy extends AbstractStrategy {

    private List<KeyEvent> events = new ArrayList<KeyEvent>();

    @Override
    public void process(KeyEvent event) {
        events.add(event);
        decisionMaker.sendResponse(events.get(0));
    }

    @Override
    public void tick() {

    }
}
