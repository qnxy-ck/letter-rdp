package com.ck.ast;

import java.util.List;

/**
 * @author 陈坤
 * 2023/10/2
 */
public record NewExpression(
        ASTree callee,
        List<ASTree> arguments
) implements ASTree {
}
