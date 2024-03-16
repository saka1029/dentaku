package saka1029.dentaku;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Value {
    private final BigDecimal[] elements;

    Value(BigDecimal... elements) {
        this.elements = elements;
    }

    public static Value of(BigDecimal... elements) {
        return new Value(elements.clone());
    }

    public Value map(UnaryOperator<BigDecimal> operator) {
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
