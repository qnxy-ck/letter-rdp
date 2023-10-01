package com.ck.ast;

import com.ck.token.OperatorToken;

/**
 * @author 陈坤
 * 2023/10/1
 */
public record BinaryExpression(
        OperatorToken operator,
        ASTree left,
        ASTree right
) implements ASTree {
}
