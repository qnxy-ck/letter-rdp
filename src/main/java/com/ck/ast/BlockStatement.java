package com.ck.ast;

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
