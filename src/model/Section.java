package model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import utility.DAY;

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


/*
class id - course name
name - year
 */

public class Section {

    private StringProperty className = new SimpleStringProperty(),
            classID = new SimpleStringProperty(),
            sectionName = new SimpleStringProperty(), id = new SimpleStringProperty();
    private ListProperty<String> subjects = new SimpleListProperty<>();
    private MapProperty<String, ObservableList<Lecture>> classSchedules = new SimpleMapProperty<>();

    public Section(String id, String className, String classId, String sectionName, ObservableMap<String, ObservableList<Lecture>> classSchedules, List<String> subjects) {
        setId(id);
        setClassID(classId);
        setClassName(className);
        setSectionName(sectionName);
        setSubjects(FXCollections.observableList(subjects));
        setClassSchedules(FXCollections.observableMap(classSchedules));
    }

    public Section(String id, String sectionName, ClassItem classItem, ObservableMap<String, ObservableList<Lecture>> classSchedules, List<String> subjects) {
        setId(id);
        setClassID(classItem.getId());
        setClassName(classItem.getName());
        setSectionName(sectionName);
        setSubjects(FXCollections.observableList(subjects));
        setClassSchedules(FXCollections.observableMap(classSchedules));
    }

    public String getClassID() {
        return classID.get();
    }

    public StringProperty classIDProperty() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID.set(classID);
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

    public ObservableList<String> getSubjects() {
        return subjects.get();
    }

    public ListProperty<String> subjectsProperty() {
        return subjects;
    }

    public void setSubjects(ObservableList<String> subjects) {
        this.subjects.set(subjects);
    }

    public void addLecture(Lecture lecture) {

        String dayOfWeek = lecture.getDayOfWeek();
//        if there's an entry for a particular day
        if (classSchedules.containsKey(dayOfWeek)) {

//             get the list and append the lecture
            classSchedules.get(dayOfWeek).add(lecture);

        } else {

//             make a new empty list
            ObservableList list = FXCollections.observableArrayList();
            list.add(lecture);
            classSchedules.put(dayOfWeek, list);

        }

        System.out.println("Lecture added ");
        System.out.println(classSchedules);

    }

    public Map<String, Object> toJSON() {
        Map<String, Object> json = new HashMap<>();
        json.put("class_name", className.get());
        json.put("name", sectionName.get());
        json.put("subjects", subjects.get());
        json.put("id", id.get());
        json.put("class_id", classID.get());


        Map<String, List<Lecture>> listMap = new HashMap<>();

        classSchedules.forEach((key, lectures) -> {

            List lectureList = new ArrayList();

            lectures.forEach(lecture -> lectureList.add(lecture.toJSON()));


            listMap.put(key, lectureList);
        });

        json.put("time_table", listMap);
        return json;
    }

    public ObservableList<Lecture>  getLectures(DAY day) {
        return classSchedules.get(day.toString());
    }

    public static Section fromJSON(Map<String, Object> json) {

        HashMap<String, List> timeTableDataMap = (HashMap) json.get("time_table");


        Map<String, ObservableList<Lecture>> classSchedule = new HashMap<>();

        if (timeTableDataMap != null)
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


                classSchedule.put((String) key, FXCollections.observableList(lectureList));
            }


        return new Section(
                (String) json.get("id"),
                (String) json.get("class_name"),
                (String) json.get("class_id"),
                String.valueOf(json.get("name")),
                FXCollections.observableMap(classSchedule),
                FXCollections.observableList((List<String>) json.get("subjects"))
        );
    }

    @Override
    public String toString() {
        return sectionName.get();
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

    public String getSectionName() {
        return sectionName.get();
    }

    public StringProperty sectionNameProperty() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName.set(sectionName);
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
