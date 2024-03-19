package test.saka1029.dentaku;

import static org.junit.Assert.assertEquals;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.Test;
import saka1029.dentaku.Context;
import saka1029.dentaku.Functions;
import saka1029.dentaku.Parser;
import saka1029.dentaku.Value;

public class TestParser {

    static Value value(double... elements) {
        return Value.of(Arrays.stream(elements)
            .mapToObj(BigDecimal::new)
            .toArray(BigDecimal[]::new));
    }

    static Value eval(Context context, String input) {
        return Parser.parse(context.functions(), input).eval(context);
    }

    @Test
    public void testNumber() {
        Functions ops = Functions.of();
        Context c = Context.of(ops);
        assertEquals(value(1), eval(c, "  1 "));
        assertEquals(value(1, 2), eval(c, "  1   2 "));
    }

    @Test
    public void testBinary() {
        Functions ops = Functions.of();
        Context c = Context.of(ops);
        assertEquals(value(3), eval(c, "  1  + 2"));
        assertEquals(value(4, 6), eval(c, "1 2 + 3 4"));
        assertEquals(value(3, 5), eval(c, "1 2 + 3 4 - 1 1"));
    }

    @Test
    public void testUnary() {
        Functions ops = Functions.of();
        Context c = Context.of(ops);
        assertEquals(value(4), eval(c, "length 1 2 3 4"));
        assertEquals(value(-1, -2, -3, -4), eval(c, "- 1 2 3 4"));
        assertEquals(value(10), eval(c, "+ 1 2 3 4"));
        assertEquals(value(24), eval(c, "* 1 2 3 4"));
        assertEquals(value(1, -1, 0), eval(c, "sign 5 -2 0"));
    }

    @Test
    public void testDefineVariable() {
        Functions ops = Functions.of();
        Context c = Context.of(ops);
        assertEquals(Value.NaN, eval(c, "a = 1 2 3"));
        assertEquals(value(1, 2, 3), eval(c, "a"));
        assertEquals(Value.NaN, eval(c, "a = (1 2 3)"));
        assertEquals(value(1, 2, 3), eval(c, "a"));
        assertEquals(Value.NaN, eval(c, "a = - 1 2 3"));
        assertEquals(value(-1, -2, -3), eval(c, "a"));
        assertEquals(Value.NaN, eval(c, "b = a + 1"));
        assertEquals(value(0, -1, -2), eval(c, "b"));
        assertEquals(Value.NaN, eval(c, "a = 3"));
        assertEquals(value(4), eval(c, "b"));
    }

    @Test
    public void testHighOperator() {
        Functions ops = Functions.of();
        Context c = Context.of(ops);
        assertEquals(value(6), eval(c, "@ + 1 2 3"));
        assertEquals(value(1, 3, 6), eval(c, "@@ + 1 2 3"));
        assertEquals(value(24), eval(c, "@ * 1 2 3 4"));
        assertEquals(value(1, 2, 6, 24), eval(c, "@@ * 1 2 3 4"));
    }

    @Test
    public void testTo() {
        Functions ops = Functions.of();
        Context c = Context.of(ops);
        assertEquals(value(3, 4, 5), eval(c, "3 .. 5"));
        assertEquals(value(5, 4, 3), eval(c, "5 .. 3"));
    }

    @Test
    public void testDefineUnary() {
        Functions ops = Functions.of();
        Context c = Context.of(ops);
        assertEquals(Value.NaN, eval(c, "a = 1 .. 4"));
        assertEquals(value(3, 4), eval(c, "a > 2 filter a"));
        assertEquals(Value.NaN, eval(c, "select-gt2 x = x > 2 filter x"));
        assertEquals(value(3, 4), eval(c, "select-gt2 a"));
        assertEquals(value(3, 4), eval(c, "select-gt2 (1 .. 4)"));
        assertEquals(Value.NaN, eval(c, "average x = + x / length x"));
        assertEquals(value(2.5), eval(c, "average a"));
    }
}
