package students.fee_view;

import com.google.cloud.firestore.QuerySnapshot;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import listeners.DataChangeListener;
import model.Fee;
import model.Student;
import utility.ClassFirestoreUtility;
import utility.FeeFirestoreUtility;

import java.net.URL;
import java.util.ResourceBundle;

public class StudentFeeController implements Initializable {
    public Label totalFeeLabel;
    public Label amountPaidLabel;
    public Label scholarshipLabel;
    public TableView<Fee> feeTable;
    public ProgressIndicator progressIndicator;

    private FeeFirestoreUtility feeFirestoreUtility = FeeFirestoreUtility.getInstance();
    private ClassFirestoreUtility classFirestoreUtility = ClassFirestoreUtility.getInstance();
    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);

    private ObjectProperty<Student> student = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        progressIndicator.visibleProperty().bind(dataLoading);
        feeFirestoreUtility.setListener(new DataChangeListener() {
            @Override
            public void onDataLoaded(ObservableList data) {
                dataLoading.set(false);
                feeTable.setItems(feeFirestoreUtility.getFeesForStudent(student.get().getAdmissionID()));
            }

            @Override
            public void onDataChange(QuerySnapshot data) {
                dataLoading.set(true);
            }

            @Override
            public void onError(Exception e) {
                dataLoading.set(false);
            }
        });
        classFirestoreUtility.setListener(new DataChangeListener() {
            @Override
            public void onDataLoaded(ObservableList data) {
//                dataLoading.set(false);
                totalFeeLabel.setText(String.valueOf(classFirestoreUtility.getClassItem(student.get().getClassName()).getFee()));
//                scholarshipLabel.setText(String.valueOf(student.get().getScholarship()));
            }

            @Override
            public void onDataChange(QuerySnapshot data) {
                dataLoading.set(true);
            }

            @Override
            public void onError(Exception e) {
//                dataLoading.set(false);
            }
        });
    }

    public void setStudent(Student student) {
        this.student.set(student);
        feeFirestoreUtility.getFees();
        classFirestoreUtility.getClasses();
    }

//    @Override
//    public void onDataLoaded(ObservableList data) {
//        dataLoading.set(false);
//        if (student.get() == null) return;
//
//    }
//
//    @Override
//    public void onDataChange(QuerySnapshot data) {
//        dataLoading.set(true);
//    }
//
//    @Override
//    public void onError(Exception e) {
//        dataLoading.set(false);
//    }
}
