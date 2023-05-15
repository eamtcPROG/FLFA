# Chomsky Normal Form
## Course: Formal Languages & Finite Automata
## Author: Corețchi Mihai FAF-211
## Variant: 10

## Theory
Chomsky Normal Form is a method for expressing context-free grammar in a distinct format, where every production takes one of two forms: A → BC or A → a, with A, B, and C being non-terminal symbols and a representing a terminal symbol. This format streamlines the examination and handling of grammars in specific algorithms and proves valuable for parsing natural language.
## Objectives:
- Learn about Chomsky Normal Form (CNF)

- Get familiar with the approaches of normalizing a grammar.

- Implement a method for normalizing an input grammar by the rules of CNF.
    - Implement a method for normalizing an input grammar by the rules of CNF. The implementation needs to be encapsulated in a method with an appropriate signature (also ideally in an appropriate class/type):
    - The implemented functionality needs executed and tested.
    - A BONUS point will be given for the student who will have unit tests that validate the functionality of the project.
    - Also, another BONUS point would be given if the student will make the aforementioned function to accept any grammar, not only the one from the student's variant.

## Implementation description
### removeEpsilonProductions
This method removes epsilon productions, which are productions that can derive the empty string, from a grammar. It first identifies and removes all empty productions by iterating through the grammar's productions. The left-hand side non-terminal symbols of these empty productions are stored in the emptyProductions set. Next, it determines which non-terminal symbols have at least one occurrence of an empty production by iterating through the grammar's productions again. If a production contains any non-terminal symbol from the emptyProductions set, the left-hand side non-terminal symbol is added to the nullableSymbols set. Finally, it replaces all occurrences of empty productions by iterating through the emptyProductions set and nullableSymbols set. For each combination, it generates new productions and replaces the old productions in the grammar.
```java
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
```
### removeUnitProductions
This method removes unit productions from a grammar by converting them to Chomsky Normal Form (CNF) or preserving them as they are. It iterates through each non-terminal symbol in the grammar's productions and checks for unit productions of the form A -> B. It retrieves the productions corresponding to B and iterates through them. If a unit production is found, it further checks for unit productions in the retrieved productions until non-unit productions are reached. The method creates new productions in CNF format by replacing the unit productions and adds them to a new production list. Finally, it updates the grammar's production list for each non-terminal symbol with the new production list.
```java
private void removeUnitProductions() {
        for (String key : grammar.getProductions().keySet()) {
            ArrayList<String> prodList = grammar.getProductions().get(key);
            ArrayList<String> newProdList = new ArrayList<>(); // New list to hold CNF converted productions
            for (String prod : prodList) {
                if (prod.length() == 1 && Character.isUpperCase(prod.charAt(0))) { // check when we have unit A -> B
                    ArrayList<String> unitProdList = grammar.getProductions().get(prod); // get productions corresponding to B
                    for (String unitProd : unitProdList) {
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
```

### removeNonproductiveSymbols
This method checks if a non-terminal symbol is non-productive in the grammar. It does so by examining the list of productions associated with the symbol. If the symbol has no productions, the current production is removed, indicating it is non-productive. The method also checks for the presence of terminal symbols or other non-terminal symbols within the productions of the symbol. If there are no terminal symbols but there is at least one non-terminal symbol, the current production is removed, and the non-terminal symbol is removed from the grammar's production list. Overall, the method determines if a non-terminal symbol is non-productive based on the absence of productions or the lack of terminal symbols in its productions. It updates the production list and the grammar accordingly.
```java
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
```

### removeInaccessibleSymbols
This method removes inaccessible productions from the grammar. It performs a breadth-first search (BFS) starting from the grammar's start symbol to find reachable symbols. It removes productions associated with non-reachable symbols and checks for non-productive productions in reachable symbols. The method ensures that only reachable symbols and their productive productions remain in the grammar.
```java
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
```

### convertToChomskyNormalForm
This method converts the grammar to Chomsky Normal Form (CNF). It creates new non-terminal symbols for each terminal symbol, replaces terminal symbols in productions with the corresponding new non-terminal symbols, groups productions to meet CNF requirements, and updates the grammar with the modified productions.
```java
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
```
## Results
Input Grammar

Grammar {

    Vn (Non-terminal) = [S, A, B, D]
    
    Vt (Terminal) = [a, b, d]
    
    P (Productions) = [S -> dB, S -> AB, A -> d, A -> dS, A -> aAaAb, A -> ε, B -> a, B -> aS, B -> A, D -> Aba]
    
    S (Starting character) = S

}

---------------------------
CNF grammar

Grammar {

    Vn (Non-terminal) = [A, B, S, D, X3, X4, X5, X6, X7]
    
    Vt (Terminal) = [a, b, d]
    
    P (Productions) = [S -> X2B, S -> a, S -> X0S, S -> d, S -> X2S, S -> X4X1, S -> X3X1, S -> X6X1, S -> X7X1, S -> AB, X0 -> a, X1 -> b, X2 -> d, X3 -> X0X0, X4 -> X3A, X5 -> X0A, X6 -> X5X0, X7 -> X5X5]
    
    S (Starting character) = S

}
## Conclusions
In conclusion, in this laboratory work I implemented algorithm to transform context-free grammars into Chomsky Normal Form, which is a specific form that simplifies the analysis and manipulation of grammars. The code involves manipulating productions, symbols, and productions, using data structures such as lists, maps, and sets. Overall, this lab work seemed challenging but interesting, as it involves implementing complex algorithms to transform grammars, which can be useful in natural language processing and other areas.