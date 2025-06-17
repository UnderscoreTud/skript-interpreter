package me.tud.skriptinterpreter.pattern;

import me.tud.skriptinterpreter.lexer.Token;
import me.tud.skriptinterpreter.lexer.TokenIterator;
import me.tud.skriptinterpreter.lexer.TokenType;

import java.util.Collections;

public class WhitespacePatternNode extends LiteralPatternNode {

    private static final Token WHITESPACE_TOKEN = new Token(TokenType.WHITESPACE, " ", 1, 1);

    public WhitespacePatternNode() {
        super(" ", Collections.singletonList(WHITESPACE_TOKEN));
    }

    @Override
    public boolean matches(TokenIterator tokens, MatchResult.Metadata metadata) {
        if (!tokens.hasNext())
            return false;
        Token firstToken = tokens.next();
        return firstToken.type() == TokenType.WHITESPACE;
    }

}
