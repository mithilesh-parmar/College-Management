package model;

import javafx.beans.property.*;

import java.util.HashMap;
import java.util.Map;

public class Fee {

    public enum Type {
        ADMISSION_FEE("Admission_Fee");

        private String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }


    private LongProperty amount;
    private ObjectProperty<Type> type;

    public Fee(long amount, String type) {
        this.amount = new SimpleLongProperty(amount);
        this.type = new SimpleObjectProperty<>(type.matches("Admission Fee") ? Type.ADMISSION_FEE : null);
    }

    public static Fee fromJSON(Map<String, Object> json) {
        return new Fee(
                (long) json.get("amount"),
                (String) json.get("fee_type")
        );
    }

    public Map<String, Object> toJSON() {
        Map<String, Object> json = new HashMap<>();
        json.put("amount", amount.get());
        json.put("fee_type", type.get().toString());
        return json;
    }

    public long getAmount() {
        return amount.get();
    }

    public LongProperty amountProperty() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount.set(amount);
    }

    public Type getType() {
        return type.get();
    }

    public ObjectProperty<Type> typeProperty() {
        return type;
    }

    public void setType(Type type) {
        this.type.set(type);
    }
}
