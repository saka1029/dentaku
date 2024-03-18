package saka1029.dentaku;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

public class Functions {
    static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;

    Map<String, Unary> uops = new HashMap<>();
    Map<String, Binary> bops = new HashMap<>();
    Map<String, High> hops = new HashMap<>();

    private Functions() {
        initialize();
    }

    public static Functions of() {
        return new Functions();
    }

    static BigDecimal dec(boolean b) {
        return b ? BigDecimal.ONE : BigDecimal.ZERO;
    }

    static BigDecimal dec(int i) {
        return new BigDecimal(i);
    }

    static BigDecimal dec(double d) {
        return new BigDecimal(d);
    }

    static double d(BigDecimal d) {
        return d.doubleValue();
    }

    static boolean b(BigDecimal d) {
        return !d.equals(BigDecimal.ZERO);
    }

    void initialize() {
        // unary operators
        uops.put("-", v -> v.map(BigDecimal::negate));
        uops.put("+", v -> v.reduce((l, r) -> l.binary(BigDecimal::add, r)));
        uops.put("*", v -> v.reduce((l, r) -> l.binary(BigDecimal::multiply, r)));
        uops.put("sign", v -> v.map(x -> dec(x.signum())));
        uops.put("sqrt", v -> v.map(x -> dec(Math.sqrt(d(x)))));
        uops.put("sin", v -> v.map(x -> dec(Math.sin(d(x)))));
        uops.put("asin", v -> v.map(x -> dec(Math.asin(d(x)))));
        uops.put("cos", v -> v.map(x -> dec(Math.cos(d(x)))));
        uops.put("acos", v -> v.map(x -> dec(Math.acos(d(x)))));
        uops.put("tan", v -> v.map(x -> dec(Math.tan(d(x)))));
        uops.put("atan", v -> v.map(x -> dec(Math.atan(d(x)))));
        uops.put("log", v -> v.map(x -> dec(Math.log(d(x)))));
        uops.put("log10", v -> v.map(x -> dec(Math.log10(d(x)))));
        uops.put("not", v -> v.map(x -> dec(!b(x))));
        // binary operators
        bops.put("+", (l, r) -> l.binary(BigDecimal::add, r));
        bops.put("-", (l, r) -> l.binary(BigDecimal::subtract, r));
        bops.put("*", (l, r) -> l.binary(BigDecimal::multiply, r));
        bops.put("/", (l, r) -> l.binary((a, b) -> a.divide(b, MATH_CONTEXT), r));
        bops.put("%", (l, r) -> l.binary((a, b) -> a.remainder(b, MATH_CONTEXT), r));
        bops.put("^", (l, r) -> l.binary((a, b) -> dec(Math.pow(d(a), d(b))), r));
        bops.put("==", (l, r) -> l.binary((a, b) -> dec(a.compareTo(b) == 0), r));
        bops.put("~", (l, r) -> l.binary((a, b) -> dec(a.subtract(b).abs().compareTo(Value.EPSILON) < 0), r));
        bops.put("!=", (l, r) -> l.binary((a, b) -> dec(a.compareTo(b) != 0), r));
        bops.put("<", (l, r) -> l.binary((a, b) -> dec(a.compareTo(b) < 0), r));
        bops.put("<=", (l, r) -> l.binary((a, b) -> dec(a.compareTo(b) <= 0), r));
        bops.put(">", (l, r) -> l.binary((a, b) -> dec(a.compareTo(b) > 0), r));
        bops.put(">=", (l, r) -> l.binary((a, b) -> dec(a.compareTo(b) >= 0), r));
        bops.put("min", (l, r) -> l.binary(BigDecimal::min, r));
        bops.put("max", (l, r) -> l.binary(BigDecimal::max, r));
        bops.put("and", (l, r) -> l.binary((a, b) -> dec(b(a) & b(b)), r));
        bops.put("or", (l, r) -> l.binary((a, b) -> dec(b(a) | b(b)), r));
        bops.put("xor", (l, r) -> l.binary((a, b) -> dec(b(a) ^ b(b)), r));
        bops.put("filter", Value::filter);
        bops.put("..", Value::to);
        // high order operations
        hops.put("@", Value::reduce);
        hops.put("@@", Value::cumulate);

    }

}
