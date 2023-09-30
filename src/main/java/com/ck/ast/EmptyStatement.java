package com.ck.ast;

/**
 * 空语句
 * 
 * @author 陈坤
 * 2023/10/1
 */
public class EmptyStatement implements Statement {
    public static final EmptyStatement INSTANCE = new EmptyStatement();

    private EmptyStatement() {

    }
}
