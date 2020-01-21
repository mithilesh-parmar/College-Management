package model;

import com.google.cloud.Timestamp;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SectionAttendance {
    private StringProperty className, subject, date, sectionName;
    private ObjectProperty<Timestamp> dateUnix;
    private ListProperty<Map<String, String>> lectureAttendance;

    private IntegerProperty presentStudent, absentStudent;


    public SectionAttendance(String className,
                             String subject,
                             String section,
                             String date,
                             Timestamp dateUnix,
                             List<Map<String, String>> lectureAttendance) {
        this.className = new SimpleStringProperty(className);
        this.subject = new SimpleStringProperty(subject);
        this.sectionName = new SimpleStringProperty(section);


        this.date = new SimpleStringProperty(date);
        this.dateUnix = new SimpleObjectProperty<>(dateUnix);
        this.lectureAttendance = new SimpleListProperty<>(FXCollections.observableArrayList(lectureAttendance));
        this.presentStudent = new SimpleIntegerProperty();
        this.absentStudent = new SimpleIntegerProperty();
        getAttendanceSummary();
    }


    public static SectionAttendance fromJSON(Map<String, Object> json) {
        return new SectionAttendance(
                (String) json.get("class_name"),
                (String) json.get("subject"),
                (String) json.get("section"),
                (String) json.get("date"),
                (Timestamp) json.get("date_unix"),
                (List<Map<String, String>>) json.get("lecture_attendance")

        );
    }

    public Map<String, Object> toJSON() {
        Map<String, Object> json = new HashMap<>();
        json.put("class_name", className.get());
        json.put("subject", subject.get());
        json.put("section", sectionName.get());
        json.put("date", date.get());
        json.put("date_unix", dateUnix.get());
        json.put("lecture_attendance", lectureAttendance.get());
        return json;
    }

    private void getAttendanceSummary() {
        lectureAttendance.forEach(student -> {
            boolean isPresent = student.get("present").toUpperCase().equals("P");
            if (isPresent) presentStudent.set(presentStudent.get() + 1);
            else absentStudent.set(absentStudent.get() + 1);
        });
    }

    public int getPresentStudent() {
        return presentStudent.get();
    }

    public IntegerProperty presentStudentProperty() {
        return presentStudent;
    }

    public void setPresentStudent(int presentStudent) {
        this.presentStudent.set(presentStudent);
    }

    public int getAbsentStudent() {
        return absentStudent.get();
    }

    public IntegerProperty absentStudentProperty() {
        return absentStudent;
    }

    public void setAbsentStudent(int absentStudent) {
        this.absentStudent.set(absentStudent);
    }

    public String getClassName() {
        return className.get();
    }

    public StringProperty classNameProperty() {
        return className;
    }

    public void setClassName(String className) {
        this.className.set(className);
    }

    public String getSubject() {
        return subject.get();
    }

    public StringProperty subjectProperty() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject.set(subject);
    }

    public String getSectionName() {
        return sectionName.get();
    }

    public StringProperty sectionNameProperty() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName.set(sectionName);
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

    public Timestamp getDateUnix() {
        return dateUnix.get();
    }

    public ObjectProperty<Timestamp> dateUnixProperty() {
        return dateUnix;
    }

    public void setDateUnix(Timestamp dateUnix) {
        this.dateUnix.set(dateUnix);
    }

    public ObservableList<Map<String, String>> getLectureAttendance() {
        return lectureAttendance.get();
    }

    public ListProperty<Map<String, String>> lectureAttendanceProperty() {
        return lectureAttendance;
    }

    public void setLectureAttendance(ObservableList<Map<String, String>> lectureAttendance) {
        this.lectureAttendance.set(lectureAttendance);
    }
}
