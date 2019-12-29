package teachers;

import com.google.cloud.firestore.QuerySnapshot;
import custom_view.SearchTextFieldController;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import listeners.DataChangeListener;
import model.Teacher;
import utility.SearchCallback;
import utility.TeacherFirestoreUtility;
import view_helper.PopupWindow;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TeacherController implements Initializable, DataChangeListener, SearchCallback {


    public TableView<Teacher> teacherTable;
    public ProgressIndicator progressIndicator;
    public SearchTextFieldController searchField;
    public Button addButton;

    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);
    private TeacherFirestoreUtility firestoreUtility = TeacherFirestoreUtility.getInstance();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchField.setCallback(this);
        firestoreUtility.setListener(this);
        firestoreUtility.getTeachers();
        addButton.setOnAction(actionEvent -> {
            loadAddView();

        });
        progressIndicator.visibleProperty().bind(dataLoading);
    }

    public void onTableKeyPressed(KeyEvent keyEvent) {

    }

    @Override
    public void onDataLoaded(ObservableList data) {
        dataLoading.set(false);
        teacherTable.setItems(firestoreUtility.teachers);
    }

    @Override
    public void onDataChange(QuerySnapshot data) {
        dataLoading.set(true);
    }

    @Override
    public void onError(Exception e) {

    }


    private void loadAddView() {
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/teachers/AddTeacherView.fxml"));
        final Stage stage = new Stage();
        Parent parent = null;
        try {


            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Teacher Details");

            parent = loader.load();
            Scene scene = new Scene(parent, 600, 300);


            stage.setScene(scene);
            AddTeacherController controller = loader.getController();

            controller.setListener(new TeacherListener() {
                @Override
                public void onTeacherSubmit(Teacher teacher) {
                    close(stage);
                    Platform.runLater(() -> {

                        firestoreUtility.addTeacherToFirestore(teacher);

                    });
                }

                @Override
                public void onTeacherEdit(Teacher teacher) {
                    close(stage);
                }

                @Override
                public void onCancel() {
                    close(stage);
                }
            });

            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void performSearch(String oldValue, String newValue) {
        if (dataLoading.get()) return;
        // if pressing backspace then set initial values to list
        if (oldValue != null && (newValue.length() < oldValue.length())) {
            teacherTable.setItems(firestoreUtility.teachers);
        }

        // convert the searched text to uppercase
        String searchtext = newValue.toUpperCase();

        ObservableList<Teacher> subList = FXCollections.observableArrayList();
        for (Teacher p : teacherTable.getItems()) {
            String text =
                    p.getName().toUpperCase() + " "
                            + p.getVerificationCode().toUpperCase() + " ";
            // if the search text contains the manufacturer then add it to sublist
            if (text.contains(searchtext)) {
                subList.add(p);
            }

        }
        // set the items to listview that matches
        teacherTable.setItems(subList);
    }


}
