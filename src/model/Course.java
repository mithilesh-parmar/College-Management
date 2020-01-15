package model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Course {
    private StringProperty name, id;
    private ObjectProperty<Long> years;

    //    Subjects taught for each year
//    1-> english,hindi,maths
//    2-> digital c c++
//    3-> etx tex
    private MapProperty<String, List<String>> subjects;

    public Course(String id, String name, Long years, Map<String, List<String>> subjects) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.years = new SimpleObjectProperty<>(years);
        this.subjects = new SimpleMapProperty<>(FXCollections.observableMap(subjects));
    }


    public static Course fromJSON(Map<String, Object> json) {
        return new Course(
                (String) json.get("id"),
                (String) json.get("name"),
                (Long) json.get("years"),
                (Map<String, List<String>>) json.get("subjects")
        );
    }


    public List<String> getSubjects(int year) {
        return subjects.get(String.valueOf(year));
    }

    public Map<String, Object> toJSON() {
        Map<String, Object> json = new HashMap<>();
        json.put("name", name.get());
        json.put("years", years.get());
        json.put("id", id.get());
        json.put("subjects", subjects.get());
        return json;
    }

    @Override
    public String toString() {
        return name.get() ;
    }

    public Long getYears() {
        return years.get();
    }

    public StringProperty nameProperty() {
        return name;
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

    public void setYears(Long years) {
        this.years.set(years);
    }

    public ObservableMap<String, List<String>> getSubjects() {
        return subjects.get();
    }

    public MapProperty<String, List<String>> subjectsProperty() {
        return subjects;
    }

    public void setSubjects(ObservableMap<String, List<String>> subjects) {
        this.subjects.set(subjects);
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
