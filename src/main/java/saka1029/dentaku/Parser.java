package saka1029.dentaku;

import java.util.List;
import saka1029.dentaku.Tokenizer.Token;
import saka1029.dentaku.Tokenizer.Type;

public class Parser {
    final List<Token> tokens;
    int index;
    Token token;

    Parser(String input) {
        this.tokens = Tokenizer.tokens(input);
        this.index = 0;
        get();
    }

    Token get() {
        return token = index < tokens.size() ? tokens.get(index++) : Tokenizer.END;
    }

    Token peek(int offset) {
        int p = index + offset;
        return p < tokens.size() ? tokens.get(p) : Tokenizer.END;
    }

    boolean is(Token token, Type type) {
        return token.type() == type;
    }

    boolean is(Token token, Type type, String string) {
        return token.type() == type && token.string().equals(string);
    }

    Expression defineVariable() {

    }

    Expression defineUnary() {

    }

    Expression defineBinary() {

    }

    Expression expression() {

    }

    public Expression statement() {
        if (is(token, Type.END))
            return null;
        if (is(peek(0), Type.SPECIAL, "="))
            return defineVariable();
        if (is(peek(1), Type.SPECIAL, "="))
            return defineUnary();
        if (is(peek(2), Type.SPECIAL, "="))
            return defineBinary();
        else
            return expression();
    }
}
