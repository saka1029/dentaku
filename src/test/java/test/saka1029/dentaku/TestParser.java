package test.saka1029.dentaku;

import static org.junit.Assert.assertEquals;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.Test;
import saka1029.dentaku.Context;
import saka1029.dentaku.Operators;
import saka1029.dentaku.Parser;
import saka1029.dentaku.Value;

public class TestParser {

    static Value value(double... elements) {
        return Value.of(Arrays.stream(elements)
            .mapToObj(BigDecimal::new)
            .toArray(BigDecimal[]::new));
    }

    static Value eval(Context context, String input) {
        return Parser.parse(context.operators(), input).eval(context);
    }

    @Test
    public void testNumber() {
        Operators ops = Operators.of();
        Context c = Context.of(ops);
        assertEquals(value(1), eval(c, "  1 "));
        assertEquals(value(1, 2), eval(c, "  1   2 "));
    }

    @Test
    public void testBinary() {
        Operators ops = Operators.of();
        Context c = Context.of(ops);
        assertEquals(value(3), eval(c, "  1  + 2"));
        assertEquals(value(4, 6), eval(c, "1 2 + 3 4"));
        assertEquals(value(3, 5), eval(c, "1 2 + 3 4 - 1 1"));
    }

    @Test
    public void testUnary() {
        Operators ops = Operators.of();
        Context c = Context.of(ops);
        assertEquals(value(-1, -2, -3), eval(c, "- 1 2 3"));
        assertEquals(value(1, 2, 3), eval(c, "+ 1 2 3"));
    }
}
