package home;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import model.Menu;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    public ListView<Menu> mainMenu;
    public BorderPane mainScreen;


    private ObservableList<Menu> menuList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        menuList = FXCollections.observableArrayList();

        setUpMenu();


        mainMenu.setItems(menuList);


        mainMenu.setCellFactory(new Callback<>() {

            public ListCell<Menu> call(ListView<Menu> param) {

                return new ListCell<>() {
                    @Override
                    public void updateItem(Menu item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getName());


                        }
                    }
                };
            }
        });


        mainMenu.getSelectionModel().selectedItemProperty().addListener(((observableValue, menu, t1) -> {
            try {
                changePageTo(FXMLLoader.load(getClass().getResource(observableValue.getValue().getScreenPath())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ));

        mainMenu.getSelectionModel().selectFirst();
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
        menuList.add(new Menu("Students", "/students/StudentView.fxml", ""));
        menuList.add(new Menu("Teachers", "/teachers/TeacherView.fxml", ""));
        menuList.add(new Menu("Events", "/events/EventsView.fxml", ""));
        menuList.add(new Menu("Time Table", "/time_table/TimeTableView.fxml", ""));
        menuList.add(new Menu("Teacher Leaves", "/teacher_leaves/LeavesView.fxml", ""));
        menuList.add(new Menu("attendance", "/attendance/AttendanceView.fxml", ""));
        menuList.add(new Menu("Documents", "/documents/DocumentView.fxml", ""));
    }
}
