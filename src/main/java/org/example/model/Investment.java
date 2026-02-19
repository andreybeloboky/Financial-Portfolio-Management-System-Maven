package org.example.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public abstract sealed class Investment implements Cloneable, Serializable, Comparable<Investment> permits Stock, Bond, MutualFund {
    protected int id;
    protected final String name;
    @Serial
    private static final long serialVersionUID = 1L;

    public abstract double calculateCurrentValue();

    public abstract double getProjectedAnnualReturn();

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public int compareTo(Investment o) {
        if (Double.compare(calculateCurrentValue(), o.calculateCurrentValue()) == 0) {
            return this.name.compareTo(o.name);
        } else {
            return Double.compare(calculateCurrentValue(), o.calculateCurrentValue());
        }
    }
}
