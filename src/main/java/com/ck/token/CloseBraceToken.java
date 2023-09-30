package com.ck.token;

/**
 * 右花括号
 * <p>
 * 实现为单例
 *
 * @author 陈坤
 * 2023/10/1
 */
public class CloseBraceToken implements Token<String> {
    public static final CloseBraceToken INSTANCE = new CloseBraceToken();

    private CloseBraceToken() {
    }

    @Override
    public String value() {
        return "}";
    }
}
