package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;

public class StudentClass {
    private StringProperty classname = new SimpleStringProperty();

    public StudentClass(String className) {
        setClassname(className);
    }

    public static StudentClass fromJSON(Map<String, Object> json) {
        return new StudentClass(
                (String) json.get("name")
        );
    }

    public Map<String, Object> toJSON() {
        Map<String, Object> json = new HashMap<>();
        json.put("name", classname.get());
        return json;
    }

    @Override
    public String toString() {
        return classname.get();
    }

    public String getClassname() {
        return classname.get();
    }

    public StringProperty classnameProperty() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname.set(classname);
    }
}
