package tests;

import automaton.FiniteAutomaton;
import grammar.ChomskyType;
import grammar.Grammar;
import grammar.Production;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
public class GrammarTest {
    private Grammar grammar;

    @BeforeEach
    void setUp() {
        String[] nonTerminalVariables = {"S", "A", "B"};
        String[] terminalVariables = {"a", "b"};
        Production[] productions = {
                new Production("S", "aA"),
                new Production("S", "bB"),
                new Production("A", "aS"),
                new Production("A", "b"),
                new Production("B", "bS"),
                new Production("B", "a")
        };
        String startingCharacter = "S";

        this.grammar = new Grammar(nonTerminalVariables, terminalVariables, productions, startingCharacter);

    }

    @Test
    void testIsRegularGrammar() {
        String[] nonTerminalVariables = {"S"};
        String[] terminalVariables = {"a", "b"};
        Production[] productions = {
                new Production("S", "aS"),
                new Production("S", "b")
        };
        String startingCharacter = "S";

        Grammar grammar = new Grammar(nonTerminalVariables, terminalVariables, productions, startingCharacter);
        assertTrue(grammar.isRegularGrammar());
    }

    @Test
    void testIsContextFreeGrammar() {
        String[] nonTerminalVariables = {"S"};
        String[] terminalVariables = {"a", "b"};
        Production[] productions = {
                new Production("S", "aSb"),
                new Production("S", "ε")
        };
        String startingCharacter = "S";

        Grammar grammar = new Grammar(nonTerminalVariables, terminalVariables, productions, startingCharacter);
        assertTrue(grammar.isContextFreeGrammar());
    }

    @Test
    void testIsContextSensitiveGrammar() {
        String[] nonTerminalVariables = {"S", "A", "B"};
        String[] terminalVariables = {"a", "b"};
        Production[] productions = {
                new Production("S", "AAB"),
                new Production("A", "aA"),
                new Production("A", "ε"),
                new Production("B", "b")
        };
        String startingCharacter = "S";

        Grammar grammar = new Grammar(nonTerminalVariables, terminalVariables, productions, startingCharacter);
        assertTrue(grammar.isContextSensitiveGrammar());
    }
    @Test
    void testGenerateWord() {
        String generatedWord = this.grammar.generateWord();
        assertNotNull(generatedWord);
    }

    @Test
    void testToFiniteAutomaton() {
        FiniteAutomaton finiteAutomaton = this.grammar.toFiniteAutomaton();
        assertNotNull(finiteAutomaton);
    }

    @Test
    void testClassifyGrammar() {
        ChomskyType chomskyType = this.grammar.classifyGrammar();
        assertNotNull(chomskyType);
    }

    @Test
    void testConvertToChomskyNormalForm() {

        String[] nonTerminalVariables = {"S", "A", "B"};
        String[] terminalVariables = {"a", "b"};
        Production[] productions = {
                new Production("S", "aA"),
                new Production("S", "bB"),
                new Production("A", "aS"),
                new Production("A", "b"),
                new Production("B", "bS"),
                new Production("B", "a")
        };
        String startingCharacter = "S";

        Grammar grammar = new Grammar(nonTerminalVariables, terminalVariables, productions, startingCharacter);
//        grammar.convertToChomskyNormalForm();

        for (Production production : grammar.getProductions()) {

            if (production.getRightSide().length() == 1) {
                assertTrue(Arrays.asList(terminalVariables).contains(production.getRightSide()));
            } else if (production.getRightSide().length() == 2) {
                for (char symbol : production.getRightSide().toCharArray()) {
                    assertTrue(Arrays.asList(grammar.getNonTerminalVariables()).contains(String.valueOf(symbol)));
                }
            } else {
                fail("Production is not in Chomsky Normal Form: " + production);
            }
        }
    }
}
