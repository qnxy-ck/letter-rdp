package com.ck.token.operator;

import com.ck.ast.operator.EqualityOperator;
import com.ck.token.OperatorToken;

/**
 * 等式运算符
 * ==
 * !=
 *
 * @author 陈坤
 * 2023/10/1
 */
public record EqualityOperatorToken(String value) implements OperatorToken<EqualityOperator> {

    @Override
    public EqualityOperator toOperatorEnum() {
        return EqualityOperator.operatorOf(this.value()).orElseThrow();
    }

}
