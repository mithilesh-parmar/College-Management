package model;

import javafx.beans.property.*;

import java.util.HashMap;
import java.util.Map;

public class Course {
    private StringProperty name, id;
    private ObjectProperty<Long> years;

    public Course(String id, String name, Long years) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.years = new SimpleObjectProperty<>(years);
    }

    public static Course fromJSON(Map<String, Object> json) {
        return new Course(
                (String) json.get("id"),
                (String) json.get("name"),
                (Long) json.get("years")
        );
    }


    public Map<String, Object> toJSON() {
        Map<String, Object> json = new HashMap<>();
        json.put("name", name.get());
        json.put("years", years.get());
        json.put("id", id.get());
        return json;
    }

    @Override
    public String toString() {
        return name.get();
    }

    public Long getYears() {
        return years.get();
    }

    public ObjectProperty<Long> yearsProperty() {
        return years;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }
}
