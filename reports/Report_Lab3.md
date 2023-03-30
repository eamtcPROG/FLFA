# Lexer & Scanner.
## Course: Formal Languages & Finite Automata
## Author: Corețchi Mihai FAF-211

## Theory
A Lexer, also called a lexical analyzer, is a software module that analyzes source code and separates it into individual tokens according to the syntax rules of a programming language. It is usually the initial phase of a compiler or interpreter and is accountable for carrying out the lexical analysis of the source code.
## Objectives:
- Understand what lexical analysis is.

- Get familiar with the inner workings of a lexer/scanner/tokenizer.

- Implement a sample lexer and show how it works.

## Implementation description
### TokenType
This code defines an enumeration type named TokenType in Java.
Each constant in the TokenType enumeration represents a specific type of token that might be encountered in source code.
```java
public enum TokenType {
    PLUS, MINUS, MULTIPLY, DIVIDE, ASSIGNMENT,
    LEFT_PAREN, RIGHT_PAREN,
    NUMBER, WHITESPACE,
    END_OF_INPUT
}
```
### tokenize
This code defines a method called tokenize(). The purpose of this method is to read a string of input and break it down into individual tokens.
The method creates an empty list of tokens, and then uses a loop to iterate through the characters of the input string. For each character, it attempts to match it against each of the token types defined in the TokenType enum.
```java
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        int position = 0;

        while (position < this.input.length()) {
            int maxMatchLength = 0;
            TokenType tokenType = null;
    
            for (TokenType type : TokenType.values()) {
                String pattern = getTokenPattern(type);
        
                if (pattern != null) {
                    Pattern regex = Pattern.compile(pattern);
                    Matcher matcher = regex.matcher(input.substring(position));
            
                    if (matcher.lookingAt()) {
                        int matchLength = matcher.end();
                
                        if (matchLength > maxMatchLength) {
                            maxMatchLength = matchLength;
                            tokenType = type;
                        }
                    }
                }
            }

            if (maxMatchLength == 0) {
                throw new IllegalArgumentException("Invalid input at position " + position);
            }

            String lexeme = input.substring(position, position + maxMatchLength);
            tokens.add(new Token(tokenType, lexeme));
            position += maxMatchLength;
        }

        tokens.add(new Token(TokenType.END_OF_INPUT, ""));
        return tokens;
    }
```

### getTokenPattern
This method takes a TokenType as an input parameter and returns a regular expression pattern that matches a lexeme of that token type.
```java
private String getTokenPattern(TokenType type) {
        switch (type) {
            case NUMBER:
                return "[0-9]*";
            case PLUS:
                return "\\+";
            case MINUS:
                return "-";
            case MULTIPLY:
                return "\\*";
            case DIVIDE:
                return "/";
            case LEFT_PAREN:
                return "\\(";
            case RIGHT_PAREN:
                return "\\)";
            case WHITESPACE:
                return "\\s+";
            case ASSIGNMENT:
                return "=";
            default:
            return null;
        }
}
```



## Results
1. Test: 5 + (1 - 8) *3 / 9 = 3
   Tokens:
   NUMBER (5)
   WHITESPACE ( )
   PLUS (+)
   WHITESPACE ( )
   LEFT_PAREN (()
   NUMBER (1)
   WHITESPACE ( )
   MINUS (-)
   WHITESPACE ( )
   NUMBER (8)
   RIGHT_PAREN ())
   WHITESPACE ( )
   MULTIPLY (*)
   NUMBER (3)
   WHITESPACE ( )
   DIVIDE (/)
   WHITESPACE ( )
   NUMBER (9)
   WHITESPACE ( )
   ASSIGNMENT (=)
   WHITESPACE ( )
   NUMBER (3)
   END_OF_INPUT ()

2. Test: 8 - 3 * 3 / 1 = 2
   Tokens:
   NUMBER (8)
   WHITESPACE ( )
   MINUS (-)
   WHITESPACE ( )
   NUMBER (3)
   WHITESPACE ( )
   MULTIPLY (*)
   WHITESPACE ( )
   NUMBER (3)
   WHITESPACE ( )
   DIVIDE (/)
   WHITESPACE ( )
   NUMBER (1)
   WHITESPACE ( )
   ASSIGNMENT (=)
   WHITESPACE ( )
   NUMBER (2)
   END_OF_INPUT ()

3. Test: (10-31)*100/12 = 2
   Tokens:
   LEFT_PAREN (()
   NUMBER (10)
   MINUS (-)
   NUMBER (31)
   RIGHT_PAREN ())
   MULTIPLY (*)
   NUMBER (100)
   DIVIDE (/)
   NUMBER (12)
   WHITESPACE ( )
   ASSIGNMENT (=)
   WHITESPACE ( )
   NUMBER (2)
   END_OF_INPUT ()
## Conclusions
In this lab, we have learned about the Lexer, Token, and TokenType concepts. We have seen how a Lexer can be used to break down source code into individual tokens based on the syntax rules of a programming language. We have also learned how to define a TokenType using an enum, create a Token class to hold information about each token, and tokenize the input using regular expressions. By understanding these concepts and their implementation.