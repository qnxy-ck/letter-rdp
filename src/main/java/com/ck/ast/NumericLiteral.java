package com.ck.ast;

/**
 * @author 陈坤
 * 2023/9/30
 */
public record NumericLiteral(
        Integer value
) implements Literal {
}
