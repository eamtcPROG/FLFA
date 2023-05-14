package grammar;

import automaton.FiniteAutomaton;
import automaton.Transition;

import java.util.*;

public class Grammar {
    private String[] nonTerminalVariables;
    private final String[] terminalVariables;
    private Production[] productions;
    private String startingCharacter;

    public Grammar(String[] nonTerminalVariables, String[] terminalVariables,
                   Production[] productions, String startingCharacter) {
        this.nonTerminalVariables = nonTerminalVariables;
        this.terminalVariables = terminalVariables;
        this.productions = productions;
        this.startingCharacter = startingCharacter;
    }

    public Production[] getProductions() {
        return this.productions;
    }
    public String[] getNonTerminalVariables() {
        return this.nonTerminalVariables;
    }

    public String generateWord() {
        return generateWord(this.startingCharacter);
    }

    private String generateWord(String symbol) {
        StringBuilder result = new StringBuilder();

        ArrayList<Production> possibleProductions = new ArrayList<>();
        for (Production production : this.productions) {
            if (Objects.equals(production.getLeftSide(), symbol)) {
                possibleProductions.add(production);
            }
        }

        Random random = new Random();
        int randomIndex = random.nextInt(possibleProductions.size());
        String rightSide = possibleProductions.get(randomIndex).getRightSide();

        for (int i = 0; i < rightSide.length(); i++) {
            String currentSymbol = String.valueOf(rightSide.charAt(i));
            if (isNonTerminal(currentSymbol)) {
                result.append(generateWord(currentSymbol));
            } else {
                result.append(currentSymbol);
            }
        }

        return result.toString();
    }

    private boolean isNonTerminal(String symbol) {
        for (String nonTerminal : this.nonTerminalVariables) {
            if (Objects.equals(nonTerminal, symbol)) {
                return true;
            }
        }
        return false;
    }

    public FiniteAutomaton toFiniteAutomaton() {
        // Q - possible states
        String[] possibleStates = Arrays.toString(this.nonTerminalVariables).split("");
        String[] newPossibleStates = new String[possibleStates.length + 1];
        System.arraycopy(possibleStates, 0, newPossibleStates, 0, possibleStates.length);
        newPossibleStates[newPossibleStates.length - 1] = "X";
        possibleStates = newPossibleStates;

        String[] alphabet = terminalVariables;

        Transition[] transitions = new Transition[this.productions.length];
        int i = 0;
        for (Production production : this.productions) {
            char currentState = production.getLeftSide().charAt(0);
            char nextState = production.getRightSide().length() > 1
                    ? production.getRightSide().charAt(1)
                    : 'X';
            String transitionLabel = String.valueOf(production.getRightSide().charAt(0));

            transitions[i] = new Transition(Character.toString(currentState), transitionLabel, Character.toString(nextState));
            i++;
        }

        String initialState = String.valueOf(startingCharacter);

        String[] finalStates = new String[]{"X"};

        return new FiniteAutomaton(possibleStates, alphabet, transitions, initialState, finalStates);
    }

    public ChomskyType classifyGrammar() {
        if (isRegularGrammar()) {
            return ChomskyType.TYPE_3;
        } else if (isContextFreeGrammar()) {
            return ChomskyType.TYPE_2;
        } else if (isContextSensitiveGrammar()) {
            return ChomskyType.TYPE_1;
        } else {
            return ChomskyType.TYPE_0;
        }
    }

    public boolean isRegularGrammar() {
        for (Production p : productions) {
            String rhs = p.getRightSide();
            if (rhs.length() == 1 && Character.isLowerCase(rhs.charAt(0))) {
                continue;
            } else if (rhs.length() == 2) {
                char firstSymbol = rhs.charAt(0);
                char secondSymbol = rhs.charAt(1);
                if (Character.isUpperCase(firstSymbol) && Character.isLowerCase(secondSymbol)) {
                    continue;
                } else if (Character.isLowerCase(firstSymbol) && Character.isUpperCase(secondSymbol)) {
                    continue;
                }
            }
            return false;
        }

        return true;
    }

