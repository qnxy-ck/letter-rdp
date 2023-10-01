package com.ck.ast.statement;

import com.ck.ast.ASTree;
import com.ck.ast.Statement;

import java.util.List;

/**
 * @author 陈坤
 * 2023/10/1
 */
public record VariableStatement(
        List<ASTree> declarations
) implements Statement {
}
