package com.ck.ast;

/**
 * 变量定义
 *
 * @author 陈坤
 * 2023/10/1
 */
public record VariableDeclaration(
        Identifier id,
        ASTree init
) implements ASTree {
}
