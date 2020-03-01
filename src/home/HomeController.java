package home;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
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

    private static final String QUIT = "QUIT";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        menuList = FXCollections.observableArrayList();

        setUpMenu();

        mainMenu.getChildren().addAll(menuList);
        toggleGroup.getToggles().addAll(menuList);
        toggleGroup.selectedToggleProperty().addListener((observableValue, toggle, t1) -> {
            if (t1.getUserData() == QUIT) System.exit(0);
            try {
                changePageTo(FXMLLoader.load(getClass().getResource((String) t1.getUserData())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        toggleGroup.selectToggle(menuList.get(0));
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
        menuList.add(getScreenButton("Back Logs", "/backlog/BackLogView.fxml", "/assets/backlog.png"));
        menuList.add(getScreenButton("Fee", "/fee/FeeView.fxml", "/assets/fee.png"));
        menuList.add(getScreenButton("Students", "/students/StudentView.fxml", "/assets/student.png"));
        menuList.add(getScreenButton("Teachers", "/teachers/TeacherView.fxml", "/assets/teacher.png"));
        menuList.add(getScreenButton("Events", "/events/EventsView.fxml", "/assets/event.png"));
        menuList.add(getScreenButton("Time Table", "/time_table/TimeTableView.fxml", "/assets/timetable.png"));
        menuList.add(getScreenButton("Course", "/course/CourseView.fxml", "/assets/course.png"));
        menuList.add(getScreenButton(" Leaves", "/teacher_leaves/LeavesView.fxml", "/assets/leave.png"));
        menuList.add(getScreenButton("Attendance", "/attendance/AttendanceView.fxml", "/assets/attendance.png"));
        menuList.add(getScreenButton("Documents", "/documents/DocumentView.fxml", "/assets/documents.png"));
        menuList.add(getScreenButton("Exams", "/exams/ExamView.fxml", "/assets/exam.png"));
        menuList.add(getScreenButton("Result", "/result/ResultView.fxml", "/assets/backlog.png"));
        menuList.add(getScreenButton("Exit", QUIT, "/assets/exit.png"));
    }


    private ToggleButton getScreenButton(String title, String screenPath, String iconPath) {

        ToggleButton toggleButton = new ToggleButton(title);
        toggleButton.setMinHeight(35);
        toggleButton.setMaxHeight(55);

        toggleButton.setMinWidth(100);
        toggleButton.setMaxWidth(200);
        toggleButton.setAlignment(Pos.CENTER_LEFT);

        ImageView imageView = new ImageView(iconPath);
        imageView.setFitWidth(30);
        imageView.setFitHeight(30);

        toggleButton.setGraphic(imageView);
        toggleButton.setUserData(screenPath);
        toggleButton.setId("menu-toggle");
        return toggleButton;
    }
}
