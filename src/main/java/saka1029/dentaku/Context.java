package saka1029.dentaku;

import java.util.HashMap;
import java.util.Map;

public class Context {
    final Operators operators;
    final Map<String, Expression> variables = new HashMap<>();

    private Context(Operators operators) {
        this.operators = operators;
    }

    public static Context of(Operators operators) {
        return new Context(operators);
    }

    public Operators operators() {
        return operators;
    }

    public Expression variable(String name) {
        return variables.get(name);
    }

    public void variable(String name, Expression e) {
        variables.put(name, e);
    }
}
