package com.ck.ast;

/**
 * 表达式语句
 * 
 * @author 陈坤
 * 2023/10/1
 */
public record ExpressionStatement(
        ASTree expression
) implements Statement {
}
