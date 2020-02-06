package model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassItem {

    private StringProperty name;
    private StringProperty id;
//    private ListProperty<Fee> feeList;

    public ClassItem(String id, String name) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
//        this.feeList = new SimpleListProperty<>(FXCollections.observableArrayList(feeList));
    }

    @Override
    public String toString() {
        return name.get();
    }

    public static ClassItem fromJSON(Map<String, Object> json) {
        return new ClassItem(
                (String) json.get("id"),
                (String) json.get("name")
//                (List<Fee>) json.get("fee_type")
        );
    }

    public Map<String, Object> toJSON() {
        Map<String, Object> json = new HashMap<>();
        json.put("id", id.get());
        json.put("name", name.get());
        return json;
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
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
