package course.add_course;

import custom_view.chip_view.Chip;
import custom_view.chip_view.ChipView;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.Course;

import java.net.URL;
import java.util.*;

public class AddCourseController implements Initializable {
    public TextField courseNameTextField;
    public Button submitButton;
    public ComboBox<Long> yearsComboBox;

    private final int MAX_YEARS = 5;
    public ListView<ChipView> subjectListView;
    private ListProperty<Long> yearsList = new SimpleListProperty<>(FXCollections.observableArrayList());
    private BooleanProperty canSubmit = new SimpleBooleanProperty(true);
    private ListProperty<ChipView> chipViews = new SimpleListProperty<>(FXCollections.observableArrayList());

    private StringProperty courseName = new SimpleStringProperty();
    private LongProperty selectedYears = new SimpleLongProperty();
    private MapProperty<String, List<String>> subjects = new SimpleMapProperty<>(FXCollections.observableHashMap());


    private Course course;
    private AddCourseCallback callback;

    public void setCallback(AddCourseCallback callback) {
        this.callback = callback;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


//        Years comboBox
        for (int i = 1; i <= MAX_YEARS; i++) {
            yearsList.get().add((long) i);
        }
        yearsComboBox.itemsProperty().bind(yearsList);
        subjectListView.itemsProperty().bind(chipViews);

        yearsComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, integer, t1) -> {

            chipViews.get().clear();
            for (int i = 1; i <= t1; i++) {
                chipViews.get().add(new ChipView(String.valueOf(i)));
            }
            selectedYears.setValue(t1);
        });
        courseNameTextField.textProperty().addListener((observableValue, s, t1) -> courseName.set(t1));

        submitButton.setOnAction(actionEvent -> onSubmitAction());

        submitButton.visibleProperty().bind(canSubmit);

    }

    private void onSubmitAction() {
        if (callback == null) return;
        chipViews.forEach(chipView -> {
            String yearName = chipView.getTitle();
            subjects.get().put(yearName, new ArrayList<>());
            chipView.getChips().forEach(subjectChip -> {
                String name = subjectChip.getTitle();
                subjects.get().get(yearName).add(name);
            });
        });


        if (course == null)
            callback.onCourseSubmit(new Course(
                    "",
                    courseName.get(),
                    selectedYears.longValue(),
                    subjects.get()
            ));
        else
            callback.onCourseUpdate(new Course(
                    course.getId(),
                    courseName.get(),
                    selectedYears.longValue(),
                    subjects.get()
            ));
    }

    private void checkCanSubmit() {
        canSubmit.set(
                selectedYears.get() <= 0 && courseName.get() != null
        );
    }

    public void setCourse(Course course) {
        this.course = course;
        loadData(course);
    }

    private void loadData(Course course) {

        System.out.println("Loading Data for " + course);

        selectedYears.set(course.getYears());
        courseName.set(course.getName());
        yearsComboBox.getSelectionModel().select(course.getYears());
        courseNameTextField.setText(course.getName());
        chipViews.clear();
        for (int i = 1; i <= selectedYears.get(); i++) {
            ChipView chipView = new ChipView(String.valueOf(i));
            chipView.setChips(course.getSubjects(i));
            chipViews.add(chipView);
        }

    }
}

