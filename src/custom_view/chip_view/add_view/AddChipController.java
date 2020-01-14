package custom_view.chip_view.add_view;

import custom_view.chip_view.Chip;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AddChipController implements Initializable {


    public TextField nameTextField;
    public Button submitButton;

    private AddChipListener listener;

    public void setListener(AddChipListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        submitButton.setOnAction(actionEvent -> {

            if (listener != null) listener.onChipAdded(new Chip(nameTextField.getText()));

        });
    }
}
