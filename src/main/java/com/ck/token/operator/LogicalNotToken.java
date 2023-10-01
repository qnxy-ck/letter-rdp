package com.ck.token.operator;

import com.ck.token.OperatorToken;

/**
 * !
 *
 * @author 陈坤
 * 2023/10/1
 */
public class LogicalNotToken implements OperatorToken {

    public static final LogicalNotToken INSTANCE = new LogicalNotToken();

    private LogicalNotToken() {
    }

    @Override
    public String value() {
        return "!";
    }
}