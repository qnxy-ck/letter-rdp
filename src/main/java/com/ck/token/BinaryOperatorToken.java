package com.ck.token;

import com.ck.ast.operator.BinaryOperator;

/**
 * @author 陈坤
 * 2023/10/1
 */
public interface BinaryOperatorToken extends OperatorToken<BinaryOperator> {

    @Override
    default BinaryOperator toOperatorEnum() {
        return BinaryOperator.operatorOf(this.value()).orElseThrow();
    }
}
