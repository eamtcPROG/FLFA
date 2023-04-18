package grammar;

import automaton.FiniteAutomaton;
import automaton.Transition;

import java.util.*;

public class Grammar {
    private String[] nonTerminalVariables;
    private final String[] terminalVariables;
    private Production[] productions;
    private final String startingCharacter;

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
        this.removeEpsilonProductions();
        this.removeUnitProductions();
        this.removeNonproductiveSymbols();
        this.removeInaccessibleSymbols();
        this.toChomskyNormalFormStep();
    }

    private void removeEpsilonProductions() {
        String nullableNonTerminal = "";
        for (Production production : productions) {
            if (production.getRightSide().equals("ε")) {
                nullableNonTerminal = production.getLeftSide();
            }
        }

        if (nullableNonTerminal.equals("")) {
            System.out.println("No nullable non-terminals");
            return;
        }

        List<Production> newProductions = new ArrayList<>();
        for (Production production : productions) {
            if (production.getRightSide().equals("ε")) {
                continue;
            }

            if (production.getRightSide().contains(nullableNonTerminal)) {
                String newRightSide = production.getRightSide().replace(nullableNonTerminal, "");
                newProductions.add(production);
                newProductions.add(new Production(production.getLeftSide(), newRightSide));
                continue;
            }

            newProductions.add(production);
        }

        this.productions = new Production[newProductions.size()];
        newProductions.toArray(productions);
    }

    private void removeUnitProductions() {
        // Find all unit productions
        List<Production> unitProductions = new ArrayList<>();
        for (Production production : productions) {
            if (production.isUnitProduction()) {
                unitProductions.add(production);
            }
        }

        List<Production> newProductions = new ArrayList<>();
        for (Production unitProduction : unitProductions) {
            for (Production production : productions) {

                if (production.getLeftSide().equals(unitProduction.getRightSide())) {

                    if (production.isUnitProduction()) {
                        for (Production p : productions) {
                            if (p.getLeftSide().equals(production.getRightSide())) {
                                newProductions.add(new Production(unitProduction.getLeftSide(), p.getRightSide()));
                            }
                        }

                        continue;
                    }

                    newProductions.add(new Production(unitProduction.getLeftSide(), production.getRightSide()));
                    continue;
                }

                if (production.isUnitProduction()) {
                    continue;
                }

                if(!newProductions.contains(production)) {
                    newProductions.add(production);
                }
            }
        }

        productions = new Production[newProductions.size()];
        newProductions.toArray(productions);
    }


    private void removeNonproductiveSymbols() {
        Map<String, Boolean> productiveSymbols = new HashMap<>();
        for (String nonTerminal : nonTerminalVariables) {
            productiveSymbols.put(nonTerminal, false);
        }

        for (Production production : productions) {
            if (production.getRightSide().length() == 1
                    && Character.isLowerCase(production.getRightSide().charAt(0))) {
                productiveSymbols.put(production.getLeftSide(), true);
            }
        }

        for (Production production : productions) {
            String rightSide = production.getRightSide();
            boolean isProductive = true;

            for (int i = 0; i < rightSide.length(); i++) {
                char symbol = rightSide.charAt(i);
                if (Character.isUpperCase(symbol) && !productiveSymbols.get(String.valueOf(symbol))) {
                    isProductive = false;
                    break;
                }
            }

            if (isProductive) {
                productiveSymbols.put(production.getLeftSide(), true);
            }
        }

        List<Production> newProductions = new ArrayList<>();
        for (Production production : productions) {
            if(productiveSymbols.get(production.getLeftSide())) {
                newProductions.add(production);
            }
        }
        productions = new Production[newProductions.size()];
        newProductions.toArray(productions);
    }

    public void removeInaccessibleSymbols() {
        Set<String> reachableSymbols = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();

        stack.push(this.startingCharacter);

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
        for (Production production : this.productions) {

            if (reachableSymbols.contains(production.getLeftSide())) {
                String rightSide = production.getRightSide();
                StringBuilder newRightSide = new StringBuilder();
                for (char c : rightSide.toCharArray()) {
                    if (reachableSymbols.contains(String.valueOf(c)) || Arrays.asList(this.terminalVariables).contains(String.valueOf(c))) {
                        newRightSide.append(c);
                    }
                }

                newProductionsList.add(new Production(production.getLeftSide(), newRightSide.toString()));
            }
        }

        this.productions = newProductionsList.toArray(new Production[0]);
        this.nonTerminalVariables = reachableSymbols.toArray(new String[0]);
    }

    private List<Production> getProductionsForSymbol(String symbol) {
        List<Production> productionsList = new ArrayList<>();
        for (Production production : this.productions) {
            if (production.getLeftSide().equals(symbol)) {
                productionsList.add(production);
            }
        }
        return productionsList;
    }

    public void toChomskyNormalFormStep() {
        List<String> newNonTerminalVariables = new ArrayList<>(Arrays.asList(this.nonTerminalVariables));
        List<Production> newProductions = new ArrayList<>();
        Map<String, String> ProductionsMap = new HashMap<>();

        char newNonTerminalStart = 'Y';

        for (Production production : productions) {
            if(production.getRightSide().length() > 2 &&
                    production.getRightSide().matches("[A-Z][A-Z][A-Z]+")) {
                String oldNonTerminal = production.getRightSide().substring(0, 2);

                if (ProductionsMap.containsKey(oldNonTerminal)) {
                    newProductions.add(new Production(production.getLeftSide(),
                            ProductionsMap.get(oldNonTerminal) + production.getRightSide().substring(2)));
                    continue;
                }

                String newNonTerminal = String.valueOf(newNonTerminalStart);
                ProductionsMap.put(oldNonTerminal, newNonTerminal);
                newNonTerminalVariables.add(newNonTerminal);
                newProductions.add(new Production(production.getLeftSide(),
                        newNonTerminal + production.getRightSide().substring(2)));
                newNonTerminalStart++;

                continue;
            }

            if (production.getRightSide().matches("[a-z][A-Z]")) {
                String oldTerminal = production.getRightSide().substring(0, 1);

                if (ProductionsMap.containsKey(oldTerminal)) {
                    newProductions.add(new Production(production.getLeftSide(),
                            ProductionsMap.get(oldTerminal) + production.getRightSide().substring(1)));
                    continue;
                }

                String newNonTerminal = String.valueOf(newNonTerminalStart);
                ProductionsMap.put(oldTerminal, newNonTerminal);
                newNonTerminalVariables.add(newNonTerminal);
                newProductions.add(new Production(production.getLeftSide(),
                        newNonTerminal + production.getRightSide().substring(1)));
                newNonTerminalStart++;

                continue;
            }

            newProductions.add(production);
        }

        for (Map.Entry<String, String> entry : ProductionsMap.entrySet()) {
            String oldProduction = entry.getKey();
            String newProduction = entry.getValue();
            newProductions.add(new Production(newProduction, oldProduction));
        }

        this.productions = newProductions.toArray(new Production[0]);
        this.nonTerminalVariables = newNonTerminalVariables.toArray(new String[0]);
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