package model;

import com.google.cloud.Timestamp;
import javafx.beans.property.*;
import utility.DateUtility;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

public class StudentAttendance {


    private StringProperty date = new SimpleStringProperty();
    private BooleanProperty present = new SimpleBooleanProperty();
    private StringProperty lecture;
    private ObjectProperty<Timestamp> unixDate = new SimpleObjectProperty<>();

    public StudentAttendance(Timestamp date, boolean present, Timestamp unixDate, String lecture) {
        setDate(DateUtility.timeStampToReadable(date));
        setPresent(present);
        setUnixDate(unixDate);
        this.lecture = new SimpleStringProperty(lecture);
    }

    //    TODO for some students date is in string while for others it is in timestamp
    public static StudentAttendance fromJSON(Map<String, Object> json) {
        return new StudentAttendance(
                (Timestamp) json.get("date"),
                (boolean) json.get("present"),
                (Timestamp) json.get("unix_date"),
                (String) json.get("lecture")
        );
    }


    public Map<String, Object> toJSON() {
        Map<String, Object> json = new HashMap<>();
        json.put("date", date.get());
        json.put("present", present.get());
        json.put("lecture", lecture.get());
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

    public String getLecture() {
        return lecture.get();
    }

    public StringProperty lectureProperty() {
        return lecture;
    }

    public void setLecture(String lecture) {
        this.lecture.set(lecture);
    }
}
