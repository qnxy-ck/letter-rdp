package com.ck.token.symbol;

import com.ck.token.Token;

/**
 * 左括号
 *
 * @author 陈坤
 * 2023/10/1
 */
public class OpenParenthesisToken implements Token<String> {

    public static final OpenParenthesisToken INSTANCE = new OpenParenthesisToken();

    private OpenParenthesisToken() {
    }

    @Override
    public String value() {
        return "(";
    }
}
