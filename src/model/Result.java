package model;

import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import java.util.HashMap;
import java.util.Map;

public class Result {

    private StringProperty batch,
            className,
            examName,
            examId,
            profilePictureUrl,
            section,
            student,
            studentId;
    private MapProperty<String, Score> subjects;

    public Result(String batch,
                  String examName,
                  String className,
                  String examId,
                  String profileUrl,
                  String section,
                  String student,
                  String studentId,
                  Map<String, Score> subjects) {

        this.batch = new SimpleStringProperty(batch);
        this.examName = new SimpleStringProperty(examName);
        this.className = new SimpleStringProperty(className);
        this.examId = new SimpleStringProperty(examId);
        this.profilePictureUrl = new SimpleStringProperty(profileUrl);
        this.section = new SimpleStringProperty(section);
        this.student = new SimpleStringProperty(student);
        this.studentId = new SimpleStringProperty(studentId);
        this.subjects = new SimpleMapProperty<>(FXCollections.observableMap(subjects));

    }


    public static Result fromJSON(Map<String, Object> json) {
        return new Result(
                (String) json.get("batch"),
                (String) json.get("exam_name"),
                (String) json.get("class_name"),
                (String) json.get("exam_id"),
                (String) json.get("pp_url"),
                (String) json.get("section"),
                (String) json.get("student"),
                (String) json.get("student_id"),
                (Map<String, Score>) json.get("subjects")
        );
    }

    public Map<String, Object> toJSON() {
        Map<String, Object> json = new HashMap<>();
        json.put("batch", batch.get());
        json.put("exam_name", examName.get());
        json.put("class_name", className.get());
        json.put("exam_id", examId.get());
        json.put("pp_url", profilePictureUrl.get());
        json.put("section", section.get());
        json.put("student", student.get());
        json.put("student_id", studentId.get());
        json.put("subjects", subjects.get());
        return json;
    }

    @Override
    public String toString() {
        return "\nExam: " + examName.get() + "\nBatch: " + batch.get() + "\nStudent: " + student.get() + "\nSubjects: " + subjects;
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

    public String getExamName() {
        return examName.get();
    }

    public StringProperty examNameProperty() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName.set(examName);
    }

    public String getExamId() {
        return examId.get();
    }

    public StringProperty examIdProperty() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId.set(examId);
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl.get();
    }

    public StringProperty profilePictureUrlProperty() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl.set(profilePictureUrl);
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

    public String getStudent() {
        return student.get();
    }

    public StringProperty studentProperty() {
        return student;
    }

    public void setStudent(String student) {
        this.student.set(student);
    }

    public String getStudentId() {
        return studentId.get();
    }

    public StringProperty studentIdProperty() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId.set(studentId);
    }

    public ObservableMap<String, Score> getSubjects() {
        return subjects.get();
    }

    public MapProperty<String, Score> subjectsProperty() {
        return subjects;
    }

    public void setSubjects(ObservableMap<String, Score> subjects) {
        this.subjects.set(subjects);
    }
}
