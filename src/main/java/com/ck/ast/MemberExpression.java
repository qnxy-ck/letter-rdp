package com.ck.ast;

/**
 * 属性调用
 *
 * @author 陈坤
 * 2023/10/2
 */
public record MemberExpression(
        // 属性是否需要计算
        boolean computed,
        ASTree object,
        ASTree property
) implements ASTree {
}
