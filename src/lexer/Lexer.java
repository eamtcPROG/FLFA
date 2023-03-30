package lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private final String input;

    public Lexer(String input) {
        this.input = input;
    }

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

    public void printTokens() {
        List<Token> tokens = this.tokenize();
        for (Token token : tokens) {
            System.out.printf("%s (%s)%n", token.getType(), token.getLexeme());
        }
    }
}
