package com.ck.token.operator;

import com.ck.token.BinaryOperatorToken;

/**
 * 乘除法运算符
 *
 * @author 陈坤
 * 2023/10/1
 */
public record MultiplicativeOperatorToken(String value) implements BinaryOperatorToken {
}
