package com.ck.token.symbol;

import com.ck.token.Token;

/**
 * 分号Token
 * <p>
 * 实现为单例
 *
 * @author 陈坤
 * 2023/10/1
 */
public class SemicolonToken implements Token<String> {

    public static final SemicolonToken INSTANCE = new SemicolonToken();

    private SemicolonToken() {
    }

    @Override
    public String value() {
        return ";";
    }
}
