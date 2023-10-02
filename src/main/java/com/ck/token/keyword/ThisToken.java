package com.ck.token.keyword;

import com.ck.token.Token;

/**
 * this
 *
 * @author 陈坤
 * 2023/10/2
 */
public class ThisToken implements Token<String> {

    public static final ThisToken INSTANCE = new ThisToken();

    private ThisToken() {
    }

    @Override
    public String value() {
        return "this";
    }
}
