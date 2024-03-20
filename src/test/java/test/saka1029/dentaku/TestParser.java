package test.saka1029.dentaku;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import saka1029.dentaku.Context;
import saka1029.dentaku.Expression;
import saka1029.dentaku.Functions;
import saka1029.dentaku.Parser;
import saka1029.dentaku.Value;

public class TestParser {

    static Expression parse(Functions ops, String input) {
        return Parser.parse(ops, input);
    }

    static Value eval(Context context, String input) {
        return parse(context.functions(), input).eval(context);
    }

    @Test
    public void testToString() {
        Functions ops = Functions.of();
        Context c = Context.of(ops);
        Expression e0 = parse(ops, "  1 + 2 + 3 ");
        assertEquals("1 + 2 + 3", e0.toString());
        assertEquals(eval(c, "6"), e0.eval(c));
        Expression e1 = parse(ops, "  a = 1 + 2 + 3 ");
        assertEquals(Value.NaN, e1.eval(c));
        assertEquals("a = 1 + 2 + 3", c.variable("a").toString());
        Expression e2 = parse(ops, "  a x = 1 + x ");
        assertEquals(Value.NaN, e2.eval(c));
        assertEquals("a x = 1 + x", ops.unary("a").toString());
        Expression e3 = parse(ops, "  x a y = x + y ");
        assertEquals(Value.NaN, e3.eval(c));
        assertEquals("x a y = x + y", ops.binary("a").toString());
    }

    @Test
    public void testNumber() {
        Functions ops = Functions.of();
        Context c = Context.of(ops);
        assertEquals(eval(c, "1"), eval(c, "  1 "));
        assertEquals(eval(c, "1 2"), eval(c, "  1   2 "));
    }

    @Test
    public void testBinary() {
        Functions ops = Functions.of();
        Context c = Context.of(ops);
        assertEquals(eval(c, "3"), eval(c, "  1  + 2"));
        assertEquals(eval(c, "4 6"), eval(c, "1 2 + 3 4"));
        assertEquals(eval(c, "3 5"), eval(c, "1 2 + 3 4 - 1 1"));
    }

    @Test
    public void testUnary() {
        Functions ops = Functions.of();
        Context c = Context.of(ops);
        assertEquals(eval(c, "4"), eval(c, "length 1 2 3 4"));
        assertEquals(eval(c, "-1 -2 -3 -4"), eval(c, "- 1 2 3 4"));
        assertEquals(eval(c, "10"), eval(c, "+ 1 2 3 4"));
        assertEquals(eval(c, "24"), eval(c, "* 1 2 3 4"));
        assertEquals(eval(c, "1 -1 0"), eval(c, "sign 5 -2 0"));
    }

    @Test
    public void testDefineVariable() {
        Functions ops = Functions.of();
        Context c = Context.of(ops);
        assertEquals(Value.NaN, eval(c, "a = 1 2 3"));
        assertEquals(eval(c, "1 2 3"), eval(c, "a"));
        assertEquals(Value.NaN, eval(c, "a = (1 2 3)"));
        assertEquals(eval(c, "1 2 3"), eval(c, "a"));
        assertEquals(Value.NaN, eval(c, "a = - 1 2 3"));
        assertEquals(eval(c, "-1 -2 -3"), eval(c, "a"));
        assertEquals(Value.NaN, eval(c, "b = a + 1"));
        assertEquals(eval(c, "0 -1 -2"), eval(c, "b"));
        assertEquals(Value.NaN, eval(c, "a = 3"));
        assertEquals(eval(c, "4"), eval(c, "b"));
    }

    @Test
    public void testHighOperator() {
        Functions ops = Functions.of();
        Context c = Context.of(ops);
        assertEquals(eval(c, "6"), eval(c, "@ + 1 2 3"));
        assertEquals(eval(c, "1 3 6"), eval(c, "@@ + 1 2 3"));
        assertEquals(eval(c, "24"), eval(c, "@ * 1 2 3 4"));
        assertEquals(eval(c, "1 2 6 24"), eval(c, "@@ * 1 2 3 4"));
    }

    @Test
    public void testTo() {
        Functions ops = Functions.of();
        Context c = Context.of(ops);
        assertEquals(eval(c, "3 4 5"), eval(c, "3 .. 5"));
        assertEquals(eval(c, "5 4 3"), eval(c, "5 .. 3"));
    }

    @Test
    public void testDefineUnary() {
        Functions ops = Functions.of();
        Context c = Context.of(ops);
        assertEquals(Value.NaN, eval(c, "a = 1 .. 4"));
        assertEquals(eval(c, "3 4"), eval(c, "a > 2 filter a"));
        assertEquals(Value.NaN, eval(c, "select-gt2 x = x > 2 filter x"));
        assertEquals(eval(c, "3 4"), eval(c, "select-gt2 a"));
        assertEquals(eval(c, "3 4"), eval(c, "select-gt2 (1 .. 4)"));
        assertEquals(Value.NaN, eval(c, "average x = + x / length x"));
        assertEquals(eval(c, "2.5"), eval(c, "average a"));
        assertEquals(eval(c, "2 4"), eval(c, "not (a % 2) filter a"));
        assertEquals(Value.NaN, eval(c, "even x = not (x % 2) filter x"));
        assertEquals(eval(c, "2 4 6 8 10"), eval(c, "even (1 .. 10)"));
    }

    /**
     * フィボナッチ数列の一般項 
     */
    @Test
    public void testFibonacci() {
        Functions ops = Functions.of();
        Context c = Context.of(ops);
        assertEquals(Value.NaN, eval(c,
            "fib n = 1 + sqrt 5 / 2 ^ n - (1 - sqrt 5 / 2 ^ n) / sqrt 5"));
        assertEquals(eval(c, "0 1 1 2 3 5 8"), eval(c, "int fib (0 .. 6)"));
    }

    /**
     * 円周率を求めるライプニッツの公式
     */
    @Test
    public void testLeibniz() {
        Functions ops = Functions.of();
        Context c = Context.of(ops);
        assertEquals(Value.NaN, eval(c, "pi-term n = -1 ^ n / (2 * n + 1)"));
        assertEquals(Value.NaN, eval(c, "pi-sum range = 4 * + pi-term range"));
        assertEquals(eval(c, "3.2323"), eval(c, "pi-sum (0 .. 10) round 4"));
        assertEquals(eval(c, "3.1515"), eval(c, "pi-sum (0 .. 100) round 4"));
        assertEquals(eval(c, "3.1426"), eval(c, "pi-sum (0 .. 1000) round 4"));
        assertEquals(eval(c, "3.1417"), eval(c, "pi-sum (0 .. 10000) round 4"));
    }

    @Test
    public void testDefineBinary() {
        Functions ops = Functions.of();
        Context c = Context.of(ops);
        assertEquals(Value.NaN, eval(c, "a = 1 .. 4"));
        assertEquals(eval(c, "3 4"), eval(c, "a > 2 filter a"));
        assertEquals(Value.NaN, eval(c, "p select-gt x = x > p filter x"));
        assertEquals(eval(c, "3 4"), eval(c, "2 select-gt a"));
    }
}
