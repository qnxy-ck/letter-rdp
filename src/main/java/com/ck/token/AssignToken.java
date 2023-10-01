package com.ck.token;

import com.ck.ast.operator.AssignmentOperator;

/**
 * 赋值运算符 抽象接口
 * 
 * @author 陈坤
 * 2023/10/1
 */
public interface AssignToken extends Token<String> {

    default AssignmentOperator toAssignmentOperator() {
        return AssignmentOperator.operatorOf(this.value()).orElseThrow();
    }
}
