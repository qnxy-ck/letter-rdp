package com.ck.token.operator;

import com.ck.token.OperatorToken;

/**
 * ||
 *
 * @author 陈坤
 * 2023/10/1
 */
public class LogicalOrToken implements OperatorToken {

    public static final LogicalOrToken INSTANCE = new LogicalOrToken();

    private LogicalOrToken() {
    }

    @Override
    public String value() {
        return "||";
    }
}