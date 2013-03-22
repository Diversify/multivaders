package se.diversify.multivaders.event;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KeyEventTest {

    @Test
    public void shouldConvertFromString() throws Exception {
        // Fixture
        final KeyEvent expected = new KeyEvent(KeyEvent.Function.right, KeyEvent.ClickType.up);

        // Test
        final KeyEvent actual = new KeyEvent("ru");

        // Assert
        assertEquals(actual, expected);
    }

    @Test
    public void shouldConvertToString() throws Exception {
        // Fixture
        String expected = "sd";

        // Test
        final String actual = new KeyEvent(KeyEvent.Function.shoot, KeyEvent.ClickType.down).toJs();

        // Assert
        assertEquals(actual, expected);
    }
}
