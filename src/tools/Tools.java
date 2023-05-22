package tools;

import automaton.FiniteAutomaton;
import grammar.ChomskyNormalFormConverter;
import grammar.Grammar;
import grammar.Production;
import lexer.Lexer;
import parser.Parser;

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
    public void printTest(String num,String input,Lexer lexer){
        System.out.println(num + ". Test: " + input);
        System.out.println("Tokens:");
        lexer.printTokens();
        System.out.println();
    }
    public void generateTheLab3() {
        String input = "5 + (1 - 8) *3 / 9 = 3";
        Lexer lexer = new Lexer(input);
        printTest("1",input,lexer);

        input = "8 - 3 * 3 / 1 = 2";
        lexer = new Lexer(input);
        printTest("2",input,lexer);

        input = "(10-31)*100/12 = 2";
        lexer = new Lexer(input);
        printTest("3",input,lexer);
    }

    public void generateTheLab4() {
        String[] nonTerminalVariables = {"S", "A", "B", "D"};
        String[] terminalVariables = {"a", "b", "d"};

        Production[] productions = {
                new Production("S", "dB"),
                new Production("S", "AB"),
                new Production("A", "d"),
                new Production("A", "dS"),
                new Production("A", "aAaAb"),
                new Production("A", "ε"),
                new Production("B", "a"),
                new Production("B", "aS"),
                new Production("B", "A"),
                new Production("D", "Aba")
        };

        Grammar grammar = new Grammar(nonTerminalVariables, terminalVariables, productions, "S");
        System.out.println("Input Grammar");
        System.out.println(grammar);
        System.out.println("---------------------------");
        System.out.println("CNF grammar");
        ChomskyNormalFormConverter chomskyNormalFormConverter = new ChomskyNormalFormConverter(grammar);
//        grammar.convertToChomskyNormalForm();
        grammar = chomskyNormalFormConverter.getGrammar(grammar);
        System.out.println(grammar);
    }

    public void generateTheLab5(){
        System.out.println("---------------------------------");
        System.out.println("Lab 5");
        String expression = "2+3*4-6/5=";
        Parser parser = new Parser(expression);
        try {
            float result = parser.parse();
            System.out.println(expression + result);
            String expression2 = "1*2+3=";
            parser.setExpression(expression2);
            float result2 = parser.parse();
            System.out.println(expression2 + result2);
            String expression3 = "1+2+3+4+5/1/2/3/4/5-1-2-3-4-5*1*2*3*4*5=";
            parser.setExpression(expression3);
            float result3 = parser.parse();
            System.out.println(expression3 + result3);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("---------------------------------");
    }
}
