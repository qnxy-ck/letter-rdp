package com.ck.token.literal;

import com.ck.token.Token;

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
