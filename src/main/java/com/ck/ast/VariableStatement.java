package com.ck.ast;

import java.util.List;

/**
 * @author 陈坤
 * 2023/10/1
 */
public record VariableStatement(
        List<ASTree> declarations
) implements Statement {
}
