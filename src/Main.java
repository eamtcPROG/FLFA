import automaton.FiniteAutomaton;
import grammar.Grammar;
import grammar.Production;
import tools.Tools;

import java.security.SecureRandom;

public class Main {
    public static void main(String[] args) {
        Grammar grammar = new Grammar(
                new char[]{'S', 'B', 'L'},
                new char[]{'a', 'b', 'c'},
                new Production[]{
                        new Production("S", "aB"),
                        new Production("B", "bB"),
                        new Production("B", "cL"),
                        new Production("L", "cL"),
                        new Production("L", "aS"),
                        new Production("L", "b"),
                },
                'S'
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
        System.out.println("\n" + finiteautomaton.printFiniteAutomaton());

        System.out.println("\nTest cases: ");
        int counter = 1;
        for (int i = 0; i < n; i++) {
            System.out.println("  " + counter + ". - " + generatedStrings[i] + ": " + finiteautomaton.isWordValid(generatedStrings[i]));
            System.out.println("  " + ++counter + ". - " + generatedStringsRandom[i] + ": " + finiteautomaton.isWordValid(generatedStringsRandom[i]));
            counter++;
        }

    }
}