    public boolean isContextFreeGrammar() {
        for (Production production : productions) {
            String leftSide = production.getLeftSide();
            String rightSide = production.getRightSide();
            if (leftSide.length() != 1 || !Character.isUpperCase(leftSide.charAt(0))) {
                return false;
            }
            for (int i = 0; i < rightSide.length(); i++) {
                char symbol = rightSide.charAt(i);
                if (!Character.isUpperCase(symbol) && !Character.isLowerCase(symbol)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isContextSensitiveGrammar() {
        for (Production p : productions) {
            String leftSide = p.getLeftSide();
            String rightSide = p.getRightSide();

            if (leftSide.length() > rightSide.length()) {
                return false;
            }
        }

        return true;
    }

    public void convertToChomskyNormalForm() {
        substituteStartingSymbol();
        removeEpsilonProductions();
        removeUnitProductions();
        removeNonproductiveSymbols();
        removeInaccessibleSymbols();
        toChomskyNormalForm();
    }

    public void toChomskyNormalForm() {
        List<String> newNonTerminalVariables = new ArrayList<>(Arrays.asList(nonTerminalVariables));
        List<Production> newProductions = new ArrayList<>();
        Map<String, String> productionsMap = new HashMap<>();

        char newNonTerminalStart = 'F';

        for (Production production : productions) {
            String rightSide = production.getRightSide();
            if (rightSide.length() > 2) {
                String remaining = rightSide;
                while (remaining.length() > 2) {
                    String toReplace = remaining.substring(0, 1);
                    String newNonTerminal = String.valueOf(newNonTerminalStart++);
                    newNonTerminalVariables.add(newNonTerminal);
                    newProductions.add(new Production(newNonTerminal, toReplace));
                    remaining = newNonTerminal + remaining.substring(1);
                }
                newProductions.add(new Production(production.getLeftSide(), remaining));
            } else if (rightSide.length() == 2 && !rightSide.matches("[A-Z][A-Z]")) {
                for (char c : rightSide.toCharArray()) {
                    if (Character.isLowerCase(c)) {
                        String terminal = String.valueOf(c);
                        if (!productionsMap.containsKey(terminal)) {
                            String newNonTerminal = String.valueOf(newNonTerminalStart++);
                            productionsMap.put(terminal, newNonTerminal);
                            newNonTerminalVariables.add(newNonTerminal);
                            newProductions.add(new Production(newNonTerminal, terminal));
                        }
                        rightSide = rightSide.replace(terminal, productionsMap.get(terminal));
                    }
                }
                newProductions.add(new Production(production.getLeftSide(), rightSide));
            } else {
                newProductions.add(production);
            }
        }

        productions = newProductions.toArray(new Production[0]);
        nonTerminalVariables = newNonTerminalVariables.toArray(new String[0]);
    }
    private String getNewNonTerminal(List<String> nonTerminals) {
        char newSymbol = 'U';

        while (nonTerminals.contains(Character.toString(newSymbol))) {
            newSymbol++;
        }

        return Character.toString(newSymbol);
    }
    private void substituteStartingSymbol() {
        String oldStartingSymbol = startingCharacter;
        String newStartingSymbol = getNewNonTerminal(Arrays.asList(nonTerminalVariables));

        // Update the starting symbol in productions
        for (Production production : productions) {
            if (production.getLeftSide().equals(oldStartingSymbol)) {
                production.setLeftSide(newStartingSymbol);
            }
        }

        // Update the starting symbol
        startingCharacter = newStartingSymbol;
    }
    private List<Production> getProductionsForSymbol(String symbol) {
        List<Production> productionsList = new ArrayList<>();
        for (Production production : productions) {
            if (production.getLeftSide().equals(symbol)) {
                productionsList.add(production);
            }
        }
        return productionsList;
    }
    private void removeInaccessibleSymbols() {
        Set<String> reachableSymbols = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();

        stack.push(startingCharacter);

        while (!stack.isEmpty()) {
            String symbol = stack.pop();

            if (!reachableSymbols.contains(symbol)) {
                reachableSymbols.add(symbol);
                List<Production> productionsList = getProductionsForSymbol(symbol);
                for (Production production : productionsList) {
                    for (char c : production.getRightSide().toCharArray()) {
                        if (Character.isUpperCase(c)) {
                            stack.push(String.valueOf(c));
                        }
                    }
                }
            }
        }

        List<Production> newProductionsList = new ArrayList<>();
        for (Production production : productions) {
            if (reachableSymbols.contains(production.getLeftSide())) {
                newProductionsList.add(production);
            }
        }

        productions = newProductionsList.toArray(new Production[newProductionsList.size()]);
        List<String> newNonTerminalVariables = new ArrayList<>();
        for (String symbol : nonTerminalVariables) {
            if (reachableSymbols.contains(symbol)) {
                newNonTerminalVariables.add(symbol);
            }
        }
        nonTerminalVariables = newNonTerminalVariables.toArray(new String[newNonTerminalVariables.size()]);
    }

    private void removeEpsilonProductions() {
        Set<String> nullableNonTerminals = new HashSet<>();
        for (Production production : productions) {
            if (production.getRightSide().equals("ε")) {
                nullableNonTerminals.add(production.getLeftSide());
            }
        }

        if (nullableNonTerminals.isEmpty()) {
            return;
        }

        List<Production> newProductions = new ArrayList<>();
        for (Production production : productions) {
            if (production.getRightSide().equals("ε")) {
                continue;
            }

            String rightSide = production.getRightSide();
            List<String> replacements = generateEpsilonReplacements(rightSide, nullableNonTerminals);
            for (String replacement : replacements) {
                newProductions.add(new Production(production.getLeftSide(), replacement));
            }
        }

        productions = newProductions.toArray(new Production[0]);
        nonTerminalVariables = Arrays.stream(nonTerminalVariables)
                .filter(nonTerminal -> !nullableNonTerminals.contains(nonTerminal))
                .toArray(String[]::new);
    }

    private List<String> generateEpsilonReplacements(String rightSide, Set<String> nullableNonTerminals) {
        List<String> replacements = new ArrayList<>();
        int n = rightSide.length();
        int powSetSize = (int) Math.pow(2, n);

        for (int counter = 1; counter < powSetSize; counter++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < n; j++) {
                if (((counter >> j) & 1) == 1) {
                    char symbol = rightSide.charAt(j);
                    if (!nullableNonTerminals.contains(String.valueOf(symbol))) {
                        sb.append(symbol);
                    }
                }
            }
            replacements.add(sb.toString());
        }

        return replacements;
    }

    private void removeUnitProductions() {
        List<Production> unitProductions = new ArrayList<>();
        for (Production production : productions) {
            if (production.isUnitProduction()) {
                unitProductions.add(production);
            }
        }

        List<Production> newProductions = new ArrayList<>();
        for (Production unitProduction : unitProductions) {
            String unitProductionRightSide = unitProduction.getRightSide();
            for (Production production : productions) {
                if (production.getLeftSide().equals(unitProductionRightSide)) {
                    newProductions.add(new Production(unitProduction.getLeftSide(), production.getRightSide()));
                }
            }
        }

        productions = newProductions.toArray(new Production[0]);
    }

    private void removeNonproductiveSymbols() {
        Set<String> productiveSymbols = new HashSet<>();
        Set<String> nonproductiveSymbols = new HashSet<>(Arrays.asList(nonTerminalVariables));

        boolean changes;
        do {
            changes = false;
            for (Production production : productions) {
                String leftSide = production.getLeftSide();
                String rightSide = production.getRightSide();
                boolean isProductive = true;

                for (int i = 0; i < rightSide.length(); i++) {
                    char symbol = rightSide.charAt(i);
                    if (Character.isUpperCase(symbol) && !productiveSymbols.contains(String.valueOf(symbol))) {
                        isProductive = false;
                        break;
                    }
                }

                if (isProductive && !productiveSymbols.contains(leftSide)) {
                    productiveSymbols.add(leftSide);
                    nonproductiveSymbols.remove(leftSide);
                    changes = true;
                }
            }
        } while (changes);

        List<Production> newProductions = new ArrayList<>();
        for (Production production : productions) {
            if (productiveSymbols.contains(production.getLeftSide())) {
                newProductions.add(production);
            }
        }

        productions = newProductions.toArray(new Production[0]);
        nonTerminalVariables = productiveSymbols.toArray(new String[0]);
    }

        @Override
    public String toString() {
        return "Grammar {" + "\n" +
                "\tVn (Non-terminal) = " + Arrays.toString(this.nonTerminalVariables) + "\n" +
                "\tVt (Terminal) = " + Arrays.toString(this.terminalVariables) + "\n" +
                "\tP (Productions) = " + Arrays.toString(this.productions) + "\n" +
                "\tS (Starting character) = " + this.startingCharacter + "\n" +
                '}';
    }
}