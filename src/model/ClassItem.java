package model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.poi.ss.formula.ptg.StringPtg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassItem {

    private StringProperty name;
    private LongProperty years;
    private StringProperty id;
//    private ListProperty<Fee> feeList;

    public ClassItem(String id, String name, long years) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.years = new SimpleLongProperty(years);
//        this.feeList = new SimpleListProperty<>(FXCollections.observableArrayList(feeList));
    }

    @Override
    public String toString() {
        return name.get();
    }

    public static ClassItem fromJSON(Map<String, Object> json) {
        return new ClassItem(
                (String) json.get("id"),
                (String) json.get("name"),
                (long) json.get("years")
//                (List<Fee>) json.get("fee_type")
        );
    }

    public Map<String, Object> toJSON() {
        Map<String, Object> json = new HashMap<>();
        json.put("id", id.get());
        json.put("name", name.get());
        json.put("years", years.get());
        return json;
    }

    public long getYears() {
        return years.get();
    }

    public LongProperty yearsProperty() {
        return years;
    }

    public void setYears(long years) {
        this.years.set(years);
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
