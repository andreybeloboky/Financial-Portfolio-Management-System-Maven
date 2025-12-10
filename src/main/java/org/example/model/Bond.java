package org.example.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.time.LocalDate;
import java.util.Objects;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public final class Bond extends Investment {

    private final double faceValue;
    private final double couponRate;
    private final LocalDate maturityDate;

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public double calculateCurrentValue() {
        return faceValue;
    }

    @Override
    public double getProjectedAnnualReturn() {
        return faceValue * couponRate;
    }
}
