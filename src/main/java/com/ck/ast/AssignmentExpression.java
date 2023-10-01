package com.ck.ast;

import com.ck.token.AssignToken;

/**
 * 赋值表达式
 *
 * @author 陈坤
 * 2023/10/1
 */
public record AssignmentExpression(
        AssignToken operator,
        ASTree left,
        ASTree right
) implements ASTree {
}
