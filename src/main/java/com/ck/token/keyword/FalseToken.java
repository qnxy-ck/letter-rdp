package com.ck.token.keyword;

import com.ck.token.BooleanLiteralToken;

/**
 * false
 *
 * @author 陈坤
 * 2023/10/1
 */
public class FalseToken implements BooleanLiteralToken {

    public static final FalseToken INSTANCE = new FalseToken();

    private FalseToken() {
    }

    @Override
    public Boolean value() {
        return false;
    }
}
