package com.ck.token.symbol;

import com.ck.token.Token;

/**
 * ]
 *
 * @author 陈坤
 * 2023/10/2
 */
public class ClosedSquareBracketsToken implements Token<String> {

    public static final ClosedSquareBracketsToken INSTANCE = new ClosedSquareBracketsToken();

    private ClosedSquareBracketsToken() {
    }

    @Override
    public String value() {
        return "]";
    }
}
