package com.ck.token.operator;

import com.ck.token.OperatorToken;

/**
 * 加减法运算符
 *
 * @author 陈坤
 * 2023/10/1
 */
public enum AdditiveOperatorToken implements OperatorToken {

    ADDITION("+"),
    SUBTRACTION("-"),
    ;

    private final String operator;

    AdditiveOperatorToken(String operator) {
        this.operator = operator;
    }

    @Override
    public String value() {
        return this.operator;
    }

}
