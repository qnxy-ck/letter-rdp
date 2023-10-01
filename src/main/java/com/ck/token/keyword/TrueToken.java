package com.ck.token.keyword;

import com.ck.token.BooleanLiteralToken;

/**
 * true
 *
 * @author 陈坤
 * 2023/10/1
 */
public class TrueToken implements BooleanLiteralToken {

    public static final TrueToken INSTANCE = new TrueToken();

    private TrueToken() {
    }

    @Override
    public Boolean value() {
        return true;
    }
}
