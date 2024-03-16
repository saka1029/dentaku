package saka1029.dentaku;

import java.math.BigDecimal;
import java.util.function.BinaryOperator;

public record BOP(BinaryOperator<BigDecimal> operator, BigDecimal unit)
    implements BinaryOperator<BigDecimal> {

    @Override
    public BigDecimal apply(BigDecimal arg0, BigDecimal arg1) {
        return operator.apply(arg0, arg1);
    }
}
