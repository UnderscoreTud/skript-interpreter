package me.tud.skriptinterpreter.pattern.peg;

import me.tud.skriptinterpreter.pattern.peg.node.*;
import me.tud.skriptinterpreter.util.StringReader;

import java.util.ArrayList;
import java.util.List;

public class PegPatternParser {

    private final StringReader reader;
    private int expressionIndex, regexIndex;

    public PegPatternParser(String pattern) {
        this.reader = new StringReader(pattern);
    }

    public int expressionCount() {
        return expressionIndex;
    }

    public int regexCount() {
        return regexIndex;
    }

    public PegNode parse() {
        return parseAlternation();
    }

    private PegNode parseAlternation() {
        List<PegNode> options = new ArrayList<>();
        options.add(parseSequence());
        while (reader.canRead() && reader.peek() == '|') {
            reader.skip();
            options.add(parseSequence());
        }
        return options.size() == 1 ? options.getFirst() : new AlternationNode(options);
    }

    private PegNode parseSequence() {
        List<PegNode> elements = new ArrayList<>();
        char c;
        while (reader.canRead() && (c = reader.peek()) != '|' && c != ')' && c != ']')
            elements.add(parseElement());
        return new SequenceNode(elements);
    }

    private PegNode parseElement() {
        char c = reader.peek();
        if (c == '\\') {
            reader.skip();
            if (!reader.canRead())
                throw new IllegalArgumentException("Invalid escape sequence at end of pattern");
            return parseLiteral();
        }
        return switch (c) {
            case '(' -> parseGroup();
            case '[' -> parseOptionalGroup();
            case '%' -> parseExpression();
            case '<' -> parseRegex();
            default -> parseLiteral();
        };
    }

    private PegNode parseGroup() {
        reader.expect('(');
        PegNode group = parse();
        reader.expect(')');
        return new GroupNode(group);
    }

    private PegNode parseOptionalGroup() {
        reader.expect('[');
        PegNode group = parse();
        reader.expect(']');
        return new OptionalNode(group);
    }

    private PegNode parseExpression() {
        reader.expect('%');
        String expressionName = reader.readUntil('%');
        if (expressionName.isEmpty())
            throw new IllegalArgumentException("Expressions cannot be empty");
        reader.expect('%');
        return new ExpressionNode(expressionName, expressionIndex++);
    }

    private PegNode parseRegex() {
        reader.expect('<');
        String regex = reader.readUntil('>');
        if (regex.isEmpty())
            throw new IllegalArgumentException("Regex cannot be empty");
        reader.expect('>');
        return new RegexNode(regex, regexIndex++);
    }

    private PegNode parseLiteral() {
        StringBuilder sb = new StringBuilder();
        while (reader.canRead()) {
            char c = reader.peek();
            if ("()[]%<>|".indexOf(c) != -1)
                break;
            sb.append(reader.read());
        }
        return new LiteralNode(sb.toString());
    }

}
