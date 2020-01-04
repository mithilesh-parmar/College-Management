package model;

import com.google.cloud.Timestamp;
import javafx.beans.property.*;

import java.util.HashMap;
import java.util.Map;

public class TeacherLeave {

    private ObjectProperty<Timestamp> endDate = new SimpleObjectProperty<>(),
            startDate = new SimpleObjectProperty<>();
    private StringProperty teacherId = new SimpleStringProperty(),
            teacherName = new SimpleStringProperty(),
            reason = new SimpleStringProperty();
    private LongProperty status = new SimpleLongProperty();


    public TeacherLeave(Timestamp startDate, Timestamp endDate, String teacherId, String teacherName, String reason, long status) {
        setStartDate(startDate);
        setEndDate(endDate);
        setTeacherId(teacherId);
        setTeacherName(teacherName);
        setReason(reason);
        setStatus(status);
    }

    public static TeacherLeave fromJSON(Map<String, Object> json) {

        return new TeacherLeave(
                (Timestamp) json.get("start_date"),
                (Timestamp) json.get("end_date"),
                (String) json.get("teacher_id"),
                (String) json.get("teacher_name"),
                (String) json.get("reason"),
                (long) json.get("status")

        );
    }


    public Map<String, Object> toJSON() {
        Map<String, Object> json = new HashMap<>();
        json.put("start_date", startDate.get());
        json.put("end_date", endDate.get());
        json.put("teacher_id", teacherId.get());
        json.put("teacher_name", teacherName.get());
        json.put("reason", reason.get());
        json.put("status", status.get());
        return json;
    }

    public long getStatus() {
        return status.get();
    }

    public LongProperty statusProperty() {
        return status;
    }

    public void setStatus(long status) {
        this.status.set(status);
    }

    public Timestamp getEndDate() {
        return endDate.get();
    }

    public ObjectProperty<Timestamp> endDateProperty() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate.set(endDate);
    }

    public Timestamp getStartDate() {
        return startDate.get();
    }

    public ObjectProperty<Timestamp> startDateProperty() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate.set(startDate);
    }

    public String getTeacherId() {
        return teacherId.get();
    }

    public StringProperty teacherIdProperty() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId.set(teacherId);
    }

    public String getTeacherName() {
        return teacherName.get();
    }

    public StringProperty teacherNameProperty() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName.set(teacherName);
    }

    public String getReason() {
        return reason.get();
    }

    public StringProperty reasonProperty() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason.set(reason);
    }



    public void setStatus(int status) {
        this.status.set(status);
    }

    @Override
    public String toString() {
        return teacherName.get() + " " + reason.get();
    }
}
