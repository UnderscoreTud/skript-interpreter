package me.tud.skriptinterpreter.pattern.peg.node;

import java.util.ArrayList;
import java.util.List;

public record OptionalNode(PegNode inner) implements PegNode {

    @Override
    public List<String> expand() {
        List<String> result = new ArrayList<>(inner.expand());
        result.add("");
        return result;
    }

}
