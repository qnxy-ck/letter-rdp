package com.ck.ast;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author 陈坤
 * 2023/9/30
 */
@JsonPropertyOrder({"type"})
public interface ASTree {

    @JsonGetter
    default String type() {
        return this.getClass().getSimpleName();
    }

}
