package com.ck.token.operator;

import com.ck.token.OperatorToken;

/**
 * 关系运算符
 * >
 * >=
 * <
 * <=
 *
 * @author 陈坤
 * 2023/10/1
 */
public enum RelationalOperatorToken implements OperatorToken {

    LT("<"),
    LE("<="),
    GT(">"),
    GE(">="),

    ;
    private final String operator;

    RelationalOperatorToken(String operator) {
        this.operator = operator;
    }

    @Override
    public String value() {
        return this.operator;
    }
}
