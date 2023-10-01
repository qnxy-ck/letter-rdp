package com.ck.token.keyword;

import com.ck.token.IterationToken;

/**
 * while
 *
 * @author 陈坤
 * 2023/10/2
 */
public class WhileToken implements IterationToken {

    public static final WhileToken INSTANCE = new WhileToken();

    private WhileToken() {
    }

    @Override
    public String value() {
        return "while";
    }
}
