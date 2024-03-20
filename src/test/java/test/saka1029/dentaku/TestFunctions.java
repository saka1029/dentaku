package test.saka1029.dentaku;

import static org.junit.Assert.assertEquals;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.Test;
import saka1029.dentaku.Context;
import saka1029.dentaku.Functions;
import saka1029.dentaku.Parser;
import saka1029.dentaku.Value;

public class TestFunctions {

    static Value value(double... elements) {
        return Value.of(Arrays.stream(elements)
            .mapToObj(BigDecimal::new)
            .toArray(BigDecimal[]::new));
    }

    static Value eval(Context context, String input) {
        return Parser.parse(context.functions(), input).eval(context);
    }

    @Test
    public void testUnaryOperators() {
        Functions f = Functions.of();
        Context c = Context.of(f);
        assertEquals(value(-1, -2, -3), eval(c, "- 1 2 3"));
        assertEquals(value(6), eval(c, "+ 1 2 3"));
        assertEquals(value(24), eval(c, "* 1 2 3 4"));
        assertEquals(value(1, 1, 1, 1), eval(c, "sin 0 (PI / 2) (-PI / 2) PI ~ 0 1 -1 0"));
        assertEquals(value(1, 1, 1, 1), eval(c, "cos 0 (PI / 2) (-PI / 2) PI ~ 1 0 0 -1"));
        assertEquals(value(1, 1, 1, 1), eval(c, "tan 0 (PI / 4) (-PI / 4) PI ~ 0 1 -1 0"));
        assertEquals(value(1, 1, 1, 1), eval(c, "asin 0 1 -1 0 ~ 0 (PI / 2) (-PI / 2) 0"));
        assertEquals(value(1, 1, 1, 1), eval(c, "acos 1 0 0 -1 ~ 0 (PI / 2) (PI / 2) PI"));
        assertEquals(value(1, 1, 1, 1), eval(c, "atan 0 1 -1 0 ~ 0 (PI / 4) (-PI / 4) 0"));
        assertEquals(value(0, 1, 2, 3), eval(c, "log (E ^ (0 .. 3))"));
        assertEquals(value(0, 1, 2, 3), eval(c, "log10 (10 ^ (0 .. 3))"));
        assertEquals(value(-1, 1, 0), eval(c, "sign -1 2 0"));
        assertEquals(value(0, 1), eval(c, "not -1 0"));
    }

