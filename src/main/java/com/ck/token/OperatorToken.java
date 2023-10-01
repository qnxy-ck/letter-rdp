package com.ck.token;

import com.ck.ast.Operator;

/**
 * @author 陈坤
 * 2023/10/1
 */
public interface OperatorToken<T extends Operator> extends Token<String> {

    T toOperatorEnum();

}
