package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public final class Stock extends Investment {

    private final String tickerSymbol;
    private final int shares;
    private final double currentSharePrice;
    private final double annualDividendPerShare;
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public double calculateCurrentValue() {
        return shares * currentSharePrice;
    }

    @Override
    public double getProjectedAnnualReturn() {
        return shares * annualDividendPerShare;
    }
}
