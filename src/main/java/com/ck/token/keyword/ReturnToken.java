package com.ck.token.keyword;

import com.ck.token.Token;

/**
 * return
 *
 * @author 陈坤
 * 2023/10/2
 */
public class ReturnToken implements Token<String> {

    public static final ReturnToken INSTANCE = new ReturnToken();

    private ReturnToken() {
    }

    @Override
    public String value() {
        return "return";
    }
}
