package org.example.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public final class Stock extends Investment {

    private final String tickerSymbol;
    private final int shares;
    private final double currentSharePrice;
    private final double annualDividendPerShare;

    @Override
    public double calculateCurrentValue() {
        return shares * currentSharePrice;
    }

    @Override
    public double getProjectedAnnualReturn() {
        return shares * annualDividendPerShare;
    }

    @Override
    public void validate() {
        if(shares<0) throw new IllegalArgumentException("Shares must be positive");
        if(currentSharePrice<0) throw new IllegalArgumentException("Current share price must be positive");
        if(annualDividendPerShare<0) throw new IllegalArgumentException("Annual dividend per share must be positive");
    }
}
