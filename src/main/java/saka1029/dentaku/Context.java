package saka1029.dentaku;

import java.util.HashMap;
import java.util.Map;

public class Context {
    final Functions functions;
    final Map<String, Expression> variables = new HashMap<>();

    private Context(Functions functions) {
        this.functions = functions;
    }

    public static Context of(Functions functions) {
        return new Context(functions);
    }

    public Functions functions() {
        return functions;
    }

    public Expression variable(String name) {
        return variables.get(name);
    }

    public void variable(String name, Expression e) {
        variables.put(name, e);
    }
}
