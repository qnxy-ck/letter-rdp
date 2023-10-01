package com.ck.ast;

import com.ck.token.OperatorToken;

/**
 * 逻辑表达式
 *
 * @author 陈坤
 * 2023/10/1
 */
public record LogicalExpression(
        OperatorToken operator,
        ASTree left,
        ASTree right
) implements ASTree {
}
