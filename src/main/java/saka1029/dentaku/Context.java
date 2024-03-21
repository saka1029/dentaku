package saka1029.dentaku;

import java.util.HashMap;
import java.util.Map;

public class Context {
    final Context parent;
    final Functions functions;
    final Map<String, Str<Expression>> variables = new HashMap<>();

    private Context(Functions functions, Context parent) {
        this.parent = parent;
        this.functions = functions;
    }

    public static Context of(Functions functions) {
        Context context = new Context(functions, null);
        context.variable("PI", c -> Value.PI, "PI = " + Value.PI);
        context.variable("E", c -> Value.E, "E = " + Value.E);
        return context;
    }

    public Context child() {
        return new Context(functions, this);
    }

    public Functions functions() {
        return functions;
    }

    public Expression variable(String name) {
        Str<Expression> e = variables.get(name);
        return e != null ? e.op : parent != null ? parent.variable(name) : null;
    }

    public void variable(String name, Expression e, String string) {
        variables.put(name, Str.of(e, string));
    }

}
