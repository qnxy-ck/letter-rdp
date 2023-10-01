package com.ck.token.keyword;

import com.ck.token.Token;

/**
 * def
 *
 * @author 陈坤
 * 2023/10/2
 */
public class DefToken implements Token<String> {

    public static final DefToken INSTANCE = new DefToken();

    private DefToken() {
    }

    @Override
    public String value() {
        return "def";
    }
}
