package fee;

import com.google.cloud.firestore.QuerySnapshot;
import fee.add_fee.AddFeeController;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import listeners.DataChangeListener;
import model.Fee;
import model.Teacher;
import teachers.TeacherDetailViewListener;
import teachers.add_teacher.AddTeacherController;
import utility.FeeFirestoreUtility;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FeeController implements Initializable, DataChangeListener {

    public TableView<Fee> feeTable;
    public ProgressIndicator progressIndicator;
    public Button addButton;

    private FeeFirestoreUtility firestoreUtility = FeeFirestoreUtility.getInstance();

    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        progressIndicator.visibleProperty().bind(dataLoading);
        addButton.setOnAction(actionEvent -> loadAddView());
        firestoreUtility.setListener(this);
        firestoreUtility.getFees();
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
