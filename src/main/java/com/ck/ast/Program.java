package com.ck.ast;

import java.util.List;

/**
 * AST 入口
 *
 * @author 陈坤
 * 2023/9/30
 */
public record Program(
        List<ASTree> body
) implements ASTree {

}
