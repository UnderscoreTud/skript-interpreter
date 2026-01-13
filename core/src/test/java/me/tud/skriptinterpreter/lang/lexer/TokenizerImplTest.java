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
        assertEquals("\"world with \"\"quotes\"\"\"", tokens.get(1).text());
        assertEquals(TokenType.EOF, tokens.get(2).type());
    }

    @Test
    void testVariables() throws TokenizationException {
        Tokenizer tokenizer = new TokenizerImpl("{var} {nested::%%expres%%%%sion%%%test%}");
        List<Token> tokens = tokenizer.tokenize();

        assertEquals(3, tokens.size());
        assertEquals(TokenType.VARIABLE, tokens.get(0).type());
        assertEquals("{var}", tokens.get(0).text());
        assertEquals(TokenType.VARIABLE, tokens.get(1).type());
        assertEquals("{nested::%%expres%%%%sion%%%test%}", tokens.get(1).text());
        assertEquals(TokenType.EOF, tokens.get(2).type());
    }

    @Test
    void testComments() throws TokenizationException {
        Tokenizer tokenizer = new TokenizerImpl("""
                word # comment
                another word""");
        List<Token> tokens = tokenizer.tokenize();

        // word, NEWLINE, another, word, EOF
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

        // line, 1, NEWLINE, INDENT, line, 2, NEWLINE, line, 3, NEWLINE, DEDENT, line, 4, NEWLINE, EOF
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
        assertEquals(TokenType.DEDENT, tokens.get(10).type());
        assertEquals(TokenType.WORD, tokens.get(11).type()); // line
        assertEquals(TokenType.NUMBER, tokens.get(12).type()); // 4
        assertEquals(TokenType.NEWLINE, tokens.get(13).type());
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
    void testStringEscapesAndExpressions() throws TokenizationException {
        Tokenizer tokenizer = new TokenizerImpl("\"quote \"\" here and %expression%\"");
        List<Token> tokens = tokenizer.tokenize();

        assertEquals(2, tokens.size());
        assertEquals(TokenType.STRING, tokens.get(0).type());
        assertEquals("\"quote \"\" here and %expression%\"", tokens.get(0).text());
        assertEquals(TokenType.EOF, tokens.get(1).type());
    }

    @Test
    void testNestedExpressionsInString() throws TokenizationException {
        Tokenizer tokenizer = new TokenizerImpl("\"outer %{var}% inner\"");
        List<Token> tokens = tokenizer.tokenize();

        assertEquals(2, tokens.size());
        assertEquals(TokenType.STRING, tokens.get(0).type());
        assertEquals("\"outer %{var}% inner\"", tokens.get(0).text());
        assertEquals(TokenType.EOF, tokens.get(1).type());
    }

    @Test
    void testMixedIndentationError() {
        String input = """
                line 1
                    line 2
                \tline 3""";
        // First indent is 4 spaces. Second is a tab. Should fail.

        Tokenizer tokenizer = new TokenizerImpl(input);
        assertThrows(TokenizationException.class, tokenizer::tokenize);
    }

    @Test
    void testJumpIndentationError() {
        String input = """
                line 1
                    line 2
                            line 3"""; 
        // line 1: level 0
        // line 2: level 1 (4 spaces)
        // line 3: level 3 (12 spaces) -> JUMP from 1 to 3!

        Tokenizer tokenizer = new TokenizerImpl(input);
        assertThrows(TokenizationException.class, tokenizer::tokenize);
    }

    @Test
    void testTabsIndentation() throws TokenizationException {
        String input = """
                line 1
                \tline 2
                \t\tline 3
                \tline 4
                line 5""";
        Tokenizer tokenizer = new TokenizerImpl(input);
        List<Token> tokens = tokenizer.tokenize();

        // line, 1, NEWLINE, INDENT, line, 2, NEWLINE, INDENT, line, 3, NEWLINE, DEDENT, line, 4, NEWLINE, DEDENT, line, 5, EOF
        // 0:WORD, 1:NUMBER, 2:NEWLINE, 3:INDENT(\t), 4:WORD, 5:NUMBER, 6:NEWLINE, 7:INDENT(\t\t), 8:WORD, 9:NUMBER, 10:NEWLINE, 11:DEDENT, 12:WORD, 13:NUMBER, 14:NEWLINE, 15:DEDENT, 16:WORD, 17:NUMBER, 18:EOF

        assertEquals(TokenType.WORD, tokens.get(0).type());
        assertEquals(TokenType.INDENT, tokens.get(3).type());
        assertEquals("\t", tokens.get(3).text());
        assertEquals(TokenType.INDENT, tokens.get(7).type());
        assertEquals("\t\t", tokens.get(7).text());
        assertEquals(TokenType.DEDENT, tokens.get(11).type());
        assertEquals(TokenType.DEDENT, tokens.get(15).type());
        assertEquals(TokenType.EOF, tokens.get(18).type());
    }

    @Test
    void testSymbolsWithWords() throws TokenizationException {
        Tokenizer tokenizer = new TokenizerImpl("if (x): return, {var}");
        List<Token> tokens = tokenizer.tokenize();

        // if, (, x, ), :, return, ,, {, var, }, EOF
        assertEquals(TokenType.WORD, tokens.get(0).type()); // if
        assertEquals(TokenType.LPAREN, tokens.get(1).type());
        assertEquals(TokenType.WORD, tokens.get(2).type()); // x
        assertEquals(TokenType.RPAREN, tokens.get(3).type());
        assertEquals(TokenType.COLON, tokens.get(4).type());
        assertEquals(TokenType.WORD, tokens.get(5).type()); // return
        assertEquals(TokenType.COMMA, tokens.get(6).type());
        assertEquals(TokenType.VARIABLE, tokens.get(7).type());
        assertEquals(TokenType.EOF, tokens.get(8).type());
    }

    @Test
    void testExpressionWithEscapedPercentInString() throws TokenizationException {
        // "val: %%%{var}%%%" -> "val: %", expression {var}, "%"

        Tokenizer tokenizer = new TokenizerImpl("\"%%%{var}%%%\"");
        List<Token> tokens = tokenizer.tokenize();

        assertEquals(2, tokens.size());
        assertEquals(TokenType.STRING, tokens.get(0).type());
        assertEquals("\"%%%{var}%%%\"", tokens.get(0).text());
        assertEquals(TokenType.EOF, tokens.get(1).type());
    }

    @Test
    void testUnterminatedString() {
        Tokenizer tokenizer = new TokenizerImpl("\"unterminated");
        assertThrows(TokenizationException.class, tokenizer::tokenize);
    }

}
