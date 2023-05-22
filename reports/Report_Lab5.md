# Parser & Building an Abstract Syntax Tree
## Course: Formal Languages & Finite Automata
## Author: Corețchi Mihai FAF-211

## Theory
A parser is a compiler component that deconstructs data into smaller parts from the lexical analysis stage. It processes a sequence of tokens and generates a parse tree. An Abstract Syntax Tree is a tree-like representation of the abstract syntax of source code. Each tree node represents a source code construct. Abstract Syntax Trees are crucial in compilers, serving as data structures to depict program structure. Typically, an Abstract Syntax Tree is the outcome of a compiler's syntax analysis phase. It often acts as an intermediate program representation across several compiler stages, significantly influencing the compiler's final output.
## Objectives:
- Get familiar with parsing, what it is and how it can be programmed.

- Get familiar with the concept of AST.

- In addition to what has been done in the 3rd lab work do the following:
    - In case you didn't have a type that denotes the possible types of tokens you need to:
      - Have a type TokenType (like an enum) that can be used in the lexical analysis to categorize the tokens.
      - Please use regular expressions to identify the type of the token.
    - Implement the necessary data structures for an AST that could be used for the text you have processed in the 3rd lab work.
    - Implement a simple parser program that could extract the syntactic information from the input text.

## Implementation description
### parse
This method evaluates an arithmetic expression and returns the result as a float. It first calls the expression() method to calculate the result. Then, it checks if the next token is an assignment operator ('='). If it is, it moves to the next token and checks if it's the end of the input. If it is, it returns the result. If these conditions are not met, it throws an exception indicating an incomplete expression.
```java
public Float parse() {
Float result = expression();

        if (position < tokens.size() && tokens.get(position).getType() == TokenType.ASSIGNMENT) {
            position++;
            if (position < tokens.size() && tokens.get(position).getType() == TokenType.END_OF_INPUT) {
                return result;
            }
        }

        throw new IllegalArgumentException("Incomplete expression");
    }
```

### expression
This method evaluates an arithmetic expression that involves addition and subtraction operations. It starts by calling the term() method to get the initial value. Then, it enters a loop where it checks each token in the expression. If the token is a plus sign, it increments the position, adds the next term to the value, and continues the loop. If the token is a minus sign, it increments the position, subtracts the next term from the value, and continues the loop. If the token is neither a plus nor a minus sign, or if there are no more tokens, it returns the current value.
```java
private float expression() {
float value = term();

        while (true) {
            if (position >= tokens.size()) {
                return value;
            }

            Token token = tokens.get(position);

            switch (token.getType()) {
                case PLUS:
                    position++;
                    value += term();
                    break;
                case MINUS:
                    position++;
                    value -= term();
                    break;
                default:
                    return value;
            }
        }
    }
```
### term
This method evaluates a part of the arithmetic expression that involves multiplication and division operations. It starts by calling the factor() method to get the initial value. Then, it enters a loop where it checks each token in the expression. If the token is a multiplication sign, it increments the position, multiplies the current value by the next factor, and continues the loop. If the token is a division sign, it increments the position, divides the current value by the next factor, and continues the loop. If the token is neither a multiplication nor a division sign, or if there are no more tokens, it returns the current value.
```java
private float term() {
float value = factor();

        while (true) {
            if (position >= tokens.size()) {
                return value;
            }

            Token token = tokens.get(position);

            switch (token.getType()) {
                case MULTIPLY:
                    position++;
                    value *= factor();
                    break;
                case DIVIDE:
                    position++;
                    value /= factor();
                    break;
                default:
                    return value;
            }
        }
    }
```
### factor
This method processes individual numbers in the expression. If there are no more tokens, it throws an error. If the current token is a number, it converts it to a float and returns it. If the token isn't a number, it throws an error.
```java
private float factor() {
if (position >= tokens.size()) {
throw new IllegalArgumentException("Unexpected end of input");
}

        Token token = tokens.get(position++);

        if (token.getType() == TokenType.NUMBER) {
            return Float.parseFloat(token.getLexeme());
        }

        throw new IllegalArgumentException("Unexpected token: " + token);
    }
```

## Results

---------------------------------
Lab 5

`2+3*4-6/5=12.8`

`1*2+3=5.0`

`1+2+3+4+5/1/2/3/4/5-1-2-3-4-5*1*2*3*4*5=-599.9583`

---------------------------------

## Conclusions
In conclusion, this laboratory work has been an enriching experience. I've had the opportunity to delve into the intricacies of creating a parser in Java, which is a crucial component in the process of compiling and interpreting code. I've learned how to break down complex expressions into manageable tokens, and how to evaluate these tokens according to the rules of arithmetic. I've also gained a deeper understanding of how to handle different types of data, such as integers and floating-point numbers, and how to manage unexpected input. This work has certainly deepened my appreciation for the complexity and elegance of compiler design. I'm looking forward to applying these concepts in future projects.