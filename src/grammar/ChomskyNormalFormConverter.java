package grammar;

import java.util.*;

public class ChomskyNormalFormConverter {
    private static ConvertedGrammar grammar;
    private static HashMap<String, ArrayList<String>> newProductions = new HashMap<>();
    private static HashMap<String, String> newProdLookUp = new HashMap<>();
    private static int count = 0;

    public ChomskyNormalFormConverter(Grammar grammar) {
        ConvertedGrammar convertedGrammar = grammar.convertToConvertedGrammar();
        this.grammar =  convertedGrammar;
    }


    public void removeEpsilonProductions() {
        Set<String> emptyProductions = new HashSet<>();

        // Find all empty productions and remove them from the original productions
        for (String left : this.grammar.getProductions().keySet()) {
            List<String> productions = this.grammar.getProductions().get(left);
            if (productions.remove("ε")) {
                emptyProductions.add(left);
            }
        }

        // Find all non-terminal symbols with at least one occurrence of an empty production
        Set<String> nullableSymbols = new HashSet<>();
        for (String left : grammar.getProductions().keySet()) {
            List<String> productions = grammar.getProductions().get(left);
            for (String production : productions) {
                if (emptyProductions.stream().anyMatch(production::contains)) {
                    nullableSymbols.add(left);
                    break;
                }
            }
        }

        // Replace all occurrences of empty productions with all possible combinations of non-empty productions
        for (String emptyProduction : emptyProductions) {
            for (String left : nullableSymbols) {
                List<String> productions = grammar.getProductions().get(left);
                List<String> newProductions = new ArrayList<>();
                for (int i = 0; i < productions.size(); i++) {
                    String production = productions.get(i);
                    if (production.contains(emptyProduction)) {
                        productions.remove(i);
                        i--;
                        newProductions.addAll(generateProductions(production, emptyProduction));
                    }
                }
                productions.addAll(newProductions);
            }
        }
    }


    public static List<String> generateProductions(String production, String nullableSymbol) {
        List<String> newProductions = new ArrayList<>();
        List<Integer> nullablePositions = new ArrayList<>();

        // Find all positions of the nullable symbol in the production
        for (int i = 0; i < production.length(); i++) {
            if (nullableSymbol.charAt(0) == production.charAt(i)) {
                nullablePositions.add(i);
            }
        }

        // Generate all combinations of positions to remove the nullable symbol
        List<List<Integer>> positionCombinations = generateCombinations((ArrayList<Integer>) nullablePositions);
        for (List<Integer> positions : positionCombinations) {
            newProductions.add(removeCharsAtPositions(production, positions));
        }

        // Add the original production to the new productions list
        newProductions.add(production);
        return newProductions;
    }


    private static List<List<Integer>> generateCombinations(ArrayList<Integer> nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(result, new ArrayList<>(), nums, 0);
        return result;
    }


    private static void backtrack(List<List<Integer>> result, List<Integer> tempList, ArrayList<Integer> nums, int start) {
        if (tempList.size() > 0) {
            result.add(new ArrayList<>(tempList));
        }
        for (int i = start; i < nums.size(); i++) {
            tempList.add(nums.get(i));
            backtrack(result, tempList, nums, i + 1);
            tempList.remove(tempList.size() - 1);
        }
    }


    private static String removeCharsAtPositions(String inputString, List<Integer> positions) {
        StringBuilder stringBuilder = new StringBuilder(inputString);
        Collections.sort(positions, Collections.reverseOrder());
        for (int position : positions) {
            stringBuilder.deleteCharAt(position);
        }
        return stringBuilder.toString();
    }


    private void removeUnitProductions() {
        for (String key : grammar.getProductions().keySet()) {
            ArrayList<String> prodList = grammar.getProductions().get(key);
            ArrayList<String> newProdList = new ArrayList<>(); // New list to hold CNF converted productions
            for (String prod : prodList) {
                if (prod.length() == 1 && Character.isUpperCase(prod.charAt(0))) { // check when we have unit A -> B
                    ArrayList<String> unitProdList = grammar.getProductions().get(prod); // get productions corresponding to B
                    for (String unitProd : unitProdList) {
                        // If the unit production is of length 1 and contains an uppercase symbol, it needs further conversion
                        if (unitProd.length() == 1 && Character.isUpperCase(unitProd.charAt(0))) {
                            ArrayList<String> unitProdList2 = grammar.getProductions().get(unitProd); // get productions corresponding to the uppercase symbol in B
                            for (String unitProd2 : unitProdList2) {
                                String newProd = unitProd2; // new production in CNF format
                                newProdList.add(newProd);
                            }
                        } else {
                            newProdList.add(unitProd); // add the unit production directly to the new production list
                        }
                    }
                } else {
                    newProdList.add(prod); // add the original production directly to the new production list
                }
            }
            grammar.getProductions().put(key, newProdList); // update the production list for the current symbol
        }
    }


