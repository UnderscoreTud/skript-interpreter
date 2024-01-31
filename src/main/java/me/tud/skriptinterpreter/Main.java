package me.tud.skriptinterpreter;

import me.tud.skriptinterpreter.patterns.MatchResult;
import me.tud.skriptinterpreter.patterns.ParseTagPatternElement;
import me.tud.skriptinterpreter.patterns.SkriptPattern;

public class Main {

    public static void main(String[] args) {
        Skript skript = Skript.create();
        skript.init();

//        Context<Object> context = new Context<>(skript, new Object());
//        Effect<Object> effect = new EffPrint<>();
//        effect.init(new Expressions<>(new ExprString<>("test")), skript);
//        effect.execute(context);

        SkriptPattern pattern = skript.patterCompiler().compile(":(hi|hey|hello [bye])");
        System.out.println(pattern);
        MatchResult result = pattern.match("hello bye");
        System.out.println(result == null ? "failed" : "succeeded");
        if (result == null) return;
        System.out.println(result.getDataContainer(ParseTagPatternElement.TagData.class));

        skript.cleanup();
    }

}
