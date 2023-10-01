package com.ck.ast.statement;

import com.ck.ast.ASTree;
import com.ck.ast.Statement;

import java.util.List;

/**
 * 语句块
 *
 * @author 陈坤
 * 2023/10/1
 */
public record BlockStatement(
        List<ASTree> body
) implements Statement {
}
