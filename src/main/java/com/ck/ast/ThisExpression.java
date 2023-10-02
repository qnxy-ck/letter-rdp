package com.ck.ast;

/**
 * @author 陈坤
 * 2023/10/2
 */
public class ThisExpression implements ASTree {

    public static final ThisExpression INSTANCE = new ThisExpression();

    private ThisExpression() {
    }
}
