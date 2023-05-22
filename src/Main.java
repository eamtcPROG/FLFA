import automaton.FiniteAutomaton;
import grammar.Grammar;
import automaton.Transition;
import grammar.Production;
import tools.Tools;

import java.security.SecureRandom;


public class Main {
    public static void main(String[] args) {
        Grammar grammar = new Grammar(
                new String[]{"S", "B", "L"},
                new String[]{"a", "b", "c"},
                new Production[]{
                        new Production("S", "aB"),
                        new Production("B", "bB"),
                        new Production("B", "cL"),
                        new Production("L", "cL"),
                        new Production("L", "aS"),
                        new Production("L", "b"),
                },
                "S"
        );

        FiniteAutomaton FA = new FiniteAutomaton(
                new String[]{"q0", "q1", "q2", "q3"},
                new String[]{"a", "b","c"},
                new Transition[]{
                        new Transition("q0", "a", "q1"),
                        new Transition("q1", "b", "q2"),
                        new Transition("q2", "c", "q0"),
                        new Transition("q1", "a", "q3"),
                        new Transition("q0", "b", "q2"),
                        new Transition("q2", "c", "q3"),
                },
                "q0",
                new String[]{"q3"}
        );

        Tools tools = new Tools();
        tools.setChars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");

        int n = 5;

        SecureRandom random = new SecureRandom();
        int randomInt = 10;

        String[] generatedStrings = new String[n];
        String[] generatedStringsRandom = new String[n];

        System.out.println("Generate 5 random words: ");
        for (int i = 0; i < n; i++) {
            generatedStrings[i] = grammar.generateWord();
            generatedStringsRandom[i] = tools.generateRandomString(random.nextInt(randomInt));
            System.out.println(generatedStrings[i]);
        }

        FiniteAutomaton finiteautomaton = grammar.toFiniteAutomaton();
        System.out.println("\n" + finiteautomaton.toString());

        System.out.println("\nTest cases: ");
        int counter = 1;
        for (int i = 0; i < n; i++) {
            System.out.println("  " + counter + ". - " + generatedStrings[i] + ": " + finiteautomaton.isWordValid(generatedStrings[i]));
            System.out.println("  " + ++counter + ". - " + generatedStringsRandom[i] + ": " + finiteautomaton.isWordValid(generatedStringsRandom[i]));
            counter++;

        }
        tools.printOutputLab2(FA,grammar);
        tools.generateTheLab3();

        tools.generateTheLab4();
        tools.generateTheLab5();
    }
}