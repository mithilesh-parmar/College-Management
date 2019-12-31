package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;

public class Notification {
    private StringProperty title = new SimpleStringProperty(),
            message = new SimpleStringProperty();


    public Notification(String title, String message) {
        setTitle(title);
        setMessage(message);
    }

    @Override
    public String toString() {
        return title.get() + " " + message.get();
    }


    public Map<String, Object> toJSON() {
        HashMap<String, Object> json = new HashMap<>();

        json.put("msg", message.get());
        json.put("title", title.get());
        return json;
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getMessage() {
        return message.get();
    }

    public StringProperty messageProperty() {
        return message;
    }

    public void setMessage(String message) {
        this.message.set(message);
    }
}
