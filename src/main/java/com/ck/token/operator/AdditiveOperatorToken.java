package com.ck.token.operator;

import com.ck.ast.operator.AdditiveOperator;
import com.ck.token.OperatorToken;

/**
 * 加减法运算符
 *
 * @author 陈坤
 * 2023/10/1
 */
public record AdditiveOperatorToken(String value) implements OperatorToken<AdditiveOperator> {

    @Override
    public AdditiveOperator toOperatorEnum() {
        return AdditiveOperator.operatorOf(this.value()).orElseThrow();
    }
    
}
