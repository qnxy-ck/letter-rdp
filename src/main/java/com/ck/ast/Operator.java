package com.ck.ast;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author 陈坤
 * 2023/10/1
 */
public interface Operator {

    @JsonValue
    String operator();
}
