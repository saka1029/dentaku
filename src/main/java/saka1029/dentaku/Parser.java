package saka1029.dentaku;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import saka1029.dentaku.Tokenizer.Token;
import saka1029.dentaku.Tokenizer.Type;

public class Parser {
    final Operators operators;
    final List<Token> tokens;
    int index;
    Token token;

    private Parser(Operators operators, String input) {
        this.operators = operators;
        this.tokens = Tokenizer.tokens(input);
        this.index = 0;
        get();
    }

    public static Parser of(Operators operators, String input) {
        return new Parser(operators, input);
    }

    public static Expression parse(Operators operators, String input) {
        Parser parser = of(operators, input);
        return parser.statement();
    }

    Token get() {
        return token = index < tokens.size() ? tokens.get(index++) : Tokenizer.END;
    }

    Token peek(int offset) {
        int p = index + offset;
        return p < tokens.size() ? tokens.get(p) : Tokenizer.END;
    }

    boolean is(Token token, Type... types) {
        for (Type t : types)
            if (token.type() == t)
                return true;
        return false;
    }

    boolean is(Token token, String string) {
        return token.string().equals(string);
    }

    Expression defineVariable() {
        if (!is(token, Type.ID))
            throw new ValueException("ID expected but '%s'", token.string());
        String name = token.string();
        get(); // skip ID
        Expression e = expression();
        return c -> { c.variable(name, e); return Value.NaN; };
    }

    Expression defineUnary() {
        return null;
    }

    Expression defineBinary() {
        return null;
    }

    Expression primary() {
        Expression e;
        if (is(token, Type.LP)) {
            get(); // skip '('
            e = expression();
            if (!is(token, Type.RP))
                throw new ValueException("')' expected");
            get(); // skip ')'
        } else if (is(token, Type.ID)) {
            e = Variable.of(token.string());
        } else if (is(token, Type.NUMBER)) {
            List<BigDecimal> elements = new ArrayList<>();
            do {
                elements.add(token.number());
                get(); // skip NUMBER
            } while (is(token, Type.NUMBER));
            e = Value.of(elements.toArray(BigDecimal[]::new));
        } else
            throw new ValueException("Unknown token '%s'", token.string());
        return e;
    }

    Expression sequence() {
        Expression e = primary();
        while (is(token, Type.LP, Type.ID, Type.NUMBER)) {
            Expression left = e, right = primary();
            e = c -> left.eval(c).append(right.eval(c));
        }
        return e;
    }

    Expression unary() {
        UOP uop;
        if (is(token, Type.ID, Type.SPECIAL) && (uop = operators.uops.get(token.string())) != null) {
            get();  // skip UOP
            Expression e = unary();
            return c -> e.eval(c).map(uop);
        } else
            return sequence();
    }

    Expression expression() {
        Expression e = unary();
        while (is(token, Type.ID, Type.SPECIAL)) {
            BOP bop = operators.bops.get(token.string());
            if (bop == null)
                break;
            get();  // skip BOP
            Expression left = e, right = expression();
            return c -> left.eval(c).binary(bop, right.eval(c));
        }
        return e;
    }

    public Expression statement() {
        if (is(token, Type.END))
            return null;
        if (is(peek(0), "="))
            return defineVariable();
        if (is(peek(1), "="))
            return defineUnary();
        if (is(peek(2), "="))
            return defineBinary();
        else
            return expression();
    }
}
