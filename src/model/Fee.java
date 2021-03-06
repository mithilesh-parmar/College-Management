package model;

import com.google.cloud.Timestamp;
import javafx.beans.property.*;
import utility.DateUtility;

import java.util.HashMap;
import java.util.Map;

public class Fee {

    public enum Type {
        ADMISSION_FEE("Admission_Fee"), FINE("FINE");
        private String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }


    private StringProperty id;
    private ObjectProperty<Timestamp> date;
    private StringProperty studentID;
    private LongProperty amount;
    private ObjectProperty<Type> type;
    private StringProperty dateReadable;


    public Fee(String id, String studentId, long amount, Timestamp date, Type type, String dateReadable) {
        this.id = new SimpleStringProperty(id);
        this.studentID = new SimpleStringProperty(studentId);
        this.amount = new SimpleLongProperty(amount);
        this.date = new SimpleObjectProperty<>(date);
        this.type = new SimpleObjectProperty<>(type);
        this.dateReadable = new SimpleStringProperty((dateReadable == null || dateReadable.isEmpty()) ? DateUtility.timeStampToReadable(date) : dateReadable);
    }

    public static Fee fromJSON(Map<String, Object> json) {
        return new Fee(
                (String) json.get("id"),
                (String) json.get("student_id"),
                (long) json.get("amount"),
                (Timestamp) json.get("date"),
                getType((String) json.get("fee_type")),
                (String) json.get("date_readable")
        );
    }

    public Map<String, Object> toJSON() {
        Map<String, Object> json = new HashMap<>();
        json.put("id", id.get());
        json.put("student_id", studentID.get());
        json.put("date", date.get());
        json.put("amount", amount.get());
        json.put("fee_type", type.get().toString());
        json.put("date_readable", dateReadable.get());
        return json;
    }

    private static Type getType(String value) {
        if (value.matches("Admission_Fee")) return Type.ADMISSION_FEE;
        else if (value.matches("FINE")) return Type.FINE;
        return null;
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

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public Timestamp getDate() {
        return date.get();
    }

    public ObjectProperty<Timestamp> dateProperty() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date.set(date);
        setDateReadable(DateUtility.timeStampToReadable(date));
    }

    public String getStudentID() {
        return studentID.get() == null ? "" : studentID.get();
    }

    public StringProperty studentIDProperty() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID.set(studentID);
    }

    public long getAmount() {
        return amount.get();
    }

    public LongProperty amountProperty() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount.set(amount);
    }

    public Type getType() {
        return type.get();
    }

    public ObjectProperty<Type> typeProperty() {
        return type;
    }

    public void setType(Type type) {
        this.type.set(type);
    }
}
