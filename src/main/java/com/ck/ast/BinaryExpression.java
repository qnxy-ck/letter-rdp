package com.ck.ast;

/**
 * @author 陈坤
 * 2023/10/1
 */
public record BinaryExpression(
        Operator operator,
        ASTree left,
        ASTree right
) implements ASTree {
}
