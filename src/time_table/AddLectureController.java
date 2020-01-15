package time_table;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import model.Lecture;
import model.Section;

import java.net.URL;
import java.util.ResourceBundle;

public class AddLectureController implements Initializable {

    public TextField startTimeTextField;
    public TextField endTimeTextField;
    public Button submitButton;


    public ComboBox<String> subjectComboBox;
    public ComboBox<String> dayComboBox;

    private ListProperty<String> days = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ListProperty<String> subjects = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ObjectProperty<Section> section = new SimpleObjectProperty<>();

    private StringProperty selectedSubject = new SimpleStringProperty(),
            selectedDay = new SimpleStringProperty(),
            selectedStartTime = new SimpleStringProperty(),
            selectedEndTime = new SimpleStringProperty();

    private BooleanProperty canSubmit = new SimpleBooleanProperty(false);

    private LectureListener listener;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        submitButton.visibleProperty().bind(canSubmit);
        dayComboBox.itemsProperty().bind(days);
        subjectComboBox.itemsProperty().bind(subjects);
        submitButton.setOnAction(actionEvent -> processData());

        startTimeTextField.textProperty().addListener((observableValue, s, t1) -> {
            selectedStartTime.set(t1);
            checkCanSubmit();
        });

        endTimeTextField.textProperty().addListener((observableValue, s, t1) -> {
            selectedEndTime.set(t1);
            checkCanSubmit();
        });

        subjectComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            selectedSubject.set(t1);
            checkCanSubmit();
        });

        dayComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            selectedDay.set(t1);
            checkCanSubmit();
        });
    }

    private void checkCanSubmit() {
        canSubmit.set(
                selectedDay.get() != null && !selectedDay.get().isEmpty()
                        && selectedSubject.get() != null && !selectedSubject.get().isEmpty()
                        && selectedEndTime.get() != null && !selectedEndTime.get().isEmpty()
                        && selectedStartTime.get() != null && !selectedStartTime.get().isEmpty()
        );
    }

    public void setListener(LectureListener listener) {
        this.listener = listener;
    }

    public void setSection(Section section) {
        this.section.set(section);
        if (section == null) return;
        for (int i = 1; i <= 6; i++) {
            days.get().add(getWeekDay(i));
        }
        subjects.get().setAll(section.getSubjects());
    }

    private int getDay(String day) {
        switch (day) {
            case "Monday":
                return 1;
            case "Tuesday":
                return 2;
            case "Wednesday":
                return 3;
            case "Thursday":
                return 4;
            case "Friday":
                return 5;
            case "Saturday":
                return 6;
        }
        return -1;
    }

    private String getWeekDay(int day) {
        switch (day) {
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
        }
        return "Error";
    }

    private void processData() {
        if (listener == null) return;
        Lecture lecture = new Lecture(
                selectedSubject.get(),
                selectedEndTime.get(),
                selectedStartTime.get(),
                String.valueOf(getDay(selectedDay.get()))
        );
        listener.onLectureAdded(lecture);
    }
}
