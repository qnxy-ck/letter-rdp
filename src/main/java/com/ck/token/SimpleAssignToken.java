package com.ck.token;

/**
 * 简单赋值运算符
 * =
 *
 * @author 陈坤
 * 2023/10/1
 */
public class SimpleAssignToken implements AssignToken {

    public static final SimpleAssignToken INSTANCE = new SimpleAssignToken();

    private SimpleAssignToken() {
    }

    @Override
    public String value() {
        return "=";
    }
}
