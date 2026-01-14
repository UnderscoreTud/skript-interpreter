package me.tud.skriptinterpreter.lang.lexer.pattern;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class PatternTokenizerImplTest {

    @Test
    void testLiterals() throws PatternTokenizationException {
        PatternTokenizer tokenizer = new PatternTokenizerImpl("hello world");
        List<PatternToken> tokens = tokenizer.tokenize();

        assertEquals(4, tokens.size());
        assertEquals(PatternTokenType.LITERAL, tokens.get(0).type());
        assertEquals("hello", tokens.get(0).text());
        assertEquals(PatternTokenType.WHITESPACE, tokens.get(1).type());
        assertEquals(" ", tokens.get(1).text());
        assertEquals(PatternTokenType.LITERAL, tokens.get(2).type());
        assertEquals("world", tokens.get(2).text());
        assertEquals(PatternTokenType.EOF, tokens.get(3).type());
    }

    @Test
    void testGroups() throws PatternTokenizationException {
        PatternTokenizer tokenizer = new PatternTokenizerImpl("(hello|world) [optional]");
        List<PatternToken> tokens = tokenizer.tokenize();

        assertEquals(4, tokens.size());
        assertEquals(PatternTokenType.GROUP, tokens.get(0).type());
        assertEquals("(hello|world)", tokens.get(0).text());
        assertEquals(PatternTokenType.WHITESPACE, tokens.get(1).type());
        assertEquals(" ", tokens.get(1).text());
        assertEquals(PatternTokenType.OPTIONAL_GROUP, tokens.get(2).type());
        assertEquals("[optional]", tokens.get(2).text());
        assertEquals(PatternTokenType.EOF, tokens.get(3).type());
    }

    @Test
    void testPlaceholder() throws PatternTokenizationException {
        PatternTokenizer tokenizer = new PatternTokenizerImpl("<type> <type:tag>");
        List<PatternToken> tokens = tokenizer.tokenize();

        assertEquals(4, tokens.size());
        assertEquals(PatternTokenType.PLACEHOLDER, tokens.get(0).type());
        assertEquals("<type>", tokens.get(0).text());
        assertEquals(PatternTokenType.WHITESPACE, tokens.get(1).type());
        assertEquals(PatternTokenType.PLACEHOLDER, tokens.get(2).type());
        assertEquals("<type:tag>", tokens.get(2).text());
        assertEquals(PatternTokenType.EOF, tokens.get(3).type());
    }

    @Test
    void testPipes() throws PatternTokenizationException {
        PatternTokenizer tokenizer = new PatternTokenizerImpl("a|b|c");
        List<PatternToken> tokens = tokenizer.tokenize();

        assertEquals(6, tokens.size());
        assertEquals(PatternTokenType.LITERAL, tokens.get(0).type());
        assertEquals("a", tokens.get(0).text());
        assertEquals(PatternTokenType.PIPE, tokens.get(1).type());
        assertEquals(PatternTokenType.LITERAL, tokens.get(2).type());
        assertEquals("b", tokens.get(2).text());
        assertEquals(PatternTokenType.PIPE, tokens.get(3).type());
        assertEquals(PatternTokenType.LITERAL, tokens.get(4).type());
        assertEquals("c", tokens.get(4).text());
        assertEquals(PatternTokenType.EOF, tokens.get(5).type());
    }

    @Test
    void testTags() throws PatternTokenizationException {
        PatternTokenizer tokenizer = new PatternTokenizerImpl("lit: :dyn :(a|b)");
        List<PatternToken> tokens = tokenizer.tokenize();

        assertEquals(8, tokens.size());
        assertEquals(PatternTokenType.LITERAL_TAG, tokens.get(0).type());
        assertEquals("lit:", tokens.get(0).text());
        assertEquals(PatternTokenType.WHITESPACE, tokens.get(1).type());
        assertEquals(PatternTokenType.DYNAMIC_TAG, tokens.get(2).type());
        assertEquals(":", tokens.get(2).text());
        assertEquals(PatternTokenType.LITERAL, tokens.get(3).type());
        assertEquals("dyn", tokens.get(3).text());
        assertEquals(PatternTokenType.WHITESPACE, tokens.get(4).type());
        assertEquals(PatternTokenType.DYNAMIC_TAG, tokens.get(5).type());
        assertEquals(":", tokens.get(5).text());
        assertEquals(PatternTokenType.GROUP, tokens.get(6).type());
        assertEquals("(a|b)", tokens.get(6).text());
        assertEquals(PatternTokenType.EOF, tokens.get(7).type());
    }

    @Test
    void testNestedGroups() throws PatternTokenizationException {
        PatternTokenizer tokenizer = new PatternTokenizerImpl("(a(b)c) [(d[e]f)]");
        List<PatternToken> tokens = tokenizer.tokenize();

        assertEquals(4, tokens.size());
        assertEquals(PatternTokenType.GROUP, tokens.get(0).type());
        assertEquals("(a(b)c)", tokens.get(0).text());
        assertEquals(PatternTokenType.WHITESPACE, tokens.get(1).type());
        assertEquals(PatternTokenType.OPTIONAL_GROUP, tokens.get(2).type());
        assertEquals("[(d[e]f)]", tokens.get(2).text());
    }

    @Test
    void testUnterminatedGroup() {
        PatternTokenizer tokenizer = new PatternTokenizerImpl("(abc");
        assertThrows(PatternTokenizationException.class, tokenizer::tokenize);
    }

    @Test
    void testUnterminatedOptionalGroup() {
        PatternTokenizer tokenizer = new PatternTokenizerImpl("[abc");
        assertThrows(PatternTokenizationException.class, tokenizer::tokenize);
    }

    @Test
    void testInvalidWhitespace() {
        PatternTokenizer tokenizer = new PatternTokenizerImpl("hello\tworld");
        assertThrows(PatternTokenizationException.class, tokenizer::tokenize);
    }

    @Test
    void testInvalidEscape() {
        PatternTokenizer tokenizer = new PatternTokenizerImpl("\\a");
        assertThrows(PatternTokenizationException.class, tokenizer::tokenize);
    }

    @Test
    void testEscapingDetailed() throws PatternTokenizationException {
        PatternTokenizer tokenizer = new PatternTokenizerImpl("\\( \\) \\[ \\] \\< \\> \\| \\: \\\\");
        List<PatternToken> tokens = tokenizer.tokenize();

        // 9 literals and 8 spaces
        assertEquals(18, tokens.size());
        assertEquals("\\(", tokens.get(0).text());
        assertEquals("\\)", tokens.get(2).text());
        assertEquals("\\[", tokens.get(4).text());
        assertEquals("\\]", tokens.get(6).text());
        assertEquals("\\<", tokens.get(8).text());
        assertEquals("\\>", tokens.get(10).text());
        assertEquals("\\|", tokens.get(12).text());
        assertEquals("\\:", tokens.get(14).text());
        assertEquals("\\\\", tokens.get(16).text());
    }

    @Test
    void testUnexpectedClosingBracket() {
        PatternTokenizer tokenizer = new PatternTokenizerImpl(")");
        assertThrows(PatternTokenizationException.class, tokenizer::tokenize);
    }

    @Test
    void testMismatchedBrackets() {
        PatternTokenizer tokenizer = new PatternTokenizerImpl("(a]b)");
        assertThrows(PatternTokenizationException.class, tokenizer::tokenize);
    }

    @Test
    void testUnterminatedPlaceholder() {
        PatternTokenizer tokenizer = new PatternTokenizerImpl("<type");
        assertThrows(PatternTokenizationException.class, tokenizer::tokenize);
    }

    @Test
    void testPlaceholderWithSpaces() {
        PatternTokenizer tokenizer = new PatternTokenizerImpl("<type name>");
        assertThrows(PatternTokenizationException.class, tokenizer::tokenize);
    }

}
