package me.tud.skriptinterpreter.runtime;

import me.tud.skriptinterpreter.lang.variable.VariableResolver;

public record Environment(VariableResolver variableResolver) {}
