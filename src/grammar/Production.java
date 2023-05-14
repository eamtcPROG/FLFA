package grammar;

public class Production {
    private String leftSide;
    private String rightSide;

    public Production(String leftSide, String rightSide) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }

    public boolean isUnitProduction() {
        return rightSide.length() == 1 && Character.isUpperCase(rightSide.charAt(0));
    }
    public boolean hasSymbolOnRight(String symbol) {
        return rightSide.contains(symbol);
    }
    public String getLeftSide() {
        return this.leftSide;
    }
    public String getRightSide() {
        return this.rightSide;
    }
    @Override
    public String toString() {
        return this.leftSide + " -> " + this.rightSide;
    }

    public void setLeftSide(String newStartingSymbol) {
        this.leftSide = newStartingSymbol;
    }
}