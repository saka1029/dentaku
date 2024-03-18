package saka1029.dentaku;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import saka1029.dentaku.Tokenizer.Token;
import saka1029.dentaku.Tokenizer.Type;

/**
 * SYNTAX
 * <pre>
 * statement       = define-variable
 *                 | define-unary
 *                 | define-binary
 *                 | expression
 * define-variable = ID '=' expression
 * define-unary    = ID ID '=' expression
 * define-binary   = ID ID ID '=' expression
 * expression      = unary { BOP unary }
 * unary           = sequence
 *                 | UOP unary
 *                 | MOP UOP unary'
 * sequence        = primary { primary }
 * primary         = '(' expression ')'
 *                 | ID
 *                 | NUMBER { NUMBER }
 * BOP             = ID | SPECIAL
 * UOP             = ID | SPECIAL
 * </pre>
 */
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
        get(); // skip '='
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
            get(); // ski ID
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
        MOP mop;
        UOP uop;
        if (is(token, Type.ID, Type.SPECIAL) && (mop = operators.mops.get(token.string())) != null) {
            String mopName = token.string();
            get();  // skip MOP
            BOP bop;
            if (is(token, Type.ID, Type.SPECIAL) && (bop = operators.bops.get(token.string())) != null) {
                get();  // skip BOP
                Expression e = unary();
                return c -> mop.apply(e.eval(c), bop);
            } else
                throw new ValueException("BOP expected after '%s'", mopName);
        } else if (is(token, Type.ID, Type.SPECIAL) && (uop = operators.uops.get(token.string())) != null) {
            get();  // skip UOP
            Expression e = unary();
            return c -> e.eval(c).map(uop);
        } else
            return sequence();
    }

    Expression expression() {
        Expression e = unary();
        while (is(token, Type.ID, Type.SPECIAL)) {
            BinaryOperator<Value> bin;
            BOP bop;
            if ((bin = operators.bins.get(token.string())) != null) {
                get();  // skip BIN
                Expression left = e, right = expression();
                return c -> bin.apply(left.eval(c), right.eval(c));
            } else if ((bop = operators.bops.get(token.string())) != null) {
                get();  // skip BOP
                Expression left = e, right = expression();
                return c -> left.eval(c).binary(bop, right.eval(c));
            } else
                break;
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
