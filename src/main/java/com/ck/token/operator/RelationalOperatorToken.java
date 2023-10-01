package com.ck.token.operator;

import com.ck.ast.operator.RelationalOperator;
import com.ck.token.OperatorToken;

/**
 * 关系运算符
 * >
 * >=
 * <
 * <=
 *
 * @author 陈坤
 * 2023/10/1
 */
public record RelationalOperatorToken(String value) implements OperatorToken<RelationalOperator> {

    @Override
    public RelationalOperator toOperatorEnum() {
        return RelationalOperator.operatorOf(this.value()).orElseThrow();
    }
}
