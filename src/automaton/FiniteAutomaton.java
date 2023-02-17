package automaton;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class FiniteAutomaton {
    private final char[] states;
    private final char[] alphabet;
    private Transition[] transitions;
    private final char initialState;
    private final char[] finalStates;

    public FiniteAutomaton (
             char[] states,
             char[] alphabet,
             Transition[] transitions,
             char initialState,
             char[] finalStates
            )
    {
        this.states = states;
        this.alphabet = alphabet;
        this.transitions = transitions;
        this.initialState = initialState;
        this.finalStates = finalStates;
    }

    public char[] getStates() {
        return this.states;
    }
    public char[] getAlphabet() {
        return this.alphabet;
    }
    public char getInitialState() {
        return this.initialState;
    }
    public char[] getFinalStates() {
        return this.finalStates;
    }
    public Set<Character> epsilonClosure(char state) {
        Set<Character> closure = new HashSet<>();
        closure.add(state);

        Stack<Character> stack = new Stack<>();
        stack.push(state);

        while (!stack.isEmpty()) {
            char currentState = stack.pop();
            for (Transition t : transitions) {
                if (t.getCurrentState() == currentState && t.getTransitionLabel() == 'e') {
                    char nextState = t.getNextState();
                    if (!closure.contains(nextState)) {
                        closure.add(nextState);
                        stack.push(nextState);
                    }
                }
            }
        }

        return closure;
    }
    public boolean isWordValid(String str) {
        Set<Character> currentStates = epsilonClosure(this.initialState);

        for (char c : str.toCharArray()) {
            Set<Character> nextStates = new HashSet<>();

            for (char currentState : currentStates) {
                for (Transition t : this.transitions) {
                    if (t.getCurrentState() == currentState &&
                            t.getTransitionLabel() == c) {
                        nextStates.addAll(epsilonClosure(t.getNextState()));
                    }
                }
            }

            if (nextStates.isEmpty()) {
                return false;
            }

            currentStates = nextStates;
        }

        for (char finalState : this.finalStates) {
            if (currentStates.contains(finalState)) {
                return true;
            }
        }

        return false;
    }

    public String printFiniteAutomaton() {
        return "FiniteAutomaton " + "\n" +
                "\tStates = " + Arrays.toString(states) + "\n" +
                "\tAlphabet = " + Arrays.toString(alphabet) + "\n" +
                "\tTransitions = " + Arrays.toString(transitions) + "\n" +
                "\tInitial state = " + initialState + "\n" +
                "\tFinal States = " + Arrays.toString(finalStates) + "\n";
    }
}