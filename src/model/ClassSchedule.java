package model;

import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.ObservableMap;

import java.util.List;

class ClassSchedule {
    private MapProperty<Integer, List<Lecture>> daySchedule = new SimpleMapProperty<>();

    public ClassSchedule(ObservableMap<Integer, List<Lecture>> observableMap) {
        setDaySchedule(observableMap);
    }

    public ObservableMap<Integer, List<Lecture>> getDaySchedule() {
        return daySchedule.get();
    }

    public MapProperty<Integer, List<Lecture>> dayScheduleProperty() {
        return daySchedule;
    }

    public void setDaySchedule(ObservableMap<Integer, List<Lecture>> daySchedule) {
        this.daySchedule.set(daySchedule);
    }
}
