package com.ck.token;

/**
 * @author 陈坤
 * 2023/10/1
 */
public class IfToken implements KeywordToken {

    public static final IfToken INSTANCE = new IfToken();

    private IfToken() {
    }

    @Override
    public String value() {
        return "if";
    }
}
