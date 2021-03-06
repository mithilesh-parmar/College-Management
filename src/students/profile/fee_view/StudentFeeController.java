package students.profile.fee_view;

import com.google.cloud.firestore.QuerySnapshot;
import fee.add_fee.AddFeeController;
import fee.add_fee.AddFeeListener;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import listeners.DataChangeListener;
import model.Fee;
import model.Student;
import utility.ClassFirestoreUtility;
import utility.FeeFirestoreUtility;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StudentFeeController implements Initializable {

    public Label totalFeeLabel;
    public Label amountPaidLabel;
    public Label scholarshipLabel;
    public TableView<Fee> feeTable;
    public ProgressIndicator progressIndicator;
    public Label dueAmountLabel;
    public BorderPane mainView;
    public Button addButton;

    private FeeFirestoreUtility feeFirestoreUtility = FeeFirestoreUtility.getInstance();
    private ClassFirestoreUtility classFirestoreUtility = ClassFirestoreUtility.getInstance();
    private BooleanProperty feeDataLoading = new SimpleBooleanProperty(true);
    private BooleanProperty classDataLoading = new SimpleBooleanProperty(true);

    private ObjectProperty<Student> student = new SimpleObjectProperty<>();
    private ObjectProperty<Long> totalFee = new SimpleObjectProperty<>(-1L);
    private ObjectProperty<Long> totalAmountPaid = new SimpleObjectProperty<>(-1L);
    private ObjectProperty<Long> dueAmount = new SimpleObjectProperty<>(-1L);
    private ObjectProperty<Long> totalScholarship = new SimpleObjectProperty<>(-1L);

    private BooleanProperty canCalculateDueAmount = new SimpleBooleanProperty(false);

    private StudentFeeCallback feeCallback;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        VBox.setMargin(addButton, new Insets(18));
        progressIndicator.visibleProperty().bind(feeDataLoading);
        addButton.setOnAction(actionEvent -> loadAddView());
        addButton.setBorder(createBorder(Color.WHITESMOKE, BorderStrokeStyle.SOLID, 8, 2));

        progressIndicator.visibleProperty().bind(feeDataLoading.or(classDataLoading));
        mainView.visibleProperty().bind(feeDataLoading.or(classDataLoading).not());
        totalFeeLabel.textProperty().bind(totalFee.asString());
        amountPaidLabel.textProperty().bind(totalAmountPaid.asString());
        dueAmountLabel.textProperty().bind(dueAmount.asString());
        scholarshipLabel.textProperty().bind(totalScholarship.asString());

        totalFee.addListener((observableValue, aLong, t1) -> {
            if (t1 == null) return;
            System.out.println("Got Total Fee: " + t1);
            checkCanCalculateDueAmount();
        });

        totalAmountPaid.addListener((observableValue, aLong, t1) -> {
            if (t1 == null) return;
            System.out.println("Go Total Paid: " + t1);
            checkCanCalculateDueAmount();
        });

        canCalculateDueAmount.addListener((observableValue, aBoolean, t1) -> {
            if (t1 == null) return;
            if (t1) {
                System.out.println("Setting due Amount");
                long discountAmount = totalFee.get() * (totalScholarship.get() / 100);
                System.out.println("Discount: " + discountAmount);
                long amountToBePaid = totalFee.get() - discountAmount;
                System.out.println("Amount To Be Paid: " + amountToBePaid);
                dueAmount.set(amountToBePaid - totalAmountPaid.get());
                dueAmount.set(totalFee.get() - totalAmountPaid.get());
            }
        });

        totalScholarship.addListener((observableValue, aLong, t1) -> {
            if (t1 == null) return;
            System.out.println("Go Total Scholarship: " + t1);
            checkCanCalculateDueAmount();
        });

        feeFirestoreUtility.setListener(new DataChangeListener() {
            @Override
            public void onDataLoaded(ObservableList data) {
                feeDataLoading.set(false);
                if (student.get().getAdmissionID() == null) return;

                feeTable.setItems(feeFirestoreUtility.getAllFeesForStudent(student.get().getAdmissionID()));
                totalAmountPaid.set(feeFirestoreUtility.getFeeAmountPaid(student.get().getAdmissionID()));

            }

            @Override
            public void onDataChange(QuerySnapshot data) {
                feeDataLoading.set(true);
            }

            @Override
            public void onError(Exception e) {
                feeDataLoading.set(false);
            }
        });
        classFirestoreUtility.setListener(new DataChangeListener() {
            @Override
            public void onDataLoaded(ObservableList data) {
                classDataLoading.set(false);
                totalFee.set(classFirestoreUtility.getClassItem(student.get().getClassName()).getFee());
                totalScholarship.set(student.get().getScholarship());
            }

            @Override
            public void onDataChange(QuerySnapshot data) {
                classDataLoading.set(true);
            }

            @Override
            public void onError(Exception e) {
                classDataLoading.set(false);
            }
        });
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
            controller.setStudent(student.get());
            if (feeCallback != null)
                controller.setListener(new AddFeeListener() {
                    @Override
                    public void onFeeSubmit(Fee fee) {
                        close(stage);
                        feeCallback.onStudentFeeAdded(fee);
                    }
                });

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setCallback(StudentFeeCallback feeCallback) {
        this.feeCallback = feeCallback;
    }

    private void checkCanCalculateDueAmount() {
        System.out.println("Checking With Values : " + totalFee.get() + " and " + totalAmountPaid.get() + " With " + totalScholarship.get());
        canCalculateDueAmount.set(
                totalFee.get() >= 0 && totalFee.get() != null
                        && totalAmountPaid.get() >= 0 && totalAmountPaid.get() != null
                        && totalScholarship.get() >= 0 && totalScholarship.get() != null
        );
    }

    private Border createBorder(Color color, BorderStrokeStyle borderStrokeStyle, double radius, double width) {
        return new Border(new BorderStroke(color, borderStrokeStyle, new CornerRadii(radius), new BorderWidths(width)));
    }

    public void setStudent(Student student) {
        this.student.set(student);
        feeFirestoreUtility.getFees();
        classFirestoreUtility.getClasses();
    }

}
