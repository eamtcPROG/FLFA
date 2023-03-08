package tools;

import automaton.FiniteAutomaton;
import grammar.Grammar;

import java.security.SecureRandom;

public class Tools {
    public String chars;

    public String getChars() {
        return chars;
    }

    public void setChars(String chars) {
        this.chars = chars;
    }

    public String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(this.chars.length());
            sb.append(this.chars.charAt(randomIndex));
        }
        return sb.toString();
    }

    public void printOutputLab2(FiniteAutomaton FA,Grammar grammar){
        System.out.println("\n1. Classify the grammar based on Chomsky hierarchy: ");
        System.out.println("Current grammar is of: " + grammar.classifyGrammar());
        System.out.println("2. Convert the finite automaton to regular grammar: " + FA.convertToRegularGrammar());
        System.out.println("3. Check if Finite Automaton is deterministic: "+ FA.isDeterministic());
        System.out.println("4. NFA to DFA: " + FA.convertToDFA());
    }
}
