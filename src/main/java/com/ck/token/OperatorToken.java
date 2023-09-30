package com.ck.token;

import com.ck.ast.BinaryOperator;

/**
 * @author 陈坤
 * 2023/10/1
 */
public interface OperatorToken extends Token<String> {

    default BinaryOperator toBinaryOperator() {
        return BinaryOperator.operatorOf(this.value()).orElseThrow();
    }
}
