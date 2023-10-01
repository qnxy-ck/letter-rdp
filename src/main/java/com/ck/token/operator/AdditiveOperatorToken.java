package com.ck.token.operator;

import com.ck.token.BinaryOperatorToken;

/**
 * 加减法运算符
 *
 * @author 陈坤
 * 2023/10/1
 */
public record AdditiveOperatorToken(String value) implements BinaryOperatorToken {
}
