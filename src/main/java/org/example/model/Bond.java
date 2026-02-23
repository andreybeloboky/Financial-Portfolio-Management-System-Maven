package org.example.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@ToString
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public final class Bond extends Investment {

    private final double faceValue;
    private final double couponRate;
    private final LocalDate maturityDate;

    @Override
    public double calculateCurrentValue() {
        return faceValue;
    }

    @Override
    public double getProjectedAnnualReturn() {
        return faceValue * couponRate;
    }

    @Override
    public void validate() {
        if (faceValue < 0) throw new IllegalArgumentException("Face value must be positive");
        if (couponRate < 0) throw new IllegalArgumentException("Coupon rate must be positive");
    }
}
