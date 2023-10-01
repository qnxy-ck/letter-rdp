package com.ck.ast.statement;

import com.ck.ast.ASTree;
import com.ck.ast.Statement;

/**
 * For
 *
 * @author 陈坤
 * 2023/10/2
 */
public record ForStatement(
        ASTree init,
        ASTree test,
        ASTree update,
        Statement body
) implements Statement {
}
