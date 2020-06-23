package de.aktienmathematik.domain;

public class Symbol {

    private String fullName;
    private String symbol;

    public Symbol() {}

    public Symbol(String fullName, String symbol) {
        setFullName(fullName);
        setSymbol(symbol);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
