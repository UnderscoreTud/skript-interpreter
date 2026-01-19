package me.tud.skriptinterpreter.lang.lexer.source;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class SourceTokenizerImplTest {

    @Test
    void testSimpleWords() throws SourceTokenizationException {
        SourceTokenizer tokenizer = new SourceTokenizerImpl("hello world");
        List<SourceToken> tokens = tokenizer.tokenize();

        assertEquals(3, tokens.size());
        assertEquals(SourceTokenType.WORD, tokens.get(0).type());
        assertEquals("hello", tokens.get(0).text());
        assertEquals(SourceTokenType.WORD, tokens.get(1).type());
        assertEquals("world", tokens.get(1).text());
        assertEquals(SourceTokenType.EOF, tokens.get(2).type());
    }

    @Test
    void testSymbols() throws SourceTokenizationException {
        SourceTokenizer tokenizer = new SourceTokenizerImpl(":,()");
        List<SourceToken> tokens = tokenizer.tokenize();

        assertEquals(5, tokens.size());
        assertEquals(SourceTokenType.COLON, tokens.get(0).type());
        assertEquals(SourceTokenType.COMMA, tokens.get(1).type());
        assertEquals(SourceTokenType.LPAREN, tokens.get(2).type());
        assertEquals(SourceTokenType.RPAREN, tokens.get(3).type());
        assertEquals(SourceTokenType.EOF, tokens.get(4).type());
    }

    @Test
    void testNumbers() throws SourceTokenizationException {
        SourceTokenizer tokenizer = new SourceTokenizerImpl("123 45.67 -89 .10");
        List<SourceToken> tokens = tokenizer.tokenize();

        assertEquals(5, tokens.size());
        assertEquals(SourceTokenType.NUMBER, tokens.get(0).type());
        assertEquals("123", tokens.get(0).text());
        assertEquals(SourceTokenType.NUMBER, tokens.get(1).type());
        assertEquals("45.67", tokens.get(1).text());
        assertEquals(SourceTokenType.NUMBER, tokens.get(2).type());
        assertEquals("-89", tokens.get(2).text());
        assertEquals(SourceTokenType.NUMBER, tokens.get(3).type());
        assertEquals(".10", tokens.get(3).text());
        assertEquals(SourceTokenType.EOF, tokens.get(4).type());
    }

    @Test
    void testStrings() throws SourceTokenizationException {
        SourceTokenizer tokenizer = new SourceTokenizerImpl("\"hello\" \"world with \"\"quotes\"\"\"");
        List<SourceToken> tokens = tokenizer.tokenize();

        assertEquals(7, tokens.size());
        assertEquals(SourceTokenType.STRING_START, tokens.get(0).type());
        assertEquals("\"", tokens.get(0).text());
        assertEquals(SourceTokenType.LITERAL_TEXT, tokens.get(1).type());
        assertEquals("hello", tokens.get(1).text());
        assertEquals(SourceTokenType.STRING_END, tokens.get(2).type());
        assertEquals("\"", tokens.get(2).text());

        assertEquals(SourceTokenType.STRING_START, tokens.get(3).type());
        assertEquals("\"", tokens.get(3).text());
        assertEquals(SourceTokenType.LITERAL_TEXT, tokens.get(4).type());
        assertEquals("world with \"quotes\"", tokens.get(4).text());
        assertEquals(SourceTokenType.STRING_END, tokens.get(5).type());
        assertEquals("\"", tokens.get(5).text());

        assertEquals(SourceTokenType.EOF, tokens.get(6).type());
    }
    
    @Test
    void testEmptyString() throws SourceTokenizationException {
        SourceTokenizer tokenizer = new SourceTokenizerImpl("\"\"");
        List<SourceToken> tokens = tokenizer.tokenize();

        assertEquals(3, tokens.size());
        assertEquals(SourceTokenType.STRING_START, tokens.get(0).type());
        assertEquals(SourceTokenType.STRING_END, tokens.get(1).type());

        assertEquals(SourceTokenType.EOF, tokens.get(2).type());
    }

    @Test
    void testStringifiedExpression() throws SourceTokenizationException {
        SourceTokenizer tokenizer = new SourceTokenizerImpl("\"%expr%\"");
        List<SourceToken> tokens = tokenizer.tokenize();

        assertEquals(6, tokens.size());
        assertEquals(SourceTokenType.STRING_START, tokens.get(0).type());
        assertEquals(SourceTokenType.INTERPOLATION_START, tokens.get(1).type());
        assertEquals(SourceTokenType.WORD, tokens.get(2).type());
        assertEquals("expr", tokens.get(2).text());
        assertEquals(SourceTokenType.INTERPOLATION_END, tokens.get(3).type());
        assertEquals(SourceTokenType.STRING_END, tokens.get(4).type());

        assertEquals(SourceTokenType.EOF, tokens.get(5).type());
    }

    @Test
    void testVariables() throws SourceTokenizationException {
        SourceTokenizer tokenizer = new SourceTokenizerImpl("{var} {nested::%%expres%%%%sion%%%test%}");
        List<SourceToken> tokens = tokenizer.tokenize();

        // {var} -> VARIABLE_START, LITERAL_TEXT(var), VARIABLE_END (3)
        // {nested::%%expres%%%%sion%%%test%} -> VARIABLE_START, LITERAL_TEXT(nested::%expres%%sion%), INTERPOLATION_START(%), WORD(test), INTERPOLATION_END(%), VARIABLE_END (6)
        // TOTAL: 3 + 6 + 1 (EOF) = 10 tokens

        assertEquals(10, tokens.size());
        assertEquals(SourceTokenType.VARIABLE_START, tokens.get(0).type());
        assertEquals(SourceTokenType.LITERAL_TEXT, tokens.get(1).type());
        assertEquals("var", tokens.get(1).text());
        assertEquals(SourceTokenType.VARIABLE_END, tokens.get(2).type());

        assertEquals(SourceTokenType.VARIABLE_START, tokens.get(3).type());
        assertEquals(SourceTokenType.LITERAL_TEXT, tokens.get(4).type());
        assertEquals("nested::%expres%%sion%", tokens.get(4).text());
        assertEquals(SourceTokenType.INTERPOLATION_START, tokens.get(5).type());
        assertEquals(SourceTokenType.WORD, tokens.get(6).type());
        assertEquals("test", tokens.get(6).text());
        assertEquals(SourceTokenType.INTERPOLATION_END, tokens.get(7).type());
        assertEquals(SourceTokenType.VARIABLE_END, tokens.get(8).type());

        assertEquals(SourceTokenType.EOF, tokens.get(9).type());
    }

    @Test
    void testEmptyVariable() throws SourceTokenizationException {
        SourceTokenizer tokenizer = new SourceTokenizerImpl("{}");
        List<SourceToken> tokens = tokenizer.tokenize();

        assertEquals(3, tokens.size());
        assertEquals(SourceTokenType.VARIABLE_START, tokens.get(0).type());
        assertEquals(SourceTokenType.VARIABLE_END, tokens.get(1).type());

        assertEquals(SourceTokenType.EOF, tokens.get(2).type());
    }

    @Test
    void testVariableExpression() throws SourceTokenizationException {
        SourceTokenizer tokenizer = new SourceTokenizerImpl("{%expr%}");
        List<SourceToken> tokens = tokenizer.tokenize();

        assertEquals(6, tokens.size());
        assertEquals(SourceTokenType.VARIABLE_START, tokens.get(0).type());
        assertEquals(SourceTokenType.INTERPOLATION_START, tokens.get(1).type());
        assertEquals(SourceTokenType.WORD, tokens.get(2).type());
        assertEquals("expr", tokens.get(2).text());
        assertEquals(SourceTokenType.INTERPOLATION_END, tokens.get(3).type());
        assertEquals(SourceTokenType.VARIABLE_END, tokens.get(4).type());

        assertEquals(SourceTokenType.EOF, tokens.get(5).type());
    }

    @Test
    void testComments() throws SourceTokenizationException {
        SourceTokenizer tokenizer = new SourceTokenizerImpl("""
                word # comment
                another word""");
        List<SourceToken> tokens = tokenizer.tokenize();

        // word, NEWLINE, another, word, EOF
        assertEquals(5, tokens.size());
        assertEquals(SourceTokenType.WORD, tokens.get(0).type());
        assertEquals("word", tokens.get(0).text());
        assertEquals(SourceTokenType.NEWLINE, tokens.get(1).type());
        assertEquals(SourceTokenType.WORD, tokens.get(2).type());
        assertEquals("another", tokens.get(2).text());
        assertEquals(SourceTokenType.WORD, tokens.get(3).type());
        assertEquals("word", tokens.get(3).text());
        assertEquals(SourceTokenType.EOF, tokens.get(4).type());
    }

    @Test
    void testIndentation() throws SourceTokenizationException {
        String input = """
                line 1
                    line 2
                    line 3
                line 4
                """;
        SourceTokenizer tokenizer = new SourceTokenizerImpl(input);
        List<SourceToken> tokens = tokenizer.tokenize();

        // line, 1, NEWLINE, INDENT, line, 2, NEWLINE, line, 3, NEWLINE, DEDENT, line, 4, NEWLINE, EOF
        assertEquals(SourceTokenType.WORD, tokens.get(0).type()); // line
        assertEquals(SourceTokenType.NUMBER, tokens.get(1).type()); // 1
        assertEquals(SourceTokenType.NEWLINE, tokens.get(2).type());
        assertEquals(SourceTokenType.INDENT, tokens.get(3).type());
        assertEquals(SourceTokenType.WORD, tokens.get(4).type()); // line
        assertEquals(SourceTokenType.NUMBER, tokens.get(5).type()); // 2
        assertEquals(SourceTokenType.NEWLINE, tokens.get(6).type());
        assertEquals(SourceTokenType.WORD, tokens.get(7).type()); // line
        assertEquals(SourceTokenType.NUMBER, tokens.get(8).type()); // 3
        assertEquals(SourceTokenType.NEWLINE, tokens.get(9).type());
        assertEquals(SourceTokenType.DEDENT, tokens.get(10).type());
        assertEquals(SourceTokenType.WORD, tokens.get(11).type()); // line
        assertEquals(SourceTokenType.NUMBER, tokens.get(12).type()); // 4
        assertEquals(SourceTokenType.NEWLINE, tokens.get(13).type());
        assertEquals(SourceTokenType.EOF, tokens.get(14).type());
    }

    @Test
    void testMultilineComment() throws SourceTokenizationException {
        String input = """
                word ### multiline
                comment ### another word""";
        SourceTokenizer tokenizer = new SourceTokenizerImpl(input);
        List<SourceToken> tokens = tokenizer.tokenize();

        assertEquals(4, tokens.size());
        assertEquals(SourceTokenType.WORD, tokens.get(0).type());
        assertEquals("word", tokens.get(0).text());
        assertEquals(SourceTokenType.WORD, tokens.get(1).type());
        assertEquals("another", tokens.get(1).text());
        assertEquals(SourceTokenType.WORD, tokens.get(2).type());
        assertEquals("word", tokens.get(2).text());
        assertEquals(SourceTokenType.EOF, tokens.get(3).type());
    }

    @Test
    void testStringEscapesAndExpressions() throws SourceTokenizationException {
        SourceTokenizer tokenizer = new SourceTokenizerImpl("\"quote \"\" here and %expression%\"");
        List<SourceToken> tokens = tokenizer.tokenize();

        assertEquals(7, tokens.size());
        assertEquals(SourceTokenType.STRING_START, tokens.get(0).type());
        assertEquals(SourceTokenType.LITERAL_TEXT, tokens.get(1).type());
        assertEquals("quote \" here and ", tokens.get(1).text());
        assertEquals(SourceTokenType.INTERPOLATION_START, tokens.get(2).type());
        assertEquals(SourceTokenType.WORD, tokens.get(3).type());
        assertEquals("expression", tokens.get(3).text());
        assertEquals(SourceTokenType.INTERPOLATION_END, tokens.get(4).type());
        assertEquals(SourceTokenType.STRING_END, tokens.get(5).type());
        assertEquals(SourceTokenType.EOF, tokens.get(6).type());
    }

    @Test
    void testNestedExpressionsInString() throws SourceTokenizationException {
        SourceTokenizer tokenizer = new SourceTokenizerImpl("\"outer %{var}% inner\"");
        List<SourceToken> tokens = tokenizer.tokenize();

        assertEquals(10, tokens.size());
        assertEquals(SourceTokenType.STRING_START, tokens.get(0).type());
        assertEquals(SourceTokenType.LITERAL_TEXT, tokens.get(1).type());
        assertEquals("outer ", tokens.get(1).text());
        assertEquals(SourceTokenType.INTERPOLATION_START, tokens.get(2).type());
        assertEquals(SourceTokenType.VARIABLE_START, tokens.get(3).type());
        assertEquals(SourceTokenType.LITERAL_TEXT, tokens.get(4).type());
        assertEquals("var", tokens.get(4).text());
        assertEquals(SourceTokenType.VARIABLE_END, tokens.get(5).type());
        assertEquals(SourceTokenType.INTERPOLATION_END, tokens.get(6).type());
        assertEquals(SourceTokenType.LITERAL_TEXT, tokens.get(7).type());
        assertEquals(" inner", tokens.get(7).text());
        assertEquals(SourceTokenType.STRING_END, tokens.get(8).type());
        assertEquals(SourceTokenType.EOF, tokens.get(9).type());
    }

    @Test
    void testMixedIndentationError() {
        String input = """
                line 1
                    line 2
                \tline 3""";
        // First indent is 4 spaces. Second is a tab. Should fail.

        SourceTokenizer tokenizer = new SourceTokenizerImpl(input);
        assertThrows(SourceTokenizationException.class, tokenizer::tokenize);
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

        SourceTokenizer tokenizer = new SourceTokenizerImpl(input);
        assertThrows(SourceTokenizationException.class, tokenizer::tokenize);
    }

    @Test
    void testTabsIndentation() throws SourceTokenizationException {
        String input = """
                line 1
                \tline 2
                \t\tline 3
                \tline 4
                line 5""";
        SourceTokenizer tokenizer = new SourceTokenizerImpl(input);
        List<SourceToken> tokens = tokenizer.tokenize();

        // line, 1, NEWLINE, INDENT, line, 2, NEWLINE, INDENT, line, 3, NEWLINE, DEDENT, line, 4, NEWLINE, DEDENT, line, 5, EOF
        // 0:WORD, 1:NUMBER, 2:NEWLINE, 3:INDENT(\t), 4:WORD, 5:NUMBER, 6:NEWLINE, 7:INDENT(\t\t), 8:WORD, 9:NUMBER, 10:NEWLINE, 11:DEDENT, 12:WORD, 13:NUMBER, 14:NEWLINE, 15:DEDENT, 16:WORD, 17:NUMBER, 18:EOF

        assertEquals(SourceTokenType.WORD, tokens.get(0).type());
        assertEquals(SourceTokenType.INDENT, tokens.get(3).type());
        assertEquals("\t", tokens.get(3).text());
        assertEquals(SourceTokenType.INDENT, tokens.get(7).type());
        assertEquals("\t\t", tokens.get(7).text());
        assertEquals(SourceTokenType.DEDENT, tokens.get(11).type());
        assertEquals(SourceTokenType.DEDENT, tokens.get(15).type());
        assertEquals(SourceTokenType.EOF, tokens.get(18).type());
    }

    @Test
    void testSymbolsWithWords() throws SourceTokenizationException {
        SourceTokenizer tokenizer = new SourceTokenizerImpl("if (x): return, {var}");
        List<SourceToken> tokens = tokenizer.tokenize();

        // if, (, x, ), :, return, ,, {, var, }, EOF
        assertEquals(11, tokens.size());
        assertEquals(SourceTokenType.WORD, tokens.get(0).type()); // if
        assertEquals(SourceTokenType.LPAREN, tokens.get(1).type());
        assertEquals(SourceTokenType.WORD, tokens.get(2).type()); // x
        assertEquals(SourceTokenType.RPAREN, tokens.get(3).type());
        assertEquals(SourceTokenType.COLON, tokens.get(4).type());
        assertEquals(SourceTokenType.WORD, tokens.get(5).type()); // return
        assertEquals(SourceTokenType.COMMA, tokens.get(6).type());
        assertEquals(SourceTokenType.VARIABLE_START, tokens.get(7).type());
        assertEquals(SourceTokenType.LITERAL_TEXT, tokens.get(8).type());
        assertEquals("var", tokens.get(8).text());
        assertEquals(SourceTokenType.VARIABLE_END, tokens.get(9).type());
        assertEquals(SourceTokenType.EOF, tokens.get(10).type());
    }

    @Test
    void testExpressionWithEscapedPercentInString() throws SourceTokenizationException {
        // "%%%{var}%%%" -> STRING_START, LITERAL_TEXT(%), INTERPOLATION_START, VARIABLE_START, LITERAL_TEXT(var), VARIABLE_END, INTERPOLATION_END, LITERAL_TEXT(%), STRING_END

        SourceTokenizer tokenizer = new SourceTokenizerImpl("\"%%%{var}%%%\"");
        List<SourceToken> tokens = tokenizer.tokenize();

        assertEquals(10, tokens.size());
        assertEquals(SourceTokenType.STRING_START, tokens.get(0).type());
        assertEquals(SourceTokenType.LITERAL_TEXT, tokens.get(1).type());
        assertEquals("%", tokens.get(1).text());
        assertEquals(SourceTokenType.INTERPOLATION_START, tokens.get(2).type());
        assertEquals(SourceTokenType.VARIABLE_START, tokens.get(3).type());
        assertEquals(SourceTokenType.LITERAL_TEXT, tokens.get(4).type());
        assertEquals("var", tokens.get(4).text());
        assertEquals(SourceTokenType.VARIABLE_END, tokens.get(5).type());
        assertEquals(SourceTokenType.INTERPOLATION_END, tokens.get(6).type());
        assertEquals(SourceTokenType.LITERAL_TEXT, tokens.get(7).type());
        assertEquals("%", tokens.get(7).text());
        assertEquals(SourceTokenType.STRING_END, tokens.get(8).type());
        assertEquals(SourceTokenType.EOF, tokens.get(9).type());
    }

    @Test
    void testUnterminatedString() {
        SourceTokenizer tokenizer = new SourceTokenizerImpl("\"unterminated");
        assertThrows(SourceTokenizationException.class, tokenizer::tokenize);
    }

}
