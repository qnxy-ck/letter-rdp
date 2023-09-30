package com.ck.ast;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author 陈坤
 * 2023/10/1
 */
public enum BinaryOperator {

    ADDITION("+"),
    SUBTRACTION("-"),
    MULTIPLICATION("*"),
    DIVISION("/"),
    ;

    @JsonValue
    public final String operator;

    BinaryOperator(String operator) {
        this.operator = operator;
    }

    public static Optional<BinaryOperator> operatorOf(String operator) {
        return Arrays.stream(values())
                .filter(it -> it.operator.equals(operator))
                .findFirst();
    }
}
