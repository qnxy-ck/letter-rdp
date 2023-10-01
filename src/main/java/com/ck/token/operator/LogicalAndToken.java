package com.ck.token.operator;

import com.ck.token.OperatorToken;

/**
 * &&
 *
 * @author 陈坤
 * 2023/10/1
 */
public class LogicalAndToken implements OperatorToken {

    public static final LogicalAndToken INSTANCE = new LogicalAndToken();

    private LogicalAndToken() {
    }

    @Override
    public String value() {
        return "&&";
    }
}