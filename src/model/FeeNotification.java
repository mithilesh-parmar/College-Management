package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Map;

public class FeeNotification extends Notification {
    private StringProperty amount = new SimpleStringProperty(),
            deadline = new SimpleStringProperty();

    public FeeNotification(String title, String message, String amount, String deadline) {
        super(title, message);
        setAmount(amount);
        setDeadline(deadline);
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

    public String getDeadline() {
        return deadline.get();
    }

    public StringProperty deadlineProperty() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline.set(deadline);
    }
}
