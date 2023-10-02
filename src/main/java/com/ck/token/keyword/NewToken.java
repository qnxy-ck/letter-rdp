package com.ck.token.keyword;

import com.ck.token.Token;

/**
 * new
 *
 * @author 陈坤
 * 2023/10/2
 */
public class NewToken implements Token<String> {

    public static final NewToken INSTANCE = new NewToken();

    private NewToken() {
    }

    @Override
    public String value() {
        return "new";
    }
}
