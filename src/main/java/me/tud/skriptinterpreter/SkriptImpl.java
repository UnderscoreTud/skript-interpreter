package me.tud.skriptinterpreter;

import me.tud.skriptinterpreter.log.SkriptLogger;
import me.tud.skriptinterpreter.patterns.ChoicePatternElement;
import me.tud.skriptinterpreter.patterns.GroupPatternElement;
import me.tud.skriptinterpreter.patterns.PatternCompiler;
import me.tud.skriptinterpreter.patterns.RegexPatternElement;

class SkriptImpl implements Skript {

    private final PatternCompiler patternCompiler = new PatternCompiler(this);

    @Override
    public void init() {
        patternCompiler.register(ChoicePatternElement.COMPILER);
        patternCompiler.register(GroupPatternElement.COMPILER);
        patternCompiler.register(GroupPatternElement.OPTIONAL_COMPILER);
        patternCompiler.register(RegexPatternElement.COMPILER);
        // TODO implement and register
    }

    @Override
    public void cleanup() {
    }

    @Override
    public PatternCompiler patterCompiler() {
        return patternCompiler;
    }

}
