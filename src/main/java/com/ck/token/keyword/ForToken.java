package com.ck.token.keyword;

import com.ck.token.IterationToken;

/**
 * for
 *
 * @author 陈坤
 * 2023/10/2
 */
public class ForToken implements IterationToken {

    public static final ForToken INSTANCE = new ForToken();

    private ForToken() {
    }

    @Override
    public String value() {
        return "for";
    }
}
