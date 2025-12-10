package org.example.model;

import java.io.Serial;

public final class Stock extends Investment {

    private final String tickerSymbol;
    private final int shares;
    private final double currentSharePrice;
    private final double annualDividendPerShare;
    @Serial
    private static final long serialVersionUID = 1L;
    private final String sector;

    public Stock(String id, String name, String tickerSymbol, int shares, double currentSharePrice, double annualDividendPerShare, String sector) {
        super(id, name);
        this.tickerSymbol = tickerSymbol;
        this.shares = shares;
        this.currentSharePrice = currentSharePrice;
        this.annualDividendPerShare = annualDividendPerShare;
        this.sector = sector;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public int getShares() {
        return shares;
    }

    public double getCurrentSharePrice() {
        return currentSharePrice;
    }

    public double getAnnualDividendPerShare() {
        return annualDividendPerShare;
    }

    @Override
    public double calculateCurrentValue() {
        return shares * currentSharePrice;
    }

    @Override
    public double getProjectedAnnualReturn() {
        return shares * annualDividendPerShare;
    }
}
