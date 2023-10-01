package com.ck.token.operator;

import com.ck.token.OperatorToken;

/**
 * 乘除法运算符
 *
 * @author 陈坤
 * 2023/10/1
 */
public enum MultiplicativeOperatorToken implements OperatorToken {

    MULTIPLICATION("*"),
    DIVISION("/"),
    ;

    private final String operator;

    MultiplicativeOperatorToken(String operator) {
        this.operator = operator;
    }


    @Override
    public String value() {
        return this.operator;
    }

}
