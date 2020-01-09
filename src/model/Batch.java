package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;

public class Batch {

    private StringProperty name = new SimpleStringProperty();

    public Batch(String name) {
        setName(name);
    }

    public static Batch fromJSON(Map<String, Object> json) {
        return new Batch(
                (String) json.get("name")
        );
    }

    public Map<String, Object> toJSON() {
        Map<String, Object> json = new HashMap<>();
        json.put("name", name.get());
        return json;
    }

    @Override
    public String toString() {
        return name.get();
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }
}
