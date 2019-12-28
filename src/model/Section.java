package model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.sql.Time;
import java.util.*;



/*

Time Table
            -> Day 1 ->
                        English -> start time, end time
                        Hindi   -> start time, end time
                        Maths   -> start time, end time
           -> Day 2 ->
                        English -> start time, end time
                        Hindi   -> start time, end time
                        Maths   -> start time, end time
           -> Day 3 -> (Class Schedule)
                        Bio -> start time, end time (Lecture)
                        Geo -> start time, end time
                        Physics -> start time, end time

 */


public class Section {

    private StringProperty classId = new SimpleStringProperty(),
            name = new SimpleStringProperty();

    private MapProperty<String, ObservableList<Lecture>> classSchedules = new SimpleMapProperty<>();

    public Section(String classId, String name, ObservableMap<String, ObservableList<Lecture>> classSchedules) {
        setClassId(classId);
        setName(name);
        setClassSchedules(FXCollections.observableMap(classSchedules));
    }

    public static Section fromJSON(Map<String, Object> json) {

//        System.out.println(json.get("time_table"));

        HashMap<String, List> timeTableDataMap = (HashMap) json.get("time_table");


        Map<String, ObservableList<Lecture>> classSchedule = new HashMap<>();

        for (Map.Entry mapElement : timeTableDataMap.entrySet()) {
            List<Lecture> lectureList = new ArrayList<>();

            Object key = mapElement.getKey();
            List value = (List) mapElement.getValue();

//            if this day already has lecture registered
            if (classSchedule.containsKey(key)) {
                lectureList = classSchedule.get(key);
            }

            for (Object data : value) {

                lectureList.add(Lecture.fromJSON((Map<String, Object>) data));

            }

            System.out.println("Adding day: " + key + " with lectures: " + lectureList);
            classSchedule.put((String) key, FXCollections.observableList(lectureList));
        }


        return new Section(
                (String) json.get("class_id"),
                (String) json.get("name"),
                FXCollections.observableMap(classSchedule)
        );
    }

    @Override
    public String toString() {
        return name.get();
    }

    public String getClassId() {
        return classId.get();
    }

    public StringProperty classIdProperty() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId.set(classId);
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


    public ObservableMap<String, ObservableList<Lecture>> getClassSchedules() {
        return classSchedules.get();
    }

    public MapProperty<String, ObservableList<Lecture>> classSchedulesProperty() {
        return classSchedules;
    }

    public void setClassSchedules(ObservableMap<String, ObservableList<Lecture>> classSchedules) {
        this.classSchedules.set(classSchedules);
    }
}
