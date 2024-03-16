package test.saka1029.dentaku;

import static org.junit.Assert.assertEquals;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.Test;
import saka1029.dentaku.BOP;
import saka1029.dentaku.Value;

public class TestValue {

    static BigDecimal dec(double element) {
        return new BigDecimal(element);
    }

    static Value value(double... elements) {
        return Value.of(Arrays.stream(elements)
            .mapToObj(BigDecimal::new)
            .toArray(BigDecimal[]::new));
    }

    @Test
    public void testOf() {
        Value v123 = Value.of(dec(1), dec(2), dec(3));
        assertEquals(value(1, 2, 3), v123);
    }

    @Test
    public void testMap() {
        Value v = value();
        Value v1234 = value(1, 2, 3, 4);
        assertEquals(value(), v.map(BigDecimal::negate));
        assertEquals(value(-1, -2, -3, -4), v1234.map(BigDecimal::negate));
    }

    @Test
    public void testReduce() {
        Value v = value();
        Value v1234 = value(1, 2, 3, 4);
        assertEquals(value(0), v.reduce(BigDecimal::add, BigDecimal.ZERO));
        assertEquals(value(10), v1234.reduce(BigDecimal::add, BigDecimal.ZERO));
        assertEquals(value(1), v.reduce(BigDecimal::multiply, BigDecimal.ONE));
        assertEquals(value(24), v1234.reduce(BigDecimal::multiply, BigDecimal.ONE));
    }

    @Test
    public void testCumulate() {
        Value v = value();
        Value v1234 = value(1, 2, 3, 4);
        assertEquals(value(), v.cumulate(BigDecimal::add, BigDecimal.ZERO));
        assertEquals(value(1, 3, 6, 10), v1234.cumulate(BigDecimal::add, BigDecimal.ZERO));
        assertEquals(value(), v.cumulate(BigDecimal::multiply, BigDecimal.ONE));
        assertEquals(value(1, 2, 6, 24), v1234.cumulate(BigDecimal::multiply, BigDecimal.ONE));
    }

    static final BOP ADD = new BOP(BigDecimal::add, BigDecimal.ZERO);
    static final BOP MULT = new BOP(BigDecimal::multiply, BigDecimal.ONE);

    @Test
    public void testReduceBOP() {
        Value v = value();
        Value v1234 = value(1, 2, 3, 4);
        assertEquals(value(0), v.reduce(ADD));
        assertEquals(value(10), v1234.reduce(ADD));
        assertEquals(value(1), v.reduce(MULT));
        assertEquals(value(24), v1234.reduce(MULT));
    }

    @Test
    public void testCumulateBOP() {
        Value v = value();
        Value v1234 = value(1, 2, 3, 4);
        assertEquals(value(), v.cumulate(ADD));
        assertEquals(value(1, 3, 6, 10), v1234.cumulate(ADD));
        assertEquals(value(), v.cumulate(MULT));
        assertEquals(value(1, 2, 6, 24), v1234.cumulate(MULT));
    }

    @Test
    public void testBinary() {
        Value v2 = value(2);
        Value v1234 = value(1, 2, 3, 4);
        Value v5678 = value(5, 6, 7, 8);
        assertEquals(value(3, 4, 5, 6), v1234.binary(BigDecimal::add, v2));
        assertEquals(value(3, 4, 5, 6), v2.binary(BigDecimal::add, v1234));
        assertEquals(value(6, 8, 10, 12), v1234.binary(BigDecimal::add, v5678));
        assertEquals(value(2, 4, 6, 8), v1234.binary(BigDecimal::multiply, v2));
        assertEquals(value(2, 4, 6, 8), v2.binary(BigDecimal::multiply, v1234));
        assertEquals(value(5, 12, 21, 32), v1234.binary(BigDecimal::multiply, v5678));
    }

}
