package com.ck.token.literal;

import com.ck.token.Token;

/**
 * 标识符Token
 *
 * @author 陈坤
 * 2023/10/1
 */
public record IdentifierToken(String value) implements Token<String> {

}
