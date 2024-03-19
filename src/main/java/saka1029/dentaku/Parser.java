package saka1029.dentaku;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
 * define-unary    = IDSPECIAL ID '=' expression
 * define-binary   = ID IDSPECIAL ID '=' expression
 * expression      = unary { BOP unary }
 * unary           = sequence
 *                 | UOP unary
 *                 | MOP UOP unary'
 * sequence        = primary { primary }
 * primary         = '(' expression ')'
 *                 | VAR
 *                 | NUMBER { NUMBER }
 * </pre>
 */
public class Parser {
    final Functions functions;
    final List<Token> tokens;
    int index;
    Token token;

    private Parser(Functions functions, String input) {
        this.functions = functions;
        this.tokens = Tokenizer.tokens(input);
        this.index = 0;
        get();
    }

    public static Parser of(Functions functions, String input) {
        return new Parser(functions, input);
    }

    public static Expression parse(Functions functions, String input) {
        Parser parser = of(functions, input);
        Expression result = parser.statement();
        if (parser.token.type() != Type.END)
            throw new ValueException("Extra tokens '%s'", parser.token.string());
        return result;
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

    Unary unary(Token token) {
        if (!is(token, Type.ID, Type.SPECIAL))
            return null;
        return functions.uops.get(token.string());
    }

    Binary binary(Token token) {
        if (!is(token, Type.ID, Type.SPECIAL))
            return null;
        return functions.bops.get(token.string());
    }

    High high(Token token) {
        if (!is(token, Type.ID, Type.SPECIAL))
            return null;
        return functions.hops.get(token.string());
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
            List<BigDecimal> list = new ArrayList<>();
            do {
                list.add(token.number());
                get(); // skip NUMBER
            } while (is(token, Type.NUMBER));
            e = Value.of(list);
        } else
            throw new ValueException("Unknown token '%s'", token.string());
        return e;
    }

    boolean isPrimary(Token token) {
        return is(token, Type.LP, Type.NUMBER)
            || is(token, Type.ID)
                && unary(token) == null
                && binary(token) == null
                && high(token) == null;
    }

    Expression sequence() {
        Expression e = primary();
        while (isPrimary(token)) {
            Expression left = e, right = primary();
            e = c -> left.eval(c).append(right.eval(c));
        }
        return e;
    }

    Expression unary() {
        High high;
        Unary unary;
        if ((high = high(token)) != null) {
            String highName = token.string();
            get();  // skip MOP
            Binary binary;
            if ((binary = binary(token)) != null) {
                get();  // skip BOP
                Expression e = unary();
                return c -> high.apply(e.eval(c), binary);
            } else
                throw new ValueException("BOP expected after '%s'", highName);
        } else if ((unary = unary(token)) != null) {
            get();  // skip UOP
            Expression e = unary();
            return c -> unary.apply(e.eval(c));
        } else
            return sequence();
    }

    Expression expression() {
        Expression e = unary();
        Binary b;
        while ((b = binary(token)) != null) {
            get();  // skip BOP
            Binary binary = b;
            Expression left = e, right = unary();
            e = c -> binary.apply(left.eval(c), right.eval(c));
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
