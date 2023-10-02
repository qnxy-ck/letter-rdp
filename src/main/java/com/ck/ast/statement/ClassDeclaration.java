package com.ck.ast.statement;

import com.ck.ast.Statement;
import com.ck.ast.literal.Identifier;

/**
 * @author 陈坤
 * 2023/10/2
 */
public record ClassDeclaration(
        Identifier id,
        Identifier superClass,
        BlockStatement body
) implements Statement {
}
