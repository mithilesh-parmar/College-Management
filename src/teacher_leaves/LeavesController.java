package teacher_leaves;

import com.google.cloud.firestore.QuerySnapshot;
import custom_view.SearchTextFieldController;
import custom_view.card_view.Card;
import custom_view.card_view.LeaveCard;
import custom_view.card_view.LeaveCardListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import listeners.DataChangeListener;
import model.Leave;
import teacher_leaves.edit_view.LeaveEditController;
import teacher_leaves.edit_view.LeaveEditListener;
import utility.SearchCallback;
import utility.TeacherLeavesUtility;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LeavesController implements Initializable, SearchCallback, DataChangeListener, LeaveCardListener {


    public SearchTextFieldController searchTextField;
    public FlowPane teacherLeavesFlowPane;
    public ProgressIndicator progressIndicator;
    public ComboBox<Filter> filterComboBox;
    public ScrollPane scrollPane;

    private TeacherLeavesUtility firestoreUtility = TeacherLeavesUtility.getInstance();
    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);

    private ContextMenu teacherLeaveContextMenu = new ContextMenu();
    private MenuItem acceptMenuButton = new MenuItem("Accept");
    private MenuItem declineMenuButton = new MenuItem("Decline");
    private MenuItem editMenuButton = new MenuItem("Edit");
    private MenuItem cancelMenuButton = new MenuItem("Cancel");

    private final int ACCEPT = 1, DECLINE = 2;

    @Override
    public void onClick(Leave leave) {
        loadEditView(leave);
    }

    @Override
    public void onAcceptAction(Leave leave) {
        leave.setStatus(ACCEPT);
        firestoreUtility.updateLeave(leave);
    }

    @Override
    public void onDeclineAction(Leave leave) {
        leave.setStatus(DECLINE);
        firestoreUtility.updateLeave(leave);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        teacherLeavesFlowPane.setHgap(10);
        teacherLeavesFlowPane.setVgap(10);
        teacherLeavesFlowPane.setAlignment(Pos.TOP_LEFT);


        scrollPane.viewportBoundsProperty().addListener((ov, oldBounds, bounds) -> {
            teacherLeavesFlowPane.setPrefWidth(bounds.getWidth());
            teacherLeavesFlowPane.setPrefHeight(bounds.getHeight());
        });

        teacherLeaveContextMenu.setHideOnEscape(true);
        teacherLeaveContextMenu.setAutoHide(true);
        teacherLeavesFlowPane.setPadding(new Insets(10));
        filterComboBox.setPadding(new Insets(25));

        filterComboBox.getItems().addAll(Filter.APPROVED, Filter.PENDING, Filter.DECLINED);

        filterComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, filter, t1) -> {
            if (t1 == Filter.ALL) {
                showAllLeaves();
            } else if (t1 == Filter.APPROVED) {
                showApprovedLeaves();
            } else if (t1 == Filter.PENDING) {
                showPendingLeaves();
            } else if (t1 == Filter.DECLINED) {
                showDeclinedLeaves();
            }
        });

        filterComboBox.getSelectionModel().select(Filter.APPROVED);

        teacherLeaveContextMenu.getItems().addAll(acceptMenuButton, declineMenuButton, editMenuButton, cancelMenuButton);

        teacherLeavesFlowPane.getChildren().setAll(firestoreUtility.teacherLeavesCards);


        progressIndicator.visibleProperty().bind(dataLoading);
        searchTextField.setCallback(this);
        firestoreUtility.setListener(this);
        firestoreUtility.setCardListener(this);

        firestoreUtility.getTeacherLeaves();

    }

    private void showAllLeaves() {
        teacherLeavesFlowPane.getChildren().setAll(firestoreUtility.teacherLeavesCards);
    }

    private void showApprovedLeaves() {
        teacherLeavesFlowPane.getChildren().setAll(firestoreUtility.approvedLeavesCards);
    }

    private void showPendingLeaves() {
        teacherLeavesFlowPane.getChildren().setAll(firestoreUtility.pendingLeavesCards);
    }

    private void showDeclinedLeaves() {
        teacherLeavesFlowPane.getChildren().setAll(firestoreUtility.declinedLeavesCards);
    }

    @Override
    public void onDataLoaded(ObservableList data) {
        dataLoading.set(false);
        filterComboBox.getSelectionModel().select(Filter.APPROVED);
        teacherLeavesFlowPane.getChildren().setAll(firestoreUtility.approvedLeavesCards);
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
                    dataLoading.set(true);
                    firestoreUtility.updateLeave(leave);
                }
            });

            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    @Override
//    public void onContextMenuRequested(Leave leave, MouseEvent event) {
//
//        acceptMenuButton.setOnAction(actionEvent -> {
//            leave.setStatus(1);
//            firestoreUtility.updateLeave(leave);
//        });
//        declineMenuButton.setOnAction(actionEvent -> {
//            leave.setStatus(2);
//            firestoreUtility.updateLeave(leave);
//        });
//        editMenuButton.setOnAction(actionEvent -> loadEditView(leave));
//        cancelMenuButton.setOnAction(actionEvent -> teacherLeaveContextMenu.hide());
//        teacherLeaveContextMenu.show(teacherLeavesFlowPane, event.getScreenX(), event.getScreenY());
//    }
//
//    @Override
//    public void onCardClick(Leave leave) {
//        loadEditView(leave);
//    }


    @Override
    public void performSearch(String oldValue, String newValue) {
        if (dataLoading.get()) return;
        // if pressing backspace then set initial values to list
        if (oldValue != null && (newValue.length() < oldValue.length())) {

            teacherLeavesFlowPane.getChildren().setAll(firestoreUtility.teacherLeavesCards);
        }

        // convert the searched text to uppercase
        String searchtext = newValue.toUpperCase();

        ObservableList<LeaveCard> subList = FXCollections.observableArrayList();
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
