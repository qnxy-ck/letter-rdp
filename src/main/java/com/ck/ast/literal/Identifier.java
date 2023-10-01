package com.ck.ast.literal;

import com.ck.ast.ASTree;

/**
 * 标识符
 *
 * @author 陈坤
 * 2023/10/1
 */
public record Identifier(String name) implements ASTree {
}
