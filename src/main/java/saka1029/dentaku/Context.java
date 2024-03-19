package saka1029.dentaku;

import java.util.HashMap;
import java.util.Map;

public class Context {
    final Context parent;
    final Functions functions;
    final Map<String, Expression> variables = new HashMap<>();

    private Context(Functions functions, Context parent) {
        this.parent = parent;
        this.functions = functions;
    }

    public static Context of(Functions functions) {
        Context context = new Context(functions, null);
        context.variables.put("PI", c -> Value.PI);
        context.variables.put("E", c -> Value.E);
        return context;
    }

    public Context child() {
        return new Context(functions, this);
    }

    public Functions functions() {
        return functions;
    }

    public Expression variable(String name) {
        Expression e = variables.get(name);
        return e != null ? e : parent != null ? parent.variable(name) : null;
    }

    public void variable(String name, Expression e) {
        variables.put(name, e);
    }
}
