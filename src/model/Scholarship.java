package model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

public class Scholarship {
    private LongProperty percent;

    public Scholarship(long percent) {
        this.percent = new SimpleLongProperty(percent);
    }

    public long getPercent() {
        return percent.get();
    }

    public LongProperty percentProperty() {
        return percent;
    }

    public void setPercent(long percent) {
        this.percent.set(percent);
    }

    public long getDueAmount(long amount) {
        long discount = amount * percent.get();
        return amount - discount;
    }
}
