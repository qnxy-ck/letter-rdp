package com.ck.token.keyword;

import com.ck.token.Token;

/**
 * let
 *
 * @author 陈坤
 * 2023/10/1
 */
public class LetToken implements Token<String> {

    public static final LetToken INSTANCE = new LetToken();

    private LetToken() {
    }

    @Override
    public String value() {
        return "let";
    }
}
