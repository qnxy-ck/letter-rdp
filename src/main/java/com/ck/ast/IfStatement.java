package com.ck.ast;

/**
 * @author 陈坤
 * 2023/10/1
 */
public record IfStatement(
        ASTree test,
        Statement consequent,
        Statement alternate
) implements Statement {
}
