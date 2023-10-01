package com.ck.token.keyword;

import com.ck.token.Token;

/**
 * false
 *
 * @author 陈坤
 * 2023/10/1
 */
public class FalseToken implements Token<Boolean> {

    public static final FalseToken INSTANCE = new FalseToken();

    private FalseToken() {
    }

    @Override
    public Boolean value() {
        return false;
    }
}
