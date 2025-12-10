package org.example.model;

import java.io.Serial;
import java.time.LocalDate;
import java.util.Objects;

public final class Bond extends Investment {

    private final double faceValue;
    private final double couponRate;
    private final LocalDate maturityDate;
    @Serial
    private static final long serialVersionUID = 1L;

    public Bond(String id, String name, double faceValue, double couponRate, LocalDate maturityDate) {
        super(id, name);
        this.faceValue = faceValue;
        this.couponRate = couponRate;
        this.maturityDate = maturityDate;
    }

    public double getFaceValue() {
        return faceValue;
    }

    public double getCouponRate() {
        return couponRate;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    @Override
    public double calculateCurrentValue() {
        return faceValue;
    }

    @Override
    public double getProjectedAnnualReturn() {
        return faceValue * couponRate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Bond bond = (Bond) o;
        return Double.compare(faceValue, bond.faceValue) == 0 && Double.compare(couponRate, bond.couponRate) == 0 && Objects.equals(maturityDate, bond.maturityDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(faceValue, couponRate, maturityDate);
    }
}
