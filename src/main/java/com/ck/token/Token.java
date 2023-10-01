package com.ck.token;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Token 抽象接口
 *
 * @author 陈坤
 * 2023/9/30
 */
public interface Token<T> {

    @JsonValue
    T value();

}
