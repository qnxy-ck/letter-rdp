package com.ck.token.operator;

import com.ck.ast.operator.MultiplicativeOperator;
import com.ck.token.OperatorToken;

/**
 * 乘除法运算符
 *
 * @author 陈坤
 * 2023/10/1
 */
public record MultiplicativeOperatorToken(String value) implements OperatorToken<MultiplicativeOperator> {

    @Override
    public MultiplicativeOperator toOperatorEnum() {
        return MultiplicativeOperator.operatorOf(this.value()).orElseThrow();
    }
    
}
