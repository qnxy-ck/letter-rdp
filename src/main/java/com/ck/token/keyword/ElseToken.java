package com.ck.token.keyword;

import com.ck.token.Token;

/**
 * @author 陈坤
 * 2023/10/1
 */
public class ElseToken implements Token<String> {

    public static final ElseToken INSTANCE = new ElseToken();

    private ElseToken() {
    }

    @Override
    public String value() {
        return "else";
    }
}
