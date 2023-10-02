package com.ck.token.keyword;

import com.ck.token.Token;

/**
 * extends
 *
 * @author 陈坤
 * 2023/10/2
 */
public class ExtendsToken implements Token<String> {

    public static final ExtendsToken INSTANCE = new ExtendsToken();

    private ExtendsToken() {
    }

    @Override
    public String value() {
        return "extends";
    }
}
