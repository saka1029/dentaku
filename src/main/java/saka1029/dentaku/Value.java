package saka1029.dentaku;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Value {
    public static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;

    public static final UOP NEG = BigDecimal::negate;
    public static final UOP SIGN = d -> new BigDecimal(d.signum());
    public static final UOP SIN = d -> new BigDecimal(Math.sin(d.doubleValue()), MATH_CONTEXT);
    public static final UOP ASIN = d -> new BigDecimal(Math.asin(d.doubleValue()), MATH_CONTEXT);
    public static final UOP COS = d -> new BigDecimal(Math.cos(d.doubleValue()), MATH_CONTEXT);
    public static final UOP ACOS = d -> new BigDecimal(Math.acos(d.doubleValue()), MATH_CONTEXT);
    public static final UOP TAN = d -> new BigDecimal(Math.tan(d.doubleValue()), MATH_CONTEXT);
    public static final UOP ATAN = d -> new BigDecimal(Math.atan(d.doubleValue()), MATH_CONTEXT);

    public static final BOP ADD = new BOP(BigDecimal::add, BigDecimal.ZERO);
    public static final BOP MINUS = new BOP(BigDecimal::subtract, BigDecimal.ZERO);
    public static final BOP MULT = new BOP(BigDecimal::multiply, BigDecimal.ONE);
    public static final BOP DIV = new BOP((l, r) -> l.divide(r, MATH_CONTEXT), BigDecimal.ONE);
    public static final BOP MOD = new BOP((l, r) -> l.remainder(r, MATH_CONTEXT), BigDecimal.ONE);
    public static final BOP POW = new BOP(
        (l, r) -> new BigDecimal(Math.pow(l.doubleValue(), r.doubleValue()), MATH_CONTEXT), BigDecimal.ONE);

    static BigDecimal bool(boolean b) {
        return b ? BigDecimal.ONE : BigDecimal.ZERO;
    }
    
    public static final BOP EQ = new BOP((l, r) -> bool(l.compareTo(r) == 0), BigDecimal.ZERO);
    public static final BOP NE = new BOP((l, r) -> bool(l.compareTo(r) != 0), BigDecimal.ZERO);
    public static final BOP LT = new BOP((l, r) -> bool(l.compareTo(r) < 0), BigDecimal.ZERO);
    public static final BOP LE = new BOP((l, r) -> bool(l.compareTo(r) <= 0), BigDecimal.ZERO);
    public static final BOP GT = new BOP((l, r) -> bool(l.compareTo(r) > 0), BigDecimal.ZERO);
    public static final BOP GE = new BOP((l, r) -> bool(l.compareTo(r) >= 0), BigDecimal.ZERO);

    private final BigDecimal[] elements;

    Value(BigDecimal... elements) {
        this.elements = elements;
    }

    public static Value of(BigDecimal... elements) {
        return new Value(elements.clone());
    }

    public Value map(UOP operator) {
        return new Value(Arrays.stream(elements)
            .map(e -> operator.apply(e))
            .toArray(BigDecimal[]::new));
    }

    public Value reduce(BinaryOperator<BigDecimal> operator, BigDecimal unit) {
        BigDecimal result = unit;
        for (BigDecimal e : elements)
            result = operator.apply(result, e);
        return new Value(result);
    }

    public Value reduce(BOP operator) {
        return reduce(operator.operator(), operator.unit());
    }

    public Value cumulate(BinaryOperator<BigDecimal> operator, BigDecimal unit) {
        BigDecimal temp = unit;
        List<BigDecimal> result = new ArrayList<>();
        for (BigDecimal e : elements)
            result.add(temp = operator.apply(temp, e));
        return new Value(result.toArray(BigDecimal[]::new));
    }

    public Value cumulate(BOP operator) {
        return cumulate(operator.operator(), operator.unit());
    }

    public Value binary(BinaryOperator<BigDecimal> operator, Value right) {
                if (elements.length == 1) 
            return new Value(Arrays.stream(right.elements) 
                .map(e -> operator.apply(elements[0], e)) 
                .toArray(BigDecimal[]::new)); 
        else if (right.elements.length == 1) 
            return new Value(Arrays.stream(elements) 
                .map(e -> operator.apply(e, right.elements[0])) 
                .toArray(BigDecimal[]::new)); 
        else if (right.elements.length == elements.length) 
            return new Value(IntStream.range(0, elements.length) 
                .mapToObj(i -> operator.apply(elements[i], right.elements[i])) 
                .toArray(BigDecimal[]::new)); 
        else 
            throw new ValueException("Length mismatch %d, %d", elements.length, right.elements.length);
    }

    public Value binary(BOP operator, Value right) {
        return binary(operator.operator(), right);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(elements);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Value v && Arrays.equals(elements, v.elements);
    }

    @Override
    public String toString() {
        return Arrays.stream(elements)
            .map(d -> d.toString())
            .collect(Collectors.joining(" "));
    }
}
