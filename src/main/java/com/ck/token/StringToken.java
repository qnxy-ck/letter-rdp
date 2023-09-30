package com.ck.token;

/**
 * @author 陈坤
 * 2023/9/30
 */
public record StringToken(
        String value
) implements Token<String> {
}
