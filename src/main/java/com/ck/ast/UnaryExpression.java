package com.ck.ast;

import com.ck.token.OperatorToken;

/**
 * 一元表达式
 *
 * @author 陈坤
 * 2023/10/2
 */
public record UnaryExpression(
        OperatorToken operator,
        ASTree argument
) implements ASTree {
}
