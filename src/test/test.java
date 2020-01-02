package test;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class test {

    public static void main(String[] args) {

        ListProperty<Object> listProperty = new SimpleListProperty<>();
        listProperty.setValue(FXCollections.observableArrayList());
        listProperty.getValue().add(new Object());


    }

}
