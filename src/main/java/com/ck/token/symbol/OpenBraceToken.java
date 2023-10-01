package com.ck.token.symbol;

import com.ck.token.Token;

/**
 * 左花括号: {
 * <p>
 * 实现为单例
 *
 * @author 陈坤
 * 2023/10/1
 */
public class OpenBraceToken implements Token<String> {

    public static final OpenBraceToken INSTANCE = new OpenBraceToken();

    private OpenBraceToken() {
    }

    @Override
    public String value() {
        return "{";
    }
}
