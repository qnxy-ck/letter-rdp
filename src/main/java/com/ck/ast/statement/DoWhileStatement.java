package com.ck.ast.statement;

import com.ck.ast.ASTree;
import com.ck.ast.Statement;

/**
 * DoWhile
 *
 * @author 陈坤
 * 2023/10/2
 */
public record DoWhileStatement(
        ASTree test,
        Statement body
) implements Statement {
}
