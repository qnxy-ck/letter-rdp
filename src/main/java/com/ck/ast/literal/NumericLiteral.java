package com.ck.ast.literal;

import com.ck.ast.Literal;

/**
 * 数字字面量
 * 
 * @author 陈坤
 * 2023/9/30
 */
public record NumericLiteral(
        Integer value
) implements Literal {
}
