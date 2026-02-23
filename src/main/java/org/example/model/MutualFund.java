package org.example.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public final class MutualFund extends Investment {

    private final String fundCode;
    private final double unitsHeld;
    private final double currentNAV;
    private final double avgAnnualDistribution;

    @Override
    public double calculateCurrentValue() {
        return unitsHeld * currentNAV;
    }

    @Override
    public double getProjectedAnnualReturn() {
        return unitsHeld * avgAnnualDistribution;
    }

    @Override
    public void validate() {
        if(unitsHeld<0) throw new IllegalArgumentException("Units held must be positive");
        if(currentNAV<0) throw new IllegalArgumentException("Current NAV must be positive");
        if(avgAnnualDistribution<0) throw new IllegalArgumentException("Average annual distribution must be positive");
    }
}
