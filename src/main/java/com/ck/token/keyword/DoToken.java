package com.ck.token.keyword;

import com.ck.token.IterationToken;

/**
 * do
 *
 * @author 陈坤
 * 2023/10/2
 */
public class DoToken implements IterationToken {

    public static final DoToken INSTANCE = new DoToken();

    private DoToken() {
    }

    @Override
    public String value() {
        return "do";
    }
}
