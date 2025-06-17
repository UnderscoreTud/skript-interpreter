package me.tud.skriptinterpreter.pattern.peg.node;

import java.util.List;

public sealed interface PegNode permits
        AlternationNode, ExpressionNode, GroupNode,
        LiteralNode, OptionalNode, RegexNode,
        SequenceNode, WhitespaceNode {

    List<String> expand();

}
