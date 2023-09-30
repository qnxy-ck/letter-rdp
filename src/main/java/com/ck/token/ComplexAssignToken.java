package com.ck.token;

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
public record ComplexAssignToken(String value) implements AssignToken {
}
