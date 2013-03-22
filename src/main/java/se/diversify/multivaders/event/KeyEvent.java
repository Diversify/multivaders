package se.diversify.multivaders.event;


public class KeyEvent {

    static public enum Function {
        left,
        right,
        shoot,
        nothing
    }

    static public enum ClickType {
        up, down
    }

    static public KeyEvent NothingKeyEvent = new KeyEvent(Function.nothing, ClickType.up);

    public KeyEvent(Function function, ClickType clickType) {
        this.function = function;
        this.clickType = clickType;
    }

    private Function function;
    private ClickType clickType;

    public Function getFunction() {
        return function;
    }

    public ClickType getClickType() {
        return clickType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeyEvent keyEvent = (KeyEvent) o;

        if (clickType != keyEvent.clickType) return false;
        if (function != keyEvent.function) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = function != null ? function.hashCode() : 0;
        result = 31 * result + (clickType != null ? clickType.hashCode() : 0);
        return result;
    }
}
