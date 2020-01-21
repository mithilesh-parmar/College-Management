package model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassItem {

    private StringProperty name;
//    private ListProperty<Fee> feeList;

    public ClassItem(String name) {
        this.name = new SimpleStringProperty(name);
//        this.feeList = new SimpleListProperty<>(FXCollections.observableArrayList(feeList));
    }

    @Override
    public String toString() {
        return name.get();
    }

    public static ClassItem fromJSON(Map<String, Object> json) {
        return new ClassItem(
                (String) json.get("name")
//                (List<Fee>) json.get("fee_type")
        );
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
