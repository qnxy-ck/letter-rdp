package com.ck.token.keyword;

import com.ck.token.Token;

/**
 * class
 *
 * @author 陈坤
 * 2023/10/2
 */
public class ClassToken implements Token<String> {

    public static final ClassToken INSTANCE = new ClassToken();

    private ClassToken() {
    }

    @Override
    public String value() {
        return "class";
    }
}
