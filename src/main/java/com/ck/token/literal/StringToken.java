package com.ck.token.literal;

import com.ck.token.Token;

/**
 * 字符串Token
 *
 * @author 陈坤
 * 2023/9/30
 */
public record StringToken(
        String value
) implements Token<String> {
}
