package me.tud.skriptinterpreter.parser;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.pattern.MatchResult;
import me.tud.skriptinterpreter.runtime.Environment;

public record ParseContext(Skript skript, MatchResult matchResult, Environment environment) {}
