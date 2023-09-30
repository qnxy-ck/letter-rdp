package com.ck.token;

/**
 * 数值Token
 *
 * @author 陈坤
 * 2023/9/30
 */
public record NumberToken(
        Integer value
) implements Token<Integer> {

}
