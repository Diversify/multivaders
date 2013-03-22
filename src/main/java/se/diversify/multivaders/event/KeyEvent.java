package se.diversify.multivaders.event;


public class KeyEvent {
    static public enum Function {
        left('l'),
        right('r'),
        shoot('s');

        Function(char c) {
            protocolValue = c;
        }

        private final char protocolValue;

        public static Function fromProtocol(char c) {
            for (Function function : Function.values()) {
                if (function.protocolValue == c) {
                    return function;
                }
            }

            throw new IllegalArgumentException();
        }
    }

    static public enum ClickType {
        up('u'),
        down('d');

        ClickType(char c) {
            protocolValue = c;
        }

        private final char protocolValue;

        public static ClickType fromProtocol(char c) {
            for (ClickType clickType : ClickType.values()) {
                if (clickType.protocolValue == c) {
                    return clickType;
                }
            }

            throw new IllegalArgumentException();
        }
    }

    public KeyEvent(String message) {
        if (message.length() != 2) {
            throw new IllegalArgumentException();
        }

        this.function = Function.fromProtocol(message.charAt(0));
        this.clickType = ClickType.fromProtocol(message.charAt(1));
    }

    public KeyEvent(Function function, ClickType clickType) {
        this.function = function;
        this.clickType = clickType;
    }


    public String toJs() {
        return "" + function.protocolValue + clickType.protocolValue;
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
