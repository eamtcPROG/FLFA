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
This method removes epsilon productions (A → ε) from a grammar. It first identifies a nullable non-terminal (A) that has an epsilon production. If no such non-terminal is found, it prints a message and returns. Otherwise, it iterates through the productions, skipping epsilon productions, and creates new productions without the nullable non-terminal. Finally, it updates the grammar's productions with the new set of productions.
```java
private void removeEpsilonProductions() {
        String nullableNonTerminal = "";
        for (Production production : productions) {
            if (production.getRightSide().equals("ε")) {
                nullableNonTerminal = production.getLeftSide();
            }
        }

        if (nullableNonTerminal.equals("")) {
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
```
### removeUnitProductions
This method eliminates unit productions (A → B) from a grammar. First, it identifies all unit productions. Then, for each unit production, it iterates through the productions and replaces the unit production with the productions that have the same left side as the unit production's right side. If a production is a unit production, it is skipped. The method then updates the grammar's productions with the new set of productions without unit productions.
```java
private void removeUnitProductions() {
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
```

### removeNonproductiveSymbols
This method eliminates nonproductive symbols from a grammar. It initializes a map with non-terminal symbols set to false (nonproductive). Then, it marks symbols as productive if their production's right side is a single lowercase letter. Next, it iterates through all productions, marking a symbol as productive if all symbols on the right side of the production are productive. Finally, it updates the grammar's productions to only include those with productive left-side symbols.
```java
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
```

### removeInaccessibleSymbols
This method is removing inaccessible symbols from the grammar. It uses a stack to keep track of reachable symbols starting from the starting character. It iterates over the stack, adding each symbol to a set of reachable symbols if it hasn't been added already. It then adds the productions for that symbol to the stack, pushing non-terminal symbols onto the stack. Finally, it creates a new list of productions containing only those whose left-hand side is a reachable symbol, and whose right-hand side consists only of reachable symbols or terminal symbols. The original list of productions and non-terminal variables is updated with these new values.
```java
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
```

### getProductionsForSymbol
This method takes a string symbol as input and returns a list of Production objects. It iterates through the productions list and adds any Production object whose leftSide property is equal to the symbol parameter to the productionsList. Finally, it returns the productionsList. 
```java
private List<Production> getProductionsForSymbol(String symbol) {
        List<Production> productionsList = new ArrayList<>();
        for (Production production : this.productions) {
            if (production.getLeftSide().equals(symbol)) {
                productionsList.add(production);
            }
        }
        return productionsList;
}
```

### toChomskyNormalForm
This method converts a context-free grammar into Chomsky Normal Form. It first creates new non-terminal variables and a new list of productions. Then, it creates a mapping between old non-terminal variables and new non-terminal variables that will be used in the final step of the conversion. Next, it iterates through each production and applies one of two rules depending on the structure of the production. If the right side of the production has more than two non-terminal variables, it replaces the first two variables with a new non-terminal variable and adds a production to the list. If the right side of the production has one terminal and one non-terminal variable, it replaces the terminal with a new non-terminal variable and adds a production to the list. Finally, it adds new productions to the list based on the mappings created earlier, and updates the list of productions and non-terminal variables of the grammar.
```java
public void toChomskyNormalForm() {
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
```
## Results
Input Grammar

Grammar {

Vn (Non-terminal) = [S, A, B, D]

Vt (Terminal) = [a, b, d]

P (Productions) = [S -> dB, S -> AB, A -> d, A -> dS, A -> aAaAb, A -> ε, B -> a, B -> aS, B -> A, D -> Aba]

S (Starting character) = S

}



CNF grammar

Grammar {

Vn (Non-terminal) = [A, B, S, Y, Z]

Vt (Terminal) = [a, b, d]

P (Productions) = [S -> YB, S -> AB, A -> d, A -> YS, A -> aAaAb, A -> aab, S -> a, S -> ZS, S -> d, S -> YS, S -> aAaAb, S -> aab, S -> , B -> d, B -> YS, B -> aAaAb, B -> aab, B -> a, B -> ZS, B -> , Z -> a, Y -> d]

S (Starting character) = S

}
## Conclusions
In conclusion, in this laboratory work I implemented algorithm to transform context-free grammars into Chomsky Normal Form, which is a specific form that simplifies the analysis and manipulation of grammars. The code involves manipulating productions, symbols, and productions, using data structures such as lists, maps, and sets. Overall, this lab work seemed challenging but interesting, as it involves implementing complex algorithms to transform grammars, which can be useful in natural language processing and other areas.