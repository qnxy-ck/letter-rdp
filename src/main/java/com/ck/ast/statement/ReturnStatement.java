package com.ck.ast.statement;

import com.ck.ast.ASTree;
import com.ck.ast.Statement;

/**
 * return
 *
 * @author 陈坤
 * 2023/10/2
 */
public record ReturnStatement(
        ASTree argument
) implements Statement {
}
