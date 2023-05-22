package parser;

import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;

import java.util.List;

public class Parser {
    private List<Token> tokens;
    private int position = 0;

    public Parser(String input) {
        setExpression(input);
    }

    public void setExpression(String input) {
        Lexer lexer = new Lexer(input);
        this.tokens = lexer.tokenize();
        this.position = 0;
    }

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
}
