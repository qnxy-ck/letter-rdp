package com.ck.token.symbol;

import com.ck.token.Token;

/**
 * 点
 *
 * @author 陈坤
 * 2023/10/2
 */
public class DotToken implements Token<String> {

    public static final DotToken INSTANCE = new DotToken();

    private DotToken() {
    }

    @Override
    public String value() {
        return ".";
    }
}
