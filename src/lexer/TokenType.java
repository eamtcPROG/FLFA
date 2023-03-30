package lexer;

public enum TokenType {
    // Operators
    PLUS, MINUS, MULTIPLY, DIVIDE, ASSIGNMENT,

    // Punctuation
    LEFT_PAREN, RIGHT_PAREN,

    // Literals
    NUMBER, WHITESPACE,

    // End of input
    END_OF_INPUT
}
