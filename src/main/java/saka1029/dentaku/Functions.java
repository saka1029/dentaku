package saka1029.dentaku;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class Functions {
    static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;

    Map<String, Str<Unary>> uops = new HashMap<>();
    Map<String, Str<Binary>> bops = new HashMap<>();
    Map<String, Str<High>> hops = new HashMap<>();

    private Functions() {
        initialize();
    }

    public static Functions of() {
        return new Functions();
    }

    public Unary unary(String name) {
        Str<Unary> e = uops.get(name);
        return e != null ? e.op : null;
    }

    public void unary(String name, Unary value, String string) {
        uops.put(name, Str.of(value, string));
    }

    public Binary binary(String name) {
        Str<Binary> e = bops.get(name);
        return e != null ? e.op : null;
    }

    public void binary(String name, Binary value, String string) {
        bops.put(name, Str.of(value, string));
    }

    public High high(String name) {
        Str<High> e = hops.get(name);
        return e != null ? e.op : null;
    }

    public void high(String name, High value, String string) {
        hops.put(name, Str.of(value, string));
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
        unary("length", (c, v) -> Value.of(dec(v.size())), "length M : 並びの長さ");
        unary("-", (c, v) -> v.map(BigDecimal::negate), "- M : 各要素の符号反転");
        unary("+", (c, v) -> v.reduce(c, (c1, l, r) -> l.binary(BigDecimal::add, r)), "+ M : 全要素の合計");
        unary("*", (c, v) -> v.reduce(c, (c1, l, r) -> l.binary(BigDecimal::multiply, r)), "* M : 全要素の積");
        unary("^", (c, v) -> v.reduce(c, (c1, l, r) -> l.binary((a, b) -> dec(Math.pow(d(a), d(b))), r)), "^ M : 全要素のべき乗");
        unary("sign", (c, v) -> v.map(x -> dec(x.signum())), "sign M : 各要素の符号(-1, 0, 1)");
        unary("int", (c, v) -> v.map(x -> x.setScale(0, RoundingMode.HALF_UP)), "int M : 各要素の整数化(四捨五入)");
        unary("sqrt", (c, v) -> v.map(x -> dec(Math.sqrt(d(x)))), "sqrt M : 各要素の平方根");
        unary("sin", (c, v) -> v.map(x -> dec(Math.sin(d(x)))), "sin M : 各要素のsin値");
        unary("asin", (c, v) -> v.map(x -> dec(Math.asin(d(x)))), "asin M : 各要素のasin値");
        unary("cos", (c, v) -> v.map(x -> dec(Math.cos(d(x)))), "cos M : 各要素のcos値");
        unary("acos", (c, v) -> v.map(x -> dec(Math.acos(d(x)))), "acos M : 各要素のacos値");
        unary("tan", (c, v) -> v.map(x -> dec(Math.tan(d(x)))), "tan M : 各要素のtan値");
        unary("atan", (c, v) -> v.map(x -> dec(Math.atan(d(x)))), "atan M : 各要素のatan値");
        unary("log", (c, v) -> v.map(x -> dec(Math.log(d(x)))), "log M : 各要素のlog値(底はe)");
        unary("log10", (c, v) -> v.map(x -> dec(Math.log10(d(x)))), "log10 M : 各要素のlog値(底は10)");
        unary("not", (c, v) -> v.map(x -> dec(!b(x))), "not M : 各要素の否定値(0:偽⇔1:真)");
        unary("sort", (c, v) -> v.sort(), "sort M : 上昇順にソート");
        unary("reverse", (c, v) -> v.reverse(), "reverse M : 並びの逆転");
        unary("shuffle", (c, v) -> v.shuffle(), "shuffle M : 並びのシャッフル");
        // binary operators
        binary("+", (c, l, r) -> l.binary(BigDecimal::add, r), "M + M : 加算");
        binary("-", (c, l, r) -> l.binary(BigDecimal::subtract, r), "");
        binary("*", (c, l, r) -> l.binary(BigDecimal::multiply, r), "");
        binary("/", (c, l, r) -> l.binary((a, b) -> a.divide(b, MATH_CONTEXT), r), "");
        binary("%", (c, l, r) -> l.binary((a, b) -> a.remainder(b, MATH_CONTEXT), r), "");
        binary("^", (c, l, r) -> l.binary((a, b) -> dec(Math.pow(d(a), d(b))), r), "");
        binary("round", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.HALF_UP), r), "");
        binary("ceiling", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.CEILING), r), "");
        binary("down", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.DOWN), r), "");
        binary("floor", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.FLOOR), r), "");
        // binary("half-down", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.HALF_DOWN), r), "");
        // binary("half-up", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.HALF_UP), r), "");
        binary("up", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.UP), r), "");
        binary("==", (c, l, r) -> l.binary((a, b) -> dec(a.compareTo(b) == 0), r), "");
        binary("~", (c, l, r) -> l.binary((a, b) -> dec(a.subtract(b).abs().compareTo(Value.EPSILON) < 0), r), "");
        binary("!=", (c, l, r) -> l.binary((a, b) -> dec(a.compareTo(b) != 0), r), "");
        binary("<", (c, l, r) -> l.binary((a, b) -> dec(a.compareTo(b) < 0), r), "");
        binary("<=", (c, l, r) -> l.binary((a, b) -> dec(a.compareTo(b) <= 0), r), "");
        binary(">", (c, l, r) -> l.binary((a, b) -> dec(a.compareTo(b) > 0), r), "");
        binary(">=", (c, l, r) -> l.binary((a, b) -> dec(a.compareTo(b) >= 0), r), "");
        binary("min", (c, l, r) -> l.binary(BigDecimal::min, r), "");
        binary("max", (c, l, r) -> l.binary(BigDecimal::max, r), "");
        binary("and", (c, l, r) -> l.binary((a, b) -> dec(b(a) & b(b)), r), "");
        binary("or", (c, l, r) -> l.binary((a, b) -> dec(b(a) | b(b)), r), "");
        binary("xor", (c, l, r) -> l.binary((a, b) -> dec(b(a) ^ b(b)), r), "");
        binary("filter", (c, l, r) -> l.filter(r), "");
        binary("..", (c, l, r) -> l.to(r), "");
        // high order operations
        high("@", (c, v, b) -> v.reduce(c, b), "");
        high("@<", (c, v, b) -> v.reduceRight(c, b), "");
        high("@@", (c, v, b) -> v.cumulate(c, b), "");
    }

}
