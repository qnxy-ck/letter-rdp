package com.ck.token.symbol;

import com.ck.token.Token;

/**
 * 右花括号: }
 * <p>
 * 实现为单例
 *
 * @author 陈坤
 * 2023/10/1
 */
public class ClosedBraceToken implements Token<String> {
    public static final ClosedBraceToken INSTANCE = new ClosedBraceToken();

    private ClosedBraceToken() {
    }

    @Override
    public String value() {
        return "}";
    }
}
