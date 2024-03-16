package saka1029.dentaku;

import java.math.BigDecimal;
import java.util.function.BinaryOperator;

public record BOP(BinaryOperator<BigDecimal> operator, BigDecimal unit) {
}
