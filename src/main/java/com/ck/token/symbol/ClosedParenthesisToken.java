package com.ck.token.symbol;

import com.ck.token.Token;

/**
 * 右括号
 * 
 * @author 陈坤
 * 2023/10/1
 */
public class ClosedParenthesisToken implements Token<String> {
    
    public static final ClosedParenthesisToken INSTANCE = new ClosedParenthesisToken();

    private ClosedParenthesisToken() {
    }

    @Override
    public String value() {
        return ")";
    }
}
