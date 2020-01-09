package model;

import com.google.cloud.Timestamp;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import javafx.collections.ObservableMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SectionAttendance {
    private ObjectProperty<Timestamp> dateUnix = new SimpleObjectProperty<>();
    private StringProperty batch = new SimpleStringProperty(),
            className = new SimpleStringProperty(),
            section = new SimpleStringProperty(),
            date = new SimpleStringProperty();

    private MapProperty<String, List<Map<String, Object>>> lectureAttendance = new SimpleMapProperty<>();

    public SectionAttendance(String className,
                             String section,
                             String batch,
                             String date,
                             Timestamp dateUnix,
                             ObservableMap<String, List<Map<String, Object>>> lectureAttendance) {
        setClassName(className);
        setSection(section);
        setBatch(batch);
        setDate(date);
        setDateUnix(dateUnix);
        setLectureAttendance(lectureAttendance);
    }


    public static SectionAttendance fromJSON(Map<String, Object> json) {
//        System.out.println(FXCollections.observableMap((HashMap) json.get("lecture_attendance")));

        return new SectionAttendance(
                (String) json.get("class_name"),
                (String) json.get("section"),
                (String) json.get("batch"),
                json.get("date").toString(),
                (Timestamp) json.get("date_unix"),
                FXCollections.observableMap((HashMap) json.get("lecture_attendance"))
        );
    }

    public Map<String, Object> toJSON() {
        Map<String, Object> json = new HashMap<>();
        json.put("class_name", className.get());
        json.put("section", section.get());
        json.put("batch", batch.get());
        json.put("date", date.get());
        json.put("date_unix", dateUnix.get());
        json.put("lecture_attendance", lectureAttendance.get());
        return json;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public void setDateUnix(Timestamp dateUnix) {
        this.dateUnix.set(dateUnix);
    }

    public void setBatch(String batch) {
        this.batch.set(batch);
    }

    public void setClassName(String className) {
        this.className.set(className);
    }

    public void setSection(String section) {
        this.section.set(section);
    }

    public void setLectureAttendance(ObservableMap<String, List<Map<String, Object>>> lectureAttendance) {
        this.lectureAttendance.set(lectureAttendance);
    }

    public Timestamp getDateUnix() {
        return dateUnix.get();
    }

    public ObjectProperty<Timestamp> dateUnixProperty() {
        return dateUnix;
    }

    public String getBatch() {
        return batch.get();
    }

    public StringProperty batchProperty() {
        return batch;
    }

    public String getClassName() {
        return className.get();
    }

    public StringProperty classNameProperty() {
        return className;
    }

    public String getSection() {
        return section.get();
    }

    public StringProperty sectionProperty() {
        return section;
    }

    public String getDate() {
        return date.get();
    }

    public StringProperty dateProperty() {
        return date;
    }

    public ObservableMap<String, List<Map<String, Object>>> getLectureAttendance() {
        return lectureAttendance.get();
    }

    public MapProperty<String, List<Map<String, Object>>> lectureAttendanceProperty() {
        return lectureAttendance;
    }
}
