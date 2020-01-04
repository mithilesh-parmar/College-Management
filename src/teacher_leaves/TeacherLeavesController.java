package teacher_leaves;

import com.google.cloud.firestore.QuerySnapshot;
import custom_view.SearchTextFieldController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import listeners.DataChangeListener;
import model.TeacherLeave;
import utility.SearchCallback;
import utility.TeacherLeavesUtility;

import java.net.URL;
import java.util.ResourceBundle;

public class TeacherLeavesController implements Initializable, SearchCallback, DataChangeListener, TeacherLeavesCardListener {
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

        teacherLeavesFlowPane.getChildren().addAll(firestoreUtility.teacherLeavesCards);


        progressIndicator.visibleProperty().bind(dataLoading);
        searchTextField.setCallback(this);
        firestoreUtility.setListener(this);
        firestoreUtility.setCardListener(this);

        firestoreUtility.getTeacherLeaves();

    }

    @Override
    public void performSearch(String oldValue, String newValue) {

    }

    @Override
    public void onDataLoaded(ObservableList data) {
        dataLoading.set(false);
        teacherLeavesFlowPane.getChildren().addAll(firestoreUtility.teacherLeavesCards);
    }

    @Override
    public void onDataChange(QuerySnapshot data) {
        dataLoading.set(true);
    }

    @Override
    public void onError(Exception e) {
        dataLoading.set(false);
    }

    @Override
    public void onContextMenuRequested(TeacherLeave leave, MouseEvent event) {
        teacherLeaveContextMenu.show(teacherLeavesFlowPane, event.getScreenX(), event.getScreenY());
    }
}
