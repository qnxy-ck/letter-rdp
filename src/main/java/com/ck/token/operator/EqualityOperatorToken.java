package com.ck.token.operator;

import com.ck.token.OperatorToken;

/**
 * 等式运算符
 * ==
 * !=
 *
 * @author 陈坤
 * 2023/10/1
 */
public enum EqualityOperatorToken implements OperatorToken {

    EQUALITY("=="),
    NOT_EQUALITY("!="),
    ;

    private final String operator;

    EqualityOperatorToken(String operator) {
        this.operator = operator;
    }

    @Override
    public String value() {
        return this.operator;
    }

}
