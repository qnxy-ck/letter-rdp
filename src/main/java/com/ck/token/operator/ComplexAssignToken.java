package com.ck.token.operator;

import com.ck.token.AssignToken;

/**
 * 复杂赋值运算符
 * -=
 * +=
 * *=
 * /=
 *
 * @author 陈坤
 * 2023/10/1
 */
public enum ComplexAssignToken implements AssignToken {

    MUL_ASSIGN("*="),
    DIV_ASSIGN("/="),
    ADD_ASSIGN("+="),
    SUB_ASSIGN("-="),

    ;
    private final String operator;

    ComplexAssignToken(String operator) {
        this.operator = operator;
    }

    @Override
    public String value() {
        return this.operator;
    }

}
