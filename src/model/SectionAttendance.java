package model;

import com.google.cloud.Timestamp;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SectionAttendance {
    private StringProperty course, subject;
    private LongProperty year;
    private ObjectProperty<Timestamp> date, dateUnix;
    private ListProperty<Map<String, String>> lectureAttendance;

    private IntegerProperty presentStudent, absentStudent;


    public SectionAttendance(String course,
                             String subject,
                             Long year,
                             Timestamp date,
                             Timestamp dateUnix,
                             List<Map<String, String>> lectureAttendance) {
        this.course = new SimpleStringProperty(course);
        this.subject = new SimpleStringProperty(subject);
        this.year = new SimpleLongProperty(year);
        this.date = new SimpleObjectProperty<>(date);
        this.dateUnix = new SimpleObjectProperty<>(dateUnix);
        this.lectureAttendance = new SimpleListProperty<>(FXCollections.observableArrayList(lectureAttendance));
        this.presentStudent = new SimpleIntegerProperty();
        this.absentStudent = new SimpleIntegerProperty();
        getAttendanceSummary();
    }


    public static SectionAttendance fromJSON(Map<String, Object> json) {
        return new SectionAttendance(
                (String) json.get("course"),
                (String) json.get("subject"),
                (Long) json.get("year"),
                (Timestamp) json.get("date"),
                (Timestamp) json.get("date_unix"),
                (List<Map<String, String>>) json.get("lecture_attendance")

        );
    }

    public Map<String, Object> toJSON() {
        Map<String, Object> json = new HashMap<>();
        json.put("course", course.get());
        json.put("subject", subject.get());
        json.put("year", year.get());
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

    public String getCourse() {
        return course.get();
    }

    public StringProperty courseProperty() {
        return course;
    }

    public void setCourse(String course) {
        this.course.set(course);
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


    public long getYear() {
        return year.get();
    }

    public LongProperty yearProperty() {
        return year;
    }

    public void setYear(long year) {
        this.year.set(year);
    }

    public void setYear(int year) {
        this.year.set(year);
    }

    public Timestamp getDate() {
        return date.get();
    }

    public ObjectProperty<Timestamp> dateProperty() {
        return date;
    }

    public void setDate(Timestamp date) {
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
