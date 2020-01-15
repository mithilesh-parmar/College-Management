package model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

import java.util.List;

public class ClassItem {

    private ListProperty<Section> sections;
//    private StringProperty className;
//    private StringProperty id;

    public ClassItem(List<Section> sections) {
        this.sections =
                new SimpleListProperty<>(FXCollections.observableArrayList(sections));
    }


}
