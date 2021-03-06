package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;

public class Lecture {

    private StringProperty name = new SimpleStringProperty(),
            startTime = new SimpleStringProperty(),
            endTime = new SimpleStringProperty(),
            dayOfWeek = new SimpleStringProperty();


    public Lecture(String name, String endTime, String startTime, String dayOfWeek) {
        setStartTime(startTime);
        setEndTime(endTime);
        setName(name);
        setDayOfWeek(dayOfWeek);
    }

    public String getDayOfWeek() {
        return dayOfWeek.get();
    }

    public StringProperty dayOfWeekProperty() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek.set(dayOfWeek);
    }

    public static Lecture fromJSON(Map<String, Object> json) {
        return new Lecture(
                (String) json.get("sub_name"),
                (String) json.get("end_time"),
                (String) json.get("start_time"),
                (String) json.get("day_of_week")
        );
    }

    public Map<String, Object> toJSON() {
        Map<String, Object> json = new HashMap<>();
        json.put("sub_name", name.get());
        json.put("start_time", startTime.get());
        json.put("end_time", endTime.get());
        json.put("day_of_week", dayOfWeek.get());
        return json;
    }

    @Override
    public String toString() {
        return toJSON().toString();
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

    public String getStartTime() {
        return startTime.get();
    }

    public StringProperty startTimeProperty() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime.set(startTime);
    }

    public String getEndTime() {
        return endTime.get();
    }

    public StringProperty endTimeProperty() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime.set(endTime);
    }
}
