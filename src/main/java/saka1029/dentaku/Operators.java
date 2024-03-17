package saka1029.dentaku;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

public class Operators {
    public static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;
    public static final BigDecimal MIN_VALUE = new BigDecimal(-Double.MAX_VALUE);
    public static final BigDecimal MAX_VALUE = new BigDecimal(Double.MAX_VALUE);

    final Map<String, UOP> uops = new HashMap<>();
    final Map<String, BOP> bops = new HashMap<>();
    final Map<String, MOP> mops = new HashMap<>();

    public static Operators of() {
        Operators ops = new Operators();
        initialize(ops);
        return ops;
    }

    static BigDecimal bigDecimal(boolean b) {
        return b ? BigDecimal.ONE : BigDecimal.ZERO;
    }

    static BigDecimal bigDecimal(int i) {
        return new BigDecimal(i);
    }

    static BigDecimal bigDecimal(double d) {
        return new BigDecimal(d, Value.MATH_CONTEXT);
    }

    static boolean bool(BigDecimal d) {
        return !d.equals(BigDecimal.ZERO);
    }

    static int i(BigDecimal d) {
        return d.intValue();
    }

    static double d(BigDecimal d) {
        return d.doubleValue();
    }

    static void initialize(Operators ops) {
        // unary operators
        ops.uops.put("-", BigDecimal::negate);
        ops.uops.put("+", x -> x);
        ops.uops.put("sign", Value.SIGN);
        ops.uops.put("sin", Value.SIN);
        ops.uops.put("asin", Value.ASIN);
        ops.uops.put("cos", Value.COS);
        ops.uops.put("acos", Value.ACOS);
        ops.uops.put("tan", Value.TAN);
        ops.uops.put("atan", Value.ATAN);
        ops.uops.put("not", Value.NOT);
        // binary operators
        ops.bops.put("+", Value.ADD);
        ops.bops.put("-", Value.MINUS);
        ops.bops.put("*", Value.MULT);
        ops.bops.put("/", Value.DIV);
        ops.bops.put("%", Value.MOD);
        ops.bops.put("^", Value.POW);
        ops.bops.put("==", Value.EQ);
        ops.bops.put("!=", Value.NE);
        ops.bops.put("<", Value.LT);
        ops.bops.put("<=", Value.LE);
        ops.bops.put(">", Value.GT);
        ops.bops.put(">=", Value.GE);
        ops.bops.put("min", Value.MIN);
        ops.bops.put("max", Value.MAX);
        ops.bops.put("and", Value.AND);
        ops.bops.put("or", Value.OR);
        ops.bops.put("xor", Value.XOR);
        // meta operators
        ops.mops.put("reduce", Value.REDUCE);
        ops.mops.put("cumurate", Value.CUMULATE);
    }
}
