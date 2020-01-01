package model;

import com.google.cloud.Timestamp;
import javafx.beans.property.*;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Attendance {
    private StringProperty date = new SimpleStringProperty();
    private BooleanProperty present = new SimpleBooleanProperty();
    private ObjectProperty<Timestamp> unixDate = new SimpleObjectProperty<>();

    public Attendance(String date, boolean present, Timestamp unixDate) {
        setDate(date);
        setPresent(present);
        setUnixDate(unixDate);

    }

    public static Attendance fromJSON(Map<String, Object> json) {
        return new Attendance(
                (String) json.get("date"),
                (boolean) json.get("present"),
                (Timestamp) json.get("unix_date")
        );
    }


    public Map<String, Object> toJSON() {
        Map<String, Object> json = new HashMap<>();
        json.put("date", date.get());
        json.put("present", present.get());
        json.put("unix_date", unixDate.get());
        return json;
    }

    public String getDate() {
        return date.get();
    }

    public StringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public boolean isPresent() {
        return present.get();
    }

    public BooleanProperty presentProperty() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present.set(present);
    }

    public Timestamp getUnixDate() {
        return unixDate.get();
    }

    public ObjectProperty<Timestamp> unixDateProperty() {
        return unixDate;
    }

    public void setUnixDate(Timestamp unixDate) {
        this.unixDate.set(unixDate);
    }
}
