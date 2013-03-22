package se.diversify.multivaders;

import org.junit.Test;
import se.diversify.multivaders.event.KeyEvent;
import se.diversify.multivaders.strategy.DecisionStrategy;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class DecisionStrategyTest {


    @Test
    public void testFrameworkShouldAssertResult() throws Exception {
        String[] happenings = {
                "ld",
                "lu",
                "rd",
                "ru",
        };

        String[] expected = {
                "ld",
                "lu",
                "rd",
                "ru",
        };

        assertStrategy(happenings, new TransparentStrategy(), expected);
    }

    private static class TransparentStrategy implements DecisionStrategy {

        @Override
        public KeyEvent process(KeyEvent event) {
            return new KeyEvent(event.getFunction(), event.getClickType());
        }
    }

    public static void assertStrategy(String[] happenings, DecisionStrategy strategy, String[] expected) {
        // Fixture
        final List<KeyEvent> events = generateEvents(happenings);
        final List<KeyEvent> expectedEvents = generateEvents(expected);
        final ArrayList<KeyEvent> actual = new ArrayList<KeyEvent>();

        // Test
        for (KeyEvent event : events) {
            actual.add(strategy.process(event));
        }

        // Assert
        assertEquals(expectedEvents, actual);
    }

    private static List<KeyEvent> generateEvents(String[] happenings) {
        List<KeyEvent> result = new ArrayList<KeyEvent>();

        for (String happening : happenings) {
            KeyEvent.Function function = function(happening.charAt(0));
            KeyEvent.ClickType clickType = clickType(happening.charAt(1));
            result.add(new KeyEvent(function, clickType));
        }

        return result;
    }

    private static KeyEvent.ClickType clickType(char c) {
        switch (c) {
            case 'd':
                return KeyEvent.ClickType.down;
            case 'u':
                return KeyEvent.ClickType.up;
            default:
                throw new InvalidParameterException();
        }
    }

    private static KeyEvent.Function function(char c) {
        switch (c) {
            case 'l':
                return KeyEvent.Function.left;
            case 'r':
                return KeyEvent.Function.right;
            case 's':
                return KeyEvent.Function.shoot;
            case 'n':
                return KeyEvent.Function.nothing;
            default:
                throw new InvalidParameterException();
        }
    }
}
