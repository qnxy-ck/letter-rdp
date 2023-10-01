package com.ck.ast;

import com.ck.ast.literal.Identifier;

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
