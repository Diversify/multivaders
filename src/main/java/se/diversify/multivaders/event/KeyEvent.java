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

}
