package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;

public class StudentSectionAttendance {
    private StringProperty admissionID, email, ppUrl, studentID, readableValueOfPresent;
    private BooleanProperty present;

    public StudentSectionAttendance(String admissionID, String email, String ppUrl, String studentID, boolean present) {
        this.admissionID = new SimpleStringProperty(admissionID);
        this.email = new SimpleStringProperty(email);
        this.ppUrl = new SimpleStringProperty(ppUrl);
        this.studentID = new SimpleStringProperty(studentID);
        this.present = new SimpleBooleanProperty(present);
        this.readableValueOfPresent = new SimpleStringProperty(present ? "P" : "A");
    }

    public static StudentSectionAttendance fromJSON(Map<String, Object> json) {
        return new StudentSectionAttendance(
                (String) json.get("admission_id"),
                (String) json.get("email"),
                (String) json.get("pp_url"),
                (String) json.get("stud_id"),
                (boolean) json.get("present")
        );
    }

    public Map<String, Object> toJSON() {
        Map<String, Object> json = new HashMap<>();
        json.put("admission_id", admissionID.get());
        json.put("email", email.get());
        json.put("pp_url", ppUrl.get());
        json.put("stud_id", studentID.get());
        json.put("present", present.get());
        return json;
    }

    public String getReadableValueOfPresent() {
        return readableValueOfPresent.get();
    }

    public StringProperty readableValueOfPresentProperty() {
        return readableValueOfPresent;
    }

    public void setReadableValueOfPresent(String readableValueOfPresent) {
        this.readableValueOfPresent.set(readableValueOfPresent);
    }

    public String getAdmissionID() {
        return admissionID.get();
    }

    public StringProperty admissionIDProperty() {
        return admissionID;
    }

    public void setAdmissionID(String admissionID) {
        this.admissionID.set(admissionID);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getPpUrl() {
        return ppUrl.get();
    }

    public StringProperty ppUrlProperty() {
        return ppUrl;
    }

    public void setPpUrl(String ppUrl) {
        this.ppUrl.set(ppUrl);
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

    public boolean isPresent() {
        return present.get();
    }

    public BooleanProperty presentProperty() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present.set(present);
    }
}
