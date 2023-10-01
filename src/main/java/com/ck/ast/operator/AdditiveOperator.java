package com.ck.ast.operator;

import com.ck.ast.Operator;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author 陈坤
 * 2023/10/1
 */
public enum AdditiveOperator implements Operator {

    ADDITION("+"),
    SUBTRACTION("-"),

    ;

    private final String operator;

    AdditiveOperator(String operator) {
        this.operator = operator;
    }

    public static Optional<AdditiveOperator> operatorOf(String operator) {
        return Arrays.stream(values())
                .filter(it -> it.operator.equals(operator))
                .findFirst();
    }

    @Override
    public String operator() {
        return this.operator;
    }
}
