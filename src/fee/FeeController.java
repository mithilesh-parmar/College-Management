package fee;

import com.google.cloud.firestore.QuerySnapshot;
import fee.add_fee.AddFeeController;
import fee.add_fee.AddFeeListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import listeners.DataChangeListener;
import model.Fee;
import org.apache.poi.ss.usermodel.BorderStyle;
import utility.FeeFirestoreUtility;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static custom_view.dialog_helper.CustomDialog.showInputDialogWithOneParameter;

public class FeeController implements Initializable, DataChangeListener {

    public TableView<Fee> feeTable;
    public ProgressIndicator progressIndicator;
    public Button addButton;
    public Button findButton;
    public Button clearButton;

    private FeeFirestoreUtility firestoreUtility = FeeFirestoreUtility.getInstance();

    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);
    private BooleanProperty canViewClearButton = new SimpleBooleanProperty(false);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clearButton.visibleProperty().bind(canViewClearButton);
        clearButton.setDefaultButton(true);
        clearButton.setPadding(new Insets(8));
        clearButton.setBorder(createBorder(Color.WHITESMOKE, BorderStrokeStyle.SOLID, 8, 1));
        clearButton.setOnAction(actionEvent -> {
            resetTable();
        });


        findButton.disableProperty().bind(canViewClearButton);
        findButton.setPadding(new Insets(8));
        findButton.setDefaultButton(true);
        findButton.setOnAction(actionEvent -> loadStudentFee());
        findButton.setBorder(createBorder(Color.WHITESMOKE, BorderStrokeStyle.SOLID, 8, 2));

        addButton.setPadding(new Insets(8));
        progressIndicator.visibleProperty().bind(dataLoading);
        addButton.setOnAction(actionEvent -> loadAddView());
        addButton.setBorder(createBorder(Color.WHITESMOKE, BorderStrokeStyle.SOLID, 8, 2));
        firestoreUtility.setListener(this);
        firestoreUtility.getFees();

    }

    private void resetTable() {
        canViewClearButton.set(false);
        feeTable.setItems(firestoreUtility.fees);
    }

    private void loadStudentFee() {
        Optional<String> result = showInputDialogWithOneParameter("Student Details", "Student ID ");
        result.ifPresent(data -> {
            feeTable.setItems(firestoreUtility.getAllFeesForStudent(data));
            canViewClearButton.set(true);
        });
    }

    private Border createBorder(Color color, BorderStrokeStyle borderStrokeStyle, double radius, double width) {
        return new Border(new BorderStroke(color, borderStrokeStyle, new CornerRadii(radius), new BorderWidths(width)));
    }

    private void loadAddView() {
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/fee/add_fee/AddFeeView.fxml"));
        final Stage stage = new Stage();
        Parent parent = null;
        try {


            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Fee Details");

            parent = loader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            AddFeeController controller = loader.getController();
            controller.setListener(new AddFeeListener() {
                @Override
                public void onFeeSubmit(Fee fee) {
                    close(stage);
                    dataLoading.set(true);
                    firestoreUtility.addFee(fee);
                }
            });
            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDataLoaded(ObservableList data) {
        dataLoading.set(false);
        feeTable.getItems().setAll(firestoreUtility.fees);
    }

    @Override
    public void onDataChange(QuerySnapshot data) {
        dataLoading.set(true);
    }

    @Override
    public void onError(Exception e) {
        dataLoading.set(false);
    }
}
