package com.ck.token;

/**
 * 右括号
 * 
 * @author 陈坤
 * 2023/10/1
 */
public class ClosedParenthesisToken implements Token<String>{
    
    public static final ClosedParenthesisToken INSTANCE = new ClosedParenthesisToken();

    private ClosedParenthesisToken() {
    }

    @Override
    public String value() {
        return ")";
    }
}
