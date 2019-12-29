package view_helper;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import time_table.AddLectureController;

import java.io.IOException;

public class PopupWindow {

    private String path, title;
    private Object controller;
    private PopWindowCallback listener;
    private Stage stage;

    private FXMLLoader loader;


    public PopupWindow(String path, String title) {
        this.path = path;
        loader = new FXMLLoader(getClass().getResource(path));
        controller = loader.getController();
        this.title = title;
    }

    public void setListener(PopWindowCallback listener) {
        this.listener = listener;
    }

    public <T> void setController(T obj) {
        this.controller = obj;
    }

    public void display() {

        Parent parent = null;
        try {
            stage = new Stage();




//            if (controller != null) loader.setController(controller);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);

            parent = loader.load();
            Scene scene = new Scene(parent, 600, 300);


            stage.setScene(scene);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public <T> T getController() {
        return loader.getController();
    }

    public void close() {
        stage.close();
    }

}


