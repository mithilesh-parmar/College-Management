package teacher_leaves;

import com.google.cloud.firestore.QuerySnapshot;
import custom_view.SearchTextFieldController;
import custom_view.card_view.Card;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import listeners.DataChangeListener;
import model.Leave;
import model.Teacher;
import teacher_leaves.edit_view.LeaveEditController;
import teacher_leaves.edit_view.LeaveEditListener;
import utility.SearchCallback;
import utility.TeacherLeavesUtility;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LeavesController implements Initializable, SearchCallback, DataChangeListener, LeavesCardListener {
    public SearchTextFieldController searchTextField;
    public FlowPane teacherLeavesFlowPane;
    public ProgressIndicator progressIndicator;

    private TeacherLeavesUtility firestoreUtility = TeacherLeavesUtility.getInstance();
    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);

    private ContextMenu teacherLeaveContextMenu = new ContextMenu();
    private MenuItem acceptMenuButton = new MenuItem("Accept");
    private MenuItem declineMenuButton = new MenuItem("Decline");
    private MenuItem editMenuButton = new MenuItem("Edit");
    private MenuItem cancelMenuButton = new MenuItem("Cancel");


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        teacherLeavesFlowPane.setHgap(10);
        teacherLeavesFlowPane.setVgap(10);
        teacherLeavesFlowPane.setAlignment(Pos.CENTER_LEFT);
        teacherLeaveContextMenu.setHideOnEscape(true);
        teacherLeaveContextMenu.setAutoHide(true);
        teacherLeavesFlowPane.setPadding(new Insets(10));


        teacherLeaveContextMenu.getItems().addAll(acceptMenuButton, declineMenuButton, editMenuButton, cancelMenuButton);

        teacherLeavesFlowPane.getChildren().setAll(firestoreUtility.teacherLeavesCards);


        progressIndicator.visibleProperty().bind(dataLoading);
        searchTextField.setCallback(this);
        firestoreUtility.setListener(this);
        firestoreUtility.setCardListener(this);

        firestoreUtility.getTeacherLeaves();

    }


    @Override
    public void onDataLoaded(ObservableList data) {
        dataLoading.set(false);
        teacherLeavesFlowPane.getChildren().setAll(firestoreUtility.teacherLeavesCards);
    }

    @Override
    public void onDataChange(QuerySnapshot data) {
        dataLoading.set(true);
    }

    @Override
    public void onError(Exception e) {
        dataLoading.set(false);
    }

    private void loadEditView(Leave leave) {
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/teacher_leaves/edit_view/LeaveEditView.fxml"));
        final Stage stage = new Stage();
        Parent parent = null;
        try {


            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Teacher Details");

            parent = loader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            LeaveEditController controller = loader.getController();

            controller.setLeave(leave);
            controller.setListener(new LeaveEditListener() {
                @Override
                public void onEdit(Leave leave) {
                    close(stage);
                    firestoreUtility.updateLeave(leave);
                }
            });

            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onContextMenuRequested(Leave leave, MouseEvent event) {

        acceptMenuButton.setOnAction(actionEvent -> {
        });
        declineMenuButton.setOnAction(actionEvent -> {
        });
        editMenuButton.setOnAction(actionEvent -> loadEditView(leave));
        cancelMenuButton.setOnAction(actionEvent -> teacherLeaveContextMenu.hide());
        teacherLeaveContextMenu.show(teacherLeavesFlowPane, event.getScreenX(), event.getScreenY());
    }

    @Override
    public void onCardClick(Leave leave) {
        loadEditView(leave);
    }


    @Override
    public void performSearch(String oldValue, String newValue) {
        if (dataLoading.get()) return;
        // if pressing backspace then set initial values to list
        if (oldValue != null && (newValue.length() < oldValue.length())) {

            teacherLeavesFlowPane.getChildren().setAll(firestoreUtility.teacherLeavesCards);
        }

        // convert the searched text to uppercase
        String searchtext = newValue.toUpperCase();

        ObservableList<Card> subList = FXCollections.observableArrayList();
        for (Leave p : firestoreUtility.teacherLeaves) {
            String text =
                    p.getTeacherName().toUpperCase() + " "
                            + p.getReason().toUpperCase();
            // if the search text contains the manufacturer then add it to sublist
            if (text.contains(searchtext)) {
                subList.add(firestoreUtility.teacherLeaveCardMapProperty.get(p));
            }

        }
        teacherLeavesFlowPane.getChildren().setAll(subList);
    }

}
