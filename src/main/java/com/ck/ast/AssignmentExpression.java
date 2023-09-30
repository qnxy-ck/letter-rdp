package com.ck.ast;

/**
 * 赋值表达式
 * 
 * @author 陈坤
 * 2023/10/1
 */
public record AssignmentExpression(
        AssignmentOperator operator,
        ASTree left,
        ASTree right
) implements ASTree {
}
