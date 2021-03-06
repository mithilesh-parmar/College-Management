package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;

public class BackLog {
    private StringProperty ID,
            studentID,
            subjectName,
            examName,
            examID,
            sectionID,
            sectionName;

    private BooleanProperty isCleared;

    public BackLog(String ID,
                   String studentID,
                   String subjectName,
                   String examName,
                   String examID,
                   String sectionID,
                   String sectionName,
                   boolean isCleared) {
        this.ID = new SimpleStringProperty(ID);
        this.studentID = new SimpleStringProperty(studentID);
        this.isCleared = new SimpleBooleanProperty(isCleared);
        this.subjectName = new SimpleStringProperty(subjectName);
        this.examName = new SimpleStringProperty(examName);
        this.examID = new SimpleStringProperty(examID);
        this.sectionID = new SimpleStringProperty(sectionID);
        this.sectionName = new SimpleStringProperty(sectionName);
    }

    public static BackLog fromJSON(Map<String, Object> json) {
        return new BackLog(
                (String) json.get("id"),
                (String) json.get("student_id"),
                (String) json.get("subject_name"),
                (String) json.get("exam_name"),
                (String) json.get("exam_id"),
                (String) json.get("section_id"),
                (String) json.get("section_name"),
                (boolean) json.get("is_cleared")
        );
    }

    public Map<String, Object> toJSON() {
        Map<String, Object> json = new HashMap<>();
        json.put("id", ID.get());
        json.put("student_id", studentID.get());
        json.put("subject_name", subjectName.get());
        json.put("exam_name", examName.get());
        json.put("exam_id", examID.get());
        json.put("is_cleared", isCleared.get());
        json.put("section_id", sectionID.get());
        json.put("section_name", sectionName.get());
        return json;
    }

    public String getID() {
        return ID.get();
    }

    public boolean isIsCleared() {
        return isCleared.get();
    }

    public String getSectionID() {
        return sectionID.get();
    }

    public StringProperty sectionIDProperty() {
        return sectionID;
    }

    public void setSectionID(String sectionID) {
        this.sectionID.set(sectionID);
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

    public BooleanProperty isClearedProperty() {
        return isCleared;
    }

    public void setIsCleared(boolean isCleared) {
        this.isCleared.set(isCleared);
    }

    public StringProperty IDProperty() {
        return ID;
    }

    public void setID(String ID) {
        this.ID.set(ID);
    }

    public String getStudentID() {
        return studentID.get();
    }

    public StringProperty studentIDProperty() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID.set(studentID);
    }

    public String getSubjectName() {
        return subjectName.get();
    }

    public StringProperty subjectNameProperty() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName.set(subjectName);
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

    public String getExamID() {
        return examID.get();
    }

    public StringProperty examIDProperty() {
        return examID;
    }

    public void setExamID(String examID) {
        this.examID.set(examID);
    }

}
