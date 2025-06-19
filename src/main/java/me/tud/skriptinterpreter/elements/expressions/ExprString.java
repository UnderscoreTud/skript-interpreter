package me.tud.skriptinterpreter.elements.expressions;

import me.tud.skriptinterpreter.lang.Expression;
import me.tud.skriptinterpreter.lang.Expressions;
import me.tud.skriptinterpreter.parser.ParseContext;
import me.tud.skriptinterpreter.parser.exceptions.ParseException;
import me.tud.skriptinterpreter.runtime.RuntimeContext;

public class ExprString implements Expression<Object, String> {

    private final String value;

    public ExprString(String value) {
        this.value = value;
    }

    @Override
    public boolean init(Expressions<Object> expressions, ParseContext context) throws ParseException {
        return true;
    }

    @Override
    public String evaluate(RuntimeContext<Object> context) {
        return value;
    }

}
