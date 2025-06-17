package me.tud.skriptinterpreter.pattern.peg.node;

import java.util.ArrayList;
import java.util.List;

public record AlternationNode(List<PegNode> options) implements PegNode {

    @Override
    public List<String> expand() {
        List<String> result = new ArrayList<>();
        for (PegNode option : options)
            result.addAll(option.expand());
        return result;
    }

}
