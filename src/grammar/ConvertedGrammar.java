package grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConvertedGrammar {

    private final Set<String> nonTerminalSymbols;
    private final Set<String> terminalSymbols;
    private final HashMap<String, ArrayList<String>> productions;
    private final String initialSymbol;

    public ConvertedGrammar(Set<String> nonTerminalSymbols, Set<String> terminalSymbols, HashMap<String, ArrayList<String>> productions, String initialSymbol) {
        this.nonTerminalSymbols = nonTerminalSymbols;
        this.terminalSymbols = terminalSymbols;
        this.productions = productions;
        this.initialSymbol = initialSymbol;
    }

    public Set<String> getNonTerminalSymbols() {
        return nonTerminalSymbols;
    }

    public Set<String> getTerminalSymbols() {
        return terminalSymbols;
    }

    public HashMap<String, ArrayList<String>> getProductions() {
        return productions;
    }

    public String getInitialSymbol() {
        return initialSymbol;
    }

    public Grammar convertToGrammar() {
        String[] nonTerminalVariables = nonTerminalSymbols.toArray(new String[0]);
        String[] terminalVariables = terminalSymbols.toArray(new String[0]);

        ArrayList<Production> productionList = new ArrayList<>();
        for (Map.Entry<String, ArrayList<String>> entry : productions.entrySet()) {
            String lhs = entry.getKey();
            ArrayList<String> rhsList = entry.getValue();
            for (String rhs : rhsList) {
                productionList.add(new Production(lhs, rhs));
            }
        }
        Production[] productionsArray = productionList.toArray(new Production[0]);

        return new Grammar(nonTerminalVariables, terminalVariables, productionsArray, initialSymbol);
    }
}
