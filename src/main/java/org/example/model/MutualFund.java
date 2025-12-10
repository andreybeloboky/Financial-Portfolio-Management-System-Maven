package org.example.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public final class MutualFund extends Investment {

    private final String fundCode;
    private final double unitsHeld;
    private final double currentNAV;
    private final double avgAnnualDistribution;

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public double calculateCurrentValue() {
        return unitsHeld * currentNAV;
    }

    @Override
    public double getProjectedAnnualReturn() {
        return unitsHeld * avgAnnualDistribution;
    }

}
