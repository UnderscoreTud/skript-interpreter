package me.tud.skriptinterpreter.pattern.peg.node;

import java.util.Collections;
import java.util.List;

public record ExpressionNode(String expr, int index) implements PegNode {

    @Override
    public List<String> expand() {
        return Collections.singletonList("%" + expr + ":" + index + "%");
    }

}
