package com.ck.token;

/**
 * 逗号
 *
 * @author 陈坤
 * 2023/10/1
 */
public class CommaToken implements Token<String> {

    public static final CommaToken INSTANCE = new CommaToken();

    private CommaToken() {
    }

    @Override
    public String value() {
        return ",";
    }
}
