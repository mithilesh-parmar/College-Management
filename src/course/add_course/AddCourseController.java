package course.add_course;

import custom_view.chip_view.ChipView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AddCourseController implements Initializable {
    public TextField courseNameTextField;
    public Button submitButton;
    public ComboBox<Integer> yearsComboBox;

    private final int MAX_YEARS = 5;
    public ListView<ChipView> subjectListView;
    private ListProperty<Integer> yearsList = new SimpleListProperty<>(FXCollections.observableArrayList());
    private BooleanProperty canSubmit = new SimpleBooleanProperty(true);
    private ListProperty<ChipView> chipViews = new SimpleListProperty<>(FXCollections.observableArrayList());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

//        Years comboBox
        for (int i = 1; i <= MAX_YEARS; i++) {
            yearsList.get().add(i);
        }
        yearsComboBox.itemsProperty().bind(yearsList);
        subjectListView.itemsProperty().bind(chipViews);

        yearsComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, integer, t1) -> {

            chipViews.get().clear();
            for (int i = 1; i <= t1; i++) {
                chipViews.get().add(new ChipView(String.valueOf(i)));
            }
        });

        submitButton.setOnAction(actionEvent -> {
        });

        submitButton.visibleProperty().bind(canSubmit);

    }
}

