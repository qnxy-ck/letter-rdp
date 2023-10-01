package com.ck.token.keyword;

import com.ck.token.Token;

/**
 * null
 *
 * @author 陈坤
 * 2023/10/1
 */
public class NullToken implements Token<String> {

    public static final NullToken INSTANCE = new NullToken();

    private NullToken() {
    }

    @Override
    public String value() {
        return "null";
    }
}
