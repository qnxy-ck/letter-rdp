package com.ck.ast.statement;

import com.ck.ast.ASTree;
import com.ck.ast.Statement;

/**
 * While
 *
 * @author 陈坤
 * 2023/10/2
 */
public record WhileStatement(
        ASTree test,
        Statement body
) implements Statement {
}
