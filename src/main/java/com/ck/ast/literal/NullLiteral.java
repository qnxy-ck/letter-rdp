package com.ck.ast.literal;

import com.ck.ast.Literal;

/**
 * @author 陈坤
 * 2023/10/1
 */
public record NullLiteral(Boolean value) implements Literal {
}
