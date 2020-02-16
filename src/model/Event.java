package model;

import com.google.cloud.Timestamp;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event {

    private SimpleStringProperty title = new SimpleStringProperty(),
            description = new SimpleStringProperty(),
            id = new SimpleStringProperty(),
            time = new SimpleStringProperty();
    private SimpleObjectProperty<Timestamp> createdAt = new SimpleObjectProperty<>(),
            eventDate = new SimpleObjectProperty<>();
    private SimpleListProperty<String> images = new SimpleListProperty<>(FXCollections.observableArrayList());


    public Event(String id, String title, String description, String time, Timestamp createdAt, Timestamp eventDate, List<String> images) {
        setId(id);
        setTitle(title);
        setDescription(description);
        setTime(time);

        this.images.get().setAll(images);
        if (createdAt != null) setCreatedAt(createdAt);
        if (createdAt != null) setEventDate(eventDate);
//        if (images != null && images.size() > 0) {
//            ObservableList<String> imageList = FXCollections.observableList(images);
//            setImages(imageList);
//        }
    }

    public static Event fromJSON(Map<String, Object> json) {

        String title = json.containsKey("title") ? (String) json.get("title") : "";
        String desc = json.containsKey("desc") ? (String) json.get("desc") : "";
        String time = json.containsKey("time") ? (String) json.get("time") : "";
        String id = json.containsKey("id") ? (String) json.get("id") : "";
        Timestamp createdAt = json.containsKey("created_at") ? (Timestamp) json.get("created_at") : null;
        Timestamp eventDate = json.containsKey("event_date") ? (Timestamp) json.get("event_date") : null;
        List<String> images = json.containsKey("images") ? (List<String>) json.get("images") : null;

        return new Event(id, title, desc, time, createdAt, eventDate, images);


    }


    public Map<String, Object> toJSON() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id.get());
        map.put("title", title.get());
        map.put("desc", description.get());
        map.put("time", time.get());
        map.put("created_at", createdAt.get());
        map.put("event_date", eventDate.get());
        map.put("images", images.get());
        return map;
    }

    public String getId() {
        return id.get();
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public Timestamp getCreatedAt() {
        return createdAt.get();
    }

    public Timestamp getEventDate() {
        return eventDate.get();
    }

    public ObservableList<String> getImages() {
        return images.get();
    }

    public SimpleListProperty<String> imagesProperty() {
        return images;
    }

    public void setImages(ObservableList<String> images) {
        this.images.set(images);
    }

    @Override
    public String toString() {
        return title.get();
    }

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getTime() {
        return time.get();
    }

    public SimpleStringProperty timeProperty() {
        return time;
    }

    public void setTime(String time) {
        this.time.set(time);
    }


    public SimpleObjectProperty<Timestamp> createdAtProperty() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt.set(createdAt);
    }

    public SimpleObjectProperty<Timestamp> eventDateProperty() {
        return eventDate;
    }

    public void setEventDate(Timestamp eventDate) {
        this.eventDate.set(eventDate);
    }


    public String getNameWithoutSpaces() {
        String name = title.get();

        name = name.replace(" ", "_");

        return name;
    }
}
