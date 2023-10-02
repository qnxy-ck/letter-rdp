package com.ck.token.keyword;

import com.ck.token.Token;

/**
 * super
 *
 * @author 陈坤
 * 2023/10/2
 */
public class SuperToken implements Token<String> {

    public static final SuperToken INSTANCE = new SuperToken();

    private SuperToken() {
    }

    @Override
    public String value() {
        return "super";
    }
}
