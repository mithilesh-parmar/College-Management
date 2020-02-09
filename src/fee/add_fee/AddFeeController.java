package fee.add_fee;

import javafx.beans.property.*;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import model.Fee;

import java.net.URL;
import java.util.ResourceBundle;

public class AddFeeController implements Initializable {
    public Button submitButton;
    public TextField studentIDField;
    public TextField amountField;
    public ComboBox<Fee.Type> typeComboBox;

    private BooleanProperty canSubmit = new SimpleBooleanProperty(false);
    private StringProperty selectedStudentAdmissionId = new SimpleStringProperty();
    private LongProperty selectedAmount = new SimpleLongProperty();
    private ObjectProperty<Fee.Type> selectedFeeType = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        submitButton.visibleProperty().bind(canSubmit);
        typeComboBox.getItems().addAll(Fee.Type.ADMISSION_FEE, Fee.Type.FINE);


    }
}
