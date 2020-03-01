package home;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import model.Menu;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    public VBox mainMenu;
    public BorderPane mainScreen;


    private ObservableList<ToggleButton> menuList;
    private ToggleGroup toggleGroup = new ToggleGroup();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        menuList = FXCollections.observableArrayList();

        setUpMenu();

        mainMenu.getChildren().addAll(menuList);
        toggleGroup.getToggles().addAll(menuList);
        toggleGroup.selectedToggleProperty().addListener((observableValue, toggle, t1) -> {
            try {
                changePageTo(FXMLLoader.load(getClass().getResource((String) t1.getUserData())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        toggleGroup.selectToggle(menuList.get(0));


//        mainMenu.setItems(menuList);


//        mainMenu.setCellFactory(new Callback<>() {
//
//            public ListCell<Menu> call(ListView<Menu> param) {
//
//                return new ListCell<>() {
//                    @Override
//                    public void updateItem(Menu item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (item != null) {
//                            setText(item.getName());
//                        }
//                    }
//                };
//            }
//        });


//        mainMenu.getSelectionModel().selectedItemProperty().addListener(((observableValue, menu, t1) -> {
//            try {
//                changePageTo(FXMLLoader.load(getClass().getResource(observableValue.getValue().getScreenPath())));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        ));

//        mainMenu.getSelectionModel().selectFirst();
    }


    /**
     * change the center node of borderpane to provided node
     *
     * @param node
     */
    private void changePageTo(Node node) {
        mainScreen.setCenter(node);
    }

    private void setUpMenu() {
        menuList.add(getScreenButton("Back Logs", "/backlog/BackLogView.fxml", ""));
        menuList.add(getScreenButton("Fee", "/fee/FeeView.fxml", ""));
        menuList.add(getScreenButton("Students", "/students/StudentView.fxml", ""));
        menuList.add(getScreenButton("Teachers", "/teachers/TeacherView.fxml", ""));
        menuList.add(getScreenButton("Events", "/events/EventsView.fxml", ""));
        menuList.add(getScreenButton("Time Table", "/time_table/TimeTableView.fxml", ""));
        menuList.add(getScreenButton("Course", "/course/CourseView.fxml", ""));
        menuList.add(getScreenButton("Teacher Leaves", "/teacher_leaves/LeavesView.fxml", ""));
        menuList.add(getScreenButton("Attendance", "/attendance/AttendanceView.fxml", ""));
        menuList.add(getScreenButton("Documents", "/documents/DocumentView.fxml", ""));
        menuList.add(getScreenButton("Exams", "/exams/ExamView.fxml", ""));
        menuList.add(getScreenButton("Result", "/result/ResultView.fxml", ""));
    }


    private ToggleButton getScreenButton(String title, String screenPath, String test) {

        ToggleButton toggleButton = new ToggleButton(title);
        toggleButton.setPrefHeight(35);
        toggleButton.setMinHeight(35);
        toggleButton.setMaxHeight(35);

        toggleButton.setPrefWidth(120);
        toggleButton.setMinWidth(100);
        toggleButton.setMaxWidth(150);

        toggleButton.setUserData(screenPath);
        toggleButton.setId("menu-toggle");
        return toggleButton;
    }
}
