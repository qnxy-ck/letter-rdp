package com.ck.ast;

/**
 * 字符字面量
 * 
 * @author 陈坤
 * 2023/9/30
 */
public record StringLiteral(
        String value
) implements Literal {
}
