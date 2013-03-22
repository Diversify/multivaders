package se.diversify.multivaders.strategy;

import org.junit.Test;
import se.diversify.multivaders.DecisionStrategyTest;

public class TrivialDecisionStrategyTest {
    @Test
    public void testFrameworkShouldAssertResult() throws Exception {
        String[] happenings = {
                "ld",
                "sd",
                "rd",
                "lu",
                "ru",
                "su",
        };

        String[] expected = {
                "ld",
                "sd",
                "rd",
                "lu",
                "ru",
                "su",
        };

        DecisionStrategyTest.assertStrategy(happenings, new TrivialDecisionStrategy(), expected);
    }
}
