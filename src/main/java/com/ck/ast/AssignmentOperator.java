package com.ck.ast;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Optional;

/**
 * 赋值运算符
 *
 * @author 陈坤
 * 2023/10/1
 */
public enum AssignmentOperator {

    SIMPLE_ASSIGN("="),
    MUL_ASSIGN("*="),
    DIV_ASSIGN("/="),
    ADD_ASSIGN("+="),
    SUB_ASSIGN("-="),

    ;
    @JsonValue
    public final String operator;

    AssignmentOperator(String operator) {
        this.operator = operator;
    }

    public static Optional<AssignmentOperator> operatorOf(String operator) {
        return Arrays.stream(values())
                .filter(it -> it.operator.equals(operator))
                .findFirst();
    }
}