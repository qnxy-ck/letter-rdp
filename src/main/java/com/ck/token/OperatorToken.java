package com.ck.token;

import java.util.Arrays;

/**
 * @author 陈坤
 * 2023/10/1
 */
public interface OperatorToken extends Token<String> {

    static <T extends Enum<T> & OperatorToken> T toBuilder(T[] v, String operator) {
        return Arrays.stream(v)
                .filter(it -> it.value().equals(operator))
                .findFirst()
                .orElseThrow();
    }
}
