package me.tud.skriptinterpreter.lang.lexer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class TokenizerImplTest {

    @Test
    void testSimpleWords() throws TokenizationException {
        Tokenizer tokenizer = new TokenizerImpl("hello world");
        List<Token> tokens = tokenizer.tokenize();

        assertEquals(3, tokens.size());
        assertEquals(TokenType.WORD, tokens.get(0).type());
        assertEquals("hello", tokens.get(0).text());
        assertEquals(TokenType.WORD, tokens.get(1).type());
        assertEquals("world", tokens.get(1).text());
        assertEquals(TokenType.EOF, tokens.get(2).type());
    }

    @Test
    void testSymbols() throws TokenizationException {
        Tokenizer tokenizer = new TokenizerImpl(":,()");
        List<Token> tokens = tokenizer.tokenize();

        assertEquals(5, tokens.size());
        assertEquals(TokenType.COLON, tokens.get(0).type());
        assertEquals(TokenType.COMMA, tokens.get(1).type());
        assertEquals(TokenType.LPAREN, tokens.get(2).type());
        assertEquals(TokenType.RPAREN, tokens.get(3).type());
        assertEquals(TokenType.EOF, tokens.get(4).type());
    }

    @Test
    void testNumbers() throws TokenizationException {
        Tokenizer tokenizer = new TokenizerImpl("123 45.67 -89 .10");
        List<Token> tokens = tokenizer.tokenize();

        assertEquals(5, tokens.size());
        assertEquals(TokenType.NUMBER, tokens.get(0).type());
        assertEquals("123", tokens.get(0).text());
        assertEquals(TokenType.NUMBER, tokens.get(1).type());
        assertEquals("45.67", tokens.get(1).text());
        assertEquals(TokenType.NUMBER, tokens.get(2).type());
        assertEquals("-89", tokens.get(2).text());
        assertEquals(TokenType.NUMBER, tokens.get(3).type());
        assertEquals(".10", tokens.get(3).text());
        assertEquals(TokenType.EOF, tokens.get(4).type());
    }

    @Test
    void testStrings() throws TokenizationException {
        Tokenizer tokenizer = new TokenizerImpl("\"hello\" \"world with \"\"quotes\"\"\"");
        List<Token> tokens = tokenizer.tokenize();

        assertEquals(3, tokens.size());
        assertEquals(TokenType.STRING, tokens.get(0).type());
        assertEquals("\"hello\"", tokens.get(0).text());
        assertEquals(TokenType.STRING, tokens.get(1).type());
        assertEquals("\"world with \"quotes\"\"", tokens.get(1).text()); // TODO maybe it shouldn't eat escaped quotes?
        assertEquals(TokenType.EOF, tokens.get(2).type());
    }

    @Test
    void testVariables() throws TokenizationException {
        Tokenizer tokenizer = new TokenizerImpl("{var} {nested %expres%%sion%}");
        List<Token> tokens = tokenizer.tokenize();

        assertEquals(3, tokens.size());
        assertEquals(TokenType.VARIABLE, tokens.get(0).type());
        assertEquals("{var}", tokens.get(0).text());
        assertEquals(TokenType.VARIABLE, tokens.get(1).type());
        assertEquals("{nested %expres%sion%}", tokens.get(1).text()); // TODO maybe it shouldn't eat escaped percents?
        assertEquals(TokenType.EOF, tokens.get(2).type());
    }

    @Test
    void testComments() throws TokenizationException {
        Tokenizer tokenizer = new TokenizerImpl("""
                word # comment
                another word""");
        List<Token> tokens = tokenizer.tokenize();

        // word, NEWLINE, another, word, EOF
        // Note: comments are skipped
        assertEquals(5, tokens.size());
        assertEquals(TokenType.WORD, tokens.get(0).type());
        assertEquals("word", tokens.get(0).text());
        assertEquals(TokenType.NEWLINE, tokens.get(1).type());
        assertEquals(TokenType.WORD, tokens.get(2).type());
        assertEquals("another", tokens.get(2).text());
        assertEquals(TokenType.WORD, tokens.get(3).type());
        assertEquals("word", tokens.get(3).text());
        assertEquals(TokenType.EOF, tokens.get(4).type());
    }

    @Test
    void testIndentation() throws TokenizationException {
        String input = """
                line 1
                    line 2
                    line 3
                line 4
                """;
        Tokenizer tokenizer = new TokenizerImpl(input);
        List<Token> tokens = tokenizer.tokenize();

        // line, 1, NEWLINE, INDENT, line, 2, NEWLINE, line, 3, NEWLINE, line, 4, NEWLINE, DEDENT, EOF
        assertEquals(TokenType.WORD, tokens.get(0).type()); // line
        assertEquals(TokenType.NUMBER, tokens.get(1).type()); // 1
        assertEquals(TokenType.NEWLINE, tokens.get(2).type());
        assertEquals(TokenType.INDENT, tokens.get(3).type());
        assertEquals(TokenType.WORD, tokens.get(4).type()); // line
        assertEquals(TokenType.NUMBER, tokens.get(5).type()); // 2
        assertEquals(TokenType.NEWLINE, tokens.get(6).type());
        assertEquals(TokenType.WORD, tokens.get(7).type()); // line
        assertEquals(TokenType.NUMBER, tokens.get(8).type()); // 3
        assertEquals(TokenType.NEWLINE, tokens.get(9).type());
        assertEquals(TokenType.WORD, tokens.get(10).type()); // line
        assertEquals(TokenType.NUMBER, tokens.get(11).type()); // 4
        assertEquals(TokenType.NEWLINE, tokens.get(12).type());
        assertEquals(TokenType.DEDENT, tokens.get(13).type());
        assertEquals(TokenType.EOF, tokens.get(14).type());
    }

    @Test
    void testMultilineComment() throws TokenizationException {
        String input = """
                word ### multiline
                comment ### another word""";
        Tokenizer tokenizer = new TokenizerImpl(input);
        List<Token> tokens = tokenizer.tokenize();

        assertEquals(4, tokens.size());
        assertEquals(TokenType.WORD, tokens.get(0).type());
        assertEquals("word", tokens.get(0).text());
        assertEquals(TokenType.WORD, tokens.get(1).type());
        assertEquals("another", tokens.get(1).text());
        assertEquals(TokenType.WORD, tokens.get(2).type());
        assertEquals("word", tokens.get(2).text());
        assertEquals(TokenType.EOF, tokens.get(3).type());
    }

    @Test
    void testUnterminatedString() {
        Tokenizer tokenizer = new TokenizerImpl("\"unterminated");
        assertThrows(TokenizationException.class, tokenizer::tokenize);
    }
}
