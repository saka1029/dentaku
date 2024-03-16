package saka1029.dentaku;

public class ValueException extends RuntimeException {
    public ValueException(String format, Object... args) {
        super(format.formatted(args));
    }
}
