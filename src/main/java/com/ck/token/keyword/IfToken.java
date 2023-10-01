package com.ck.token.keyword;

import com.ck.token.Token;

/**
 * @author 陈坤
 * 2023/10/1
 */
public class IfToken implements Token<String> {

    public static final IfToken INSTANCE = new IfToken();

    private IfToken() {
    }

    @Override
    public String value() {
        return "if";
    }
}
