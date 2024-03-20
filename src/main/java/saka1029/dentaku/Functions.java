package saka1029.dentaku;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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

    public Unary unary(String name) {
        return uops.get(name);
    }

    public void unary(String name, Unary value) {
        uops.put(name, value);
    }

    public Binary binary(String name) {
        return bops.get(name);
    }

    public void binary(String name, Binary value) {
        bops.put(name, value);
    }

    public High high(String name) {
        return hops.get(name);
    }

    public void high(String name, High value) {
        hops.put(name, value);
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
        uops.put("length", (c, v) -> Value.of(dec(v.size())));
        uops.put("-", (c, v) -> v.map(BigDecimal::negate));
        uops.put("+", (c, v) -> v.reduce(c, (c1, l, r) -> l.binary(BigDecimal::add, r)));
        uops.put("*", (c, v) -> v.reduce(c, (c1, l, r) -> l.binary(BigDecimal::multiply, r)));
        uops.put("sign", (c, v) -> v.map(x -> dec(x.signum())));
        uops.put("int", (c, v) -> v.map(x -> x.setScale(0, RoundingMode.HALF_UP)));
        uops.put("sqrt", (c, v) -> v.map(x -> dec(Math.sqrt(d(x)))));
        uops.put("sin", (c, v) -> v.map(x -> dec(Math.sin(d(x)))));
        uops.put("asin", (c, v) -> v.map(x -> dec(Math.asin(d(x)))));
        uops.put("cos", (c, v) -> v.map(x -> dec(Math.cos(d(x)))));
        uops.put("acos", (c, v) -> v.map(x -> dec(Math.acos(d(x)))));
        uops.put("tan", (c, v) -> v.map(x -> dec(Math.tan(d(x)))));
        uops.put("atan", (c, v) -> v.map(x -> dec(Math.atan(d(x)))));
        uops.put("log", (c, v) -> v.map(x -> dec(Math.log(d(x)))));
        uops.put("log10", (c, v) -> v.map(x -> dec(Math.log10(d(x)))));
        uops.put("not", (c, v) -> v.map(x -> dec(!b(x))));
        uops.put("sort", (c, v) -> v.sort());
        uops.put("reverse", (c, v) -> v.reverse());
        uops.put("shuffle", (c, v) -> v.shuffle());
        // binary operators
        bops.put("+", (c, l, r) -> l.binary(BigDecimal::add, r));
        bops.put("-", (c, l, r) -> l.binary(BigDecimal::subtract, r));
        bops.put("*", (c, l, r) -> l.binary(BigDecimal::multiply, r));
        bops.put("/", (c, l, r) -> l.binary((a, b) -> a.divide(b, MATH_CONTEXT), r));
        bops.put("%", (c, l, r) -> l.binary((a, b) -> a.remainder(b, MATH_CONTEXT), r));
        bops.put("^", (c, l, r) -> l.binary((a, b) -> dec(Math.pow(d(a), d(b))), r));
        bops.put("round", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.HALF_UP), r));
        bops.put("ceiling", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.CEILING), r));
        bops.put("down", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.DOWN), r));
        bops.put("floor", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.FLOOR), r));
        // bops.put("half-down", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.HALF_DOWN), r));
        // bops.put("half-up", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.HALF_UP), r));
        bops.put("up", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.UP), r));
        bops.put("==", (c, l, r) -> l.binary((a, b) -> dec(a.compareTo(b) == 0), r));
        bops.put("~", (c, l, r) -> l.binary((a, b) -> dec(a.subtract(b).abs().compareTo(Value.EPSILON) < 0), r));
        bops.put("!=", (c, l, r) -> l.binary((a, b) -> dec(a.compareTo(b) != 0), r));
        bops.put("<", (c, l, r) -> l.binary((a, b) -> dec(a.compareTo(b) < 0), r));
        bops.put("<=", (c, l, r) -> l.binary((a, b) -> dec(a.compareTo(b) <= 0), r));
        bops.put(">", (c, l, r) -> l.binary((a, b) -> dec(a.compareTo(b) > 0), r));
        bops.put(">=", (c, l, r) -> l.binary((a, b) -> dec(a.compareTo(b) >= 0), r));
        bops.put("min", (c, l, r) -> l.binary(BigDecimal::min, r));
        bops.put("max", (c, l, r) -> l.binary(BigDecimal::max, r));
        bops.put("and", (c, l, r) -> l.binary((a, b) -> dec(b(a) & b(b)), r));
        bops.put("or", (c, l, r) -> l.binary((a, b) -> dec(b(a) | b(b)), r));
        bops.put("xor", (c, l, r) -> l.binary((a, b) -> dec(b(a) ^ b(b)), r));
        bops.put("filter", (c, l, r) -> l.filter(r));
        bops.put("..", (c, l, r) -> l.to(r));
        // high order operations
        hops.put("@", (c, v, b) -> v.reduce(c, b));
        hops.put("@@", (c, v, b) -> v.cumulate(c, b));
    }

}
