package org.example.model;

import java.io.Serial;

public final class MutualFund extends Investment {

    private final String fundCode;
    private final double unitsHeld;
    private final double currentNAV;
    private final double avgAnnualDistribution;
    @Serial
    private static final long serialVersionUID = 1L;

    public MutualFund(String id, String name, String fundCode, double currentNAV, double avgAnnualDistribution, double unitsHeld) {
        super(id, name);
        this.fundCode = fundCode;
        this.currentNAV = currentNAV;
        this.avgAnnualDistribution = avgAnnualDistribution;
        this.unitsHeld = unitsHeld;
    }

    public String getFundCode() {
        return fundCode;
    }

    public double getUnitsHeld() {
        return unitsHeld;
    }

    public double getCurrentNAV() {
        return currentNAV;
    }

    public double getAvgAnnualDistribution() {
        return avgAnnualDistribution;
    }

    @Override
    public double calculateCurrentValue() {
        return unitsHeld * currentNAV;
    }

    @Override
    public double getProjectedAnnualReturn() {
        return unitsHeld * avgAnnualDistribution;
    }

}
