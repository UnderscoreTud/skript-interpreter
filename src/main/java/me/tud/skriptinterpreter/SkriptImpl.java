package me.tud.skriptinterpreter;

import me.tud.skriptinterpreter.patterns.*;

class SkriptImpl implements Skript {

    private final PatternCompiler patternCompiler = new PatternCompiler(this);

    @Override
    public void init() {
        patternCompiler.register(ChoicePatternElement.COMPILER);
        patternCompiler.register(GroupPatternElement.COMPILER);
        patternCompiler.register(GroupPatternElement.OPTIONAL_COMPILER);
        patternCompiler.register(ParseTagPatternElement.COMPILER);
        patternCompiler.register(RegexPatternElement.COMPILER);
    }

    @Override
    public void cleanup() {
    }

    @Override
    public PatternCompiler patterCompiler() {
        return patternCompiler;
    }

}