    @Test
    public void testBinaryArithmeticOperators() {
        Functions f = Functions.of();
        Context c = Context.of(f);
        assertEquals(value(5, 7, 9), eval(c, "1 2 3 + 4 5 6"));
        assertEquals(value(5, 6, 7), eval(c, "1 2 3 + 4"));
        assertEquals(value(5, 6, 7), eval(c, "4 + 1 2 3"));
        assertEquals(value(-3, -3, -3), eval(c, "1 2 3 - 4 5 6"));
        assertEquals(value(-3, -2, -1), eval(c, "1 2 3 - 4"));
        assertEquals(value(3, 2, 1), eval(c, "4 - 1 2 3"));
        assertEquals(value(6, 3, 2, 1), eval(c, "6 / 1 2 3 6"));
        assertEquals(value(6, 1.5, 0.5, 0.125), eval(c, "6 3 2 1 / 1 2 4 8"));
        assertEquals(value(0.5, 1, 1.5, 3), eval(c, "1 2 3 6 / 2"));
        assertEquals(value(0, 0, 0, 0), eval(c, "6 % 1 2 3 6"));
        assertEquals(value(1, 0, 0, 4), eval(c, "1 2 3 4 % 3 2 1 5"));
        assertEquals(value(1, 0, 1, 0), eval(c, "1 2 3 6 % 2"));
        assertEquals(value(1, 2, 3, 5), eval(c, "1.2 2.3 3.4 4.5 round 0"));
        assertEquals(value(1, 1, 1, 1), eval(c, "1.23 2.34 3.45 4.56 round 1 == 1.2 2.3 3.5 4.6"));
        assertEquals(value(8, 9, 10), eval(c, "8 .. 10"));
        assertEquals(value(8), eval(c, "8 .. 8"));
        assertEquals(value(10, 9, 8), eval(c, "10 .. 8"));
        assertEquals(value(1, 4, 9, 16), eval(c, "1 .. 4 ^ 2"));
        assertEquals(value(2, 4, 8, 16), eval(c, "2 ^ (1 .. 4)"));
        assertEquals(value(1), eval(c, "1 min 4"));
        assertEquals(value(1, 2, 1), eval(c, "1 2 3 min 3 2 1"));
        assertEquals(value(4), eval(c, "1 max 4"));
        assertEquals(value(3, 2, 3), eval(c, "1 2 3 max 3 2 1"));
        assertEquals(value(1, 3), eval(c, "1 0 1 0 filter (1 .. 4)"));
        assertEquals(Value.NaN, eval(c, "a = 1 .. 4"));
        assertEquals(value(0, 0, 1, 1), eval(c, "a > 2"));
        assertEquals(value(3, 4), eval(c, "0 0 1 1 filter a"));
        assertEquals(value(3, 4), eval(c, "a > 2 filter a"));
        assertEquals(value(), eval(c, "a > 9 filter a"));
        // https://ja.wikipedia.org/wiki/%E3%83%95%E3%82%A3%E3%83%9C%E3%83%8A%E3%83%83%E3%83%81%E6%95%B0#%E4%B8%80%E8%88%AC%E9%A0%85
        assertEquals(Value.NaN, eval(c, "fib n = 1 + sqrt 5 / 2 ^ n - (1 - sqrt 5 / 2 ^ n) / sqrt 5"));
        assertEquals(value(0, 1, 1, 2, 3, 5, 8), eval(c, "int fib (0 .. 6)"));
    }

    @Test
    public void testBinaryCompareOperators() {
        Functions f = Functions.of();
        Context c = Context.of(f);
        assertEquals(value(0, 1, 0), eval(c, "0 == -1 0 1"));
        assertEquals(value(1, 0, 1), eval(c, "0 != -1 0 1"));
        assertEquals(value(0, 0, 1), eval(c, "0 < -1 0 1"));
        assertEquals(value(0, 1, 1), eval(c, "0 <= -1 0 1"));
        assertEquals(value(1, 0, 0), eval(c, "0 > -1 0 1"));
        assertEquals(value(1, 1, 0), eval(c, "0 >= -1 0 1"));
    }

    @Test
    public void testBinaryLogicalOperators() {
        Functions f = Functions.of();
        Context c = Context.of(f);
        assertEquals(value(1, 0, 0, 0), eval(c, "1 1 0 0 and 1 0 1 0"));
        assertEquals(value(1, 1, 1, 0), eval(c, "1 1 0 0 or  1 0 1 0"));
        assertEquals(value(0, 1, 1, 0), eval(c, "1 1 0 0 xor 1 0 1 0"));
    }

    @Test
    public void testHighOrderOperators() {
        Functions f = Functions.of();
        Context c = Context.of(f);
        assertEquals(value(55), eval(c, "@ + (1 .. 10)"));
        assertEquals(value(55), eval(c, "+ (1 .. 10)"));
        assertEquals(value(1, 3, 6, 10), eval(c, "@@ + (1 .. 4)"));
        assertEquals(value(1, 2, 6, 24, 120), eval(c, "@@ * (1 .. 5)"));
        assertEquals(value(-2), eval(c, "@ min 1 3 9 -2 0"));
        assertEquals(value(1, 1, 1, -2, -2), eval(c, "@@ min 1 3 9 -2 0"));
        assertEquals(value(9), eval(c, "@ max 1 3 9 -2 0"));
        assertEquals(value(1, 3, 9, 9, 9), eval(c, "@@ max 1 3 9 -2 0"));
    }
}