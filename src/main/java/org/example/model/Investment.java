package org.example.model;

import java.io.Serial;
import java.io.Serializable;

public abstract sealed class Investment implements Serializable, Comparable<Investment> permits Stock, Bond, MutualFund {

    protected final String id;
    protected final String name;
    @Serial
    private static final long serialVersionUID = 1L;

    protected Investment(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public abstract double calculateCurrentValue();

    public abstract double getProjectedAnnualReturn();

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public int compareTo(Investment o) {
        if (Double.compare(calculateCurrentValue(), o.calculateCurrentValue()) == 0) {
            return this.name.compareTo(o.getName());
        } else {
            return Double.compare(calculateCurrentValue(), o.calculateCurrentValue());
        }
    }


}
