package model;

import com.google.cloud.Timestamp;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Map;

public class FeeNotification extends Notification {
    private StringProperty amount = new SimpleStringProperty();
    private ObjectProperty<Timestamp> deadline;

    public FeeNotification(String title, String message, String amount, Timestamp deadline) {
        super(title, message);
        setAmount(amount);
        this.deadline = new SimpleObjectProperty<>(deadline);
    }


    public Map<String, Object> toJSON() {
        Map<String, Object> json = super.toJSON();
        json.put("amount", amount.get());
        json.put("deadline", deadline.get());
        return json;
    }

    public String getAmount() {
        return amount.get();
    }

    public StringProperty amountProperty() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount.set(amount);
    }

    public Timestamp getDeadline() {
        return deadline.get();
    }

    public ObjectProperty<Timestamp> deadlineProperty() {
        return deadline;
    }

    public void setDeadline(Timestamp deadline) {
        this.deadline.set(deadline);
    }
}
