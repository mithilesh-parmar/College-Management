package model;

import com.google.cloud.Timestamp;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import utility.DateUtility;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Exam {
    private StringProperty batch, className, name, section, dateReadable, time, id;
    private ObjectProperty<Timestamp> date;
    private ListProperty<SubjectExam> subjects;


    public Exam(String id, String batch, String className, String name, String section, String dateReadable, String time, Timestamp date, List<SubjectExam> subjects) {
        this.id = new SimpleStringProperty(id);
        this.batch = new SimpleStringProperty(batch);
        this.className = new SimpleStringProperty(className);
        this.name = new SimpleStringProperty(name);
        this.section = new SimpleStringProperty(section);
        this.dateReadable = new SimpleStringProperty(dateReadable);
        this.time = new SimpleStringProperty(time);
        this.date = new SimpleObjectProperty<>(date);
        this.subjects = new SimpleListProperty<>(FXCollections.observableArrayList(subjects));
    }

    public Exam() {

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

    public static Exam fromJSON(Map<String, Object> json) {

        Map<String, String> subjectData = (Map<String, String>) json.get("subjects");

        List<SubjectExam> examList = new ArrayList<>();
        subjectData.forEach((subject, time) -> {
            examList.add(new SubjectExam(subject, DateUtility.readableStringToLocalDate(time)));
        });

        return new Exam(
                "",
                (String) json.get("batch"),
                (String) json.get("class_name"),
                (String) json.get("name"),
                (String) json.get("section"),
                (String) json.get("start_date_string"),
                (String) json.get("time"),
                (Timestamp) json.get("start_date"),
                examList

        );
    }

    public Map<String, Object> toJSON() {
        Map<String, String> examSubjectsWithTime = new HashMap<>();
        subjects.forEach(subjectExam -> {
            examSubjectsWithTime.put(subjectExam.getName(), DateUtility.dateToStringForFirestore(subjectExam.getDate()));
        });
        Map<String, Object> json = new HashMap<>();
        json.put("id", id.get());
        json.put("batch", batch.get());
        json.put("class_name", className.get());
        json.put("name", name.get());
        json.put("section", section.get());
        json.put("start_date_string", dateReadable.get());
        json.put("time", time.get());
        json.put("start_date", date.get());
        json.put("subjects", examSubjectsWithTime);
        return json;
    }


    public String getBatch() {
        return batch.get();
    }

    public StringProperty batchProperty() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch.set(batch);
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

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getSection() {
        return section.get();
    }

    public StringProperty sectionProperty() {
        return section;
    }

    public void setSection(String section) {
        this.section.set(section);
    }

    public String getDateReadable() {
        return dateReadable.get();
    }

    public StringProperty dateReadableProperty() {
        return dateReadable;
    }

    public void setDateReadable(String dateReadable) {
        this.dateReadable.set(dateReadable);
    }

    public String getTime() {
        return time.get();
    }

    public StringProperty timeProperty() {
        return time;
    }

    public void setTime(String time) {
        this.time.set(time);
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


    public ObservableList<SubjectExam> getSubjects() {
        return subjects.get();
    }

    public ListProperty<SubjectExam> subjectsProperty() {
        return subjects;
    }

    public void setSubjects(ObservableList<SubjectExam> subjects) {
        this.subjects.set(subjects);
    }

    public void setDate(LocalDate t1) {
        setDate(Timestamp.of(DateUtility.localDateToDate(t1)));
    }

    public static class SubjectExam {
        private StringProperty name;
        private ObjectProperty<LocalDate> date;

        public SubjectExam(String name, LocalDate date) {
            this.name = new SimpleStringProperty(name);
            this.date = new SimpleObjectProperty<>(date);
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

        public LocalDate getDate() {
            return date.get();
        }

        public ObjectProperty<LocalDate> dateProperty() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date.set(date);
        }
    }
}
