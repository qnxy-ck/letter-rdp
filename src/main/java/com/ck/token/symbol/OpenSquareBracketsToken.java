package com.ck.token.symbol;

import com.ck.token.Token;

/**
 * [
 *
 * @author 陈坤
 * 2023/10/2
 */
public class OpenSquareBracketsToken implements Token<String> {

    public static final OpenSquareBracketsToken INSTANCE = new OpenSquareBracketsToken();

    private OpenSquareBracketsToken() {
    }

    @Override
    public String value() {
        return "[";
    }
}
