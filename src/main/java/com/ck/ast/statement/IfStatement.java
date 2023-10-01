package com.ck.ast.statement;

import com.ck.ast.ASTree;
import com.ck.ast.Statement;

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
