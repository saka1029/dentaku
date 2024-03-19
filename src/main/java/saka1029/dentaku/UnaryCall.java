package saka1029.dentaku;

public record UnaryCall(String variable, Expression body) implements Unary {

    @Override
    public Value apply(Context context, Value argument) {
        Context child = context.child();
        child.variable(variable, argument);
        return body.eval(child);
    }
}
