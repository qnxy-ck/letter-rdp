package com.ck.ast;

import com.ck.ast.statement.BlockStatement;

import java.util.List;

/**
 * func
 *
 * @author 陈坤
 * 2023/10/2
 */
public record FunctionDeclaration(
        ASTree name,
        List<ASTree> params,
        BlockStatement body
) implements Statement {
}