    public void removeInaccessibleProduction() {
        // Get the start symbol
        String startSymbol = grammar.getInitialSymbol();

        // Initialize the set of reachable symbols with the start symbol
        Set<String> reachableSymbols = new HashSet<>();
        reachableSymbols.add(startSymbol);

        // Use a queue to perform breadth-first search to find reachable symbols
        Queue<String> queue = new LinkedList<>();
        queue.add(startSymbol);

        // Perform breadth-first search
        while (!queue.isEmpty()) {
            String symbol = queue.poll();
            List<String> productions = grammar.getProductions().get(symbol);
            if (productions != null) {
                for (String production : productions) {
                    String[] symbols = production.split("\\s+");
                    for (String sym : symbols) {
                        if (!reachableSymbols.contains(sym)) {
                            reachableSymbols.add(sym);
                            queue.add(sym);
                        }
                    }
                }
            }
        }

        // Remove inaccessible productions
        for (Iterator<Map.Entry<String, ArrayList<String>>> iterator = grammar.getProductions().entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, ArrayList<String>> entry = iterator.next();
            if (!reachableSymbols.contains(entry.getKey())) {
                iterator.remove(); // Use the iterator's remove() method
            } else {
                List<String> productions = entry.getValue();
                for (Iterator<String> prodIterator = productions.iterator(); prodIterator.hasNext();) {
                    String prod = prodIterator.next();
                    if (removeNonproductiveSymbols(entry.getKey(), prod, (ArrayList<String>) productions)) {
                        prodIterator.remove(); // Use the prodIterators remove() method
                    }
                }
            }
        }
    }


    private boolean removeNonproductiveSymbols(String nonTerminalSymbol, String production, ArrayList<String> productionList) {
        List<String> symbolList = grammar.getProductions().get(nonTerminalSymbol);
        if (symbolList == null) {
            productionList.remove(production);
            return true;
        }
        boolean hasTerminalSymbol = false;
        boolean hasNonTerminalSymbol = false;

        for (String symbol : symbolList) {
            if (symbol.length() == 1 && Character.isLowerCase(symbol.charAt(0))) {
                hasTerminalSymbol = true;
            } else if (symbol.contains(nonTerminalSymbol)) {
                hasNonTerminalSymbol = true;
            }
        }
        if (!hasTerminalSymbol && hasNonTerminalSymbol) {
            productionList.remove(production);
            grammar.getProductions().remove(nonTerminalSymbol);
            return true;
        }
        return false;
    }

    public void convertToChomskyNormalForm() {
        HashMap<String, String> terminalNewProdMap = new HashMap<>();
        for (String terminalSymbol : grammar.getTerminalSymbols()) {
            String newNonTerminalSymbol = generateNewNonTerminal();
            grammar.getProductions().put(newNonTerminalSymbol, new ArrayList<>(Collections.singletonList(terminalSymbol)));
            terminalNewProdMap.put(terminalSymbol, newNonTerminalSymbol);
        }

        for (String productionLeft : grammar.getProductions().keySet()) {
            ArrayList<String> productionList = grammar.getProductions().get(productionLeft);

            for (int i = 0; i < productionList.size(); i++) {
                StringBuilder newRightProd = new StringBuilder();
                newRightProd.append(productionList.get(i));

                for (String terminalSymbol : grammar.getTerminalSymbols()) {
                    int index = newRightProd.indexOf(terminalSymbol);

                    if (newRightProd.length() > 1) {
                        while (index >= 0) {
                            newRightProd.replace(index, index + 1, terminalNewProdMap.get(terminalSymbol));
                            index = newRightProd.indexOf(terminalSymbol);
                        }
                    }
                }
                productionList.set(i, newRightProd.toString());
            }
            productionList.replaceAll(ChomskyNormalFormConverter::groupProductions);
        }
        grammar.getProductions().putAll(newProductions);
    }

    private static String generateNewNonTerminal(){
        return "X" + count++;
    }
    private static String groupProductions(String prod) {
        int xCount = 0;
        for (char s : prod.toCharArray()) {
            if (s == 'X') {
                xCount++;
            }
        }

        while (prod.length() - xCount > 2) {
            int appendCount = 0;
            StringBuilder newGroup = new StringBuilder();
            for (int i = 0; i < prod.length(); i++) {
                if (appendCount < 2) {
                    if (prod.charAt(i) == 'X') {
                        appendCount++;
                        newGroup.append(prod, i, i + 2);
                        i++;
                    } else {
                        appendCount++;
                        newGroup.append(prod.charAt(i));
                    }
                }
            }

            String newGroupStr = newGroup.toString();
            if (newProdLookUp.containsKey(newGroupStr)) {
                prod = prod.replace(newGroupStr, newProdLookUp.get(newGroupStr));
            } else {
                String newLP = generateNewNonTerminal();
                newProductions.put(newLP, new ArrayList<>(Arrays.asList(newGroupStr.split(","))));
                newProdLookUp.put(newGroupStr, newLP);
                grammar.getNonTerminalSymbols().add(newLP);
                prod = prod.replace(newGroupStr, newLP);
            }

            xCount = 0;
            for (char s : prod.toCharArray()) {
                if (s == 'X') {
                    xCount++;
                }
            }
        }
        return prod;
    }


    public Grammar getGrammar(Grammar grammar){

        this.grammar = grammar.convertToConvertedGrammar();
        removeEpsilonProductions();
        removeUnitProductions();
        removeInaccessibleProduction();
        convertToChomskyNormalForm();
        return this.grammar.convertToGrammar();
    }
}
