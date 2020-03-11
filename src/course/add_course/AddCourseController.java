package course.add_course;

import com.google.cloud.firestore.Query;
import custom_view.chip_view.Chip;
import custom_view.chip_view.ChipView;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.ClassItem;
import model.Course;
import model.Section;
import utility.FirestoreConstants;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class AddCourseController implements Initializable {
    public TextField courseNameTextField;
    public Button submitButton;
    public ComboBox<Long> yearsComboBox;

    private final int MAX_YEARS = 5;
    public ListView<ChipView> subjectListView;

    public ProgressIndicator progressIndicator;
    public TextField feeTextField;
    private ListProperty<Long> yearsList = new SimpleListProperty<>(FXCollections.observableArrayList());
    private BooleanProperty canSubmit = new SimpleBooleanProperty(true);
    private BooleanProperty canDelete = new SimpleBooleanProperty(false);
    private BooleanProperty loadingSections = new SimpleBooleanProperty(false);
    private ListProperty<ChipView> chipViews = new SimpleListProperty<>(FXCollections.observableArrayList());

    private StringProperty courseName = new SimpleStringProperty();
    private LongProperty selectedYears = new SimpleLongProperty();
    private LongProperty selectedFee = new SimpleLongProperty();
    private MapProperty<String, List<String>> subjects = new SimpleMapProperty<>(FXCollections.observableHashMap());

    //   This object is used when we display data of already present object
    private ClassItem classItem;


    private AddCourseCallback callback;

    public boolean isLoadingSections() {
        return loadingSections.get();
    }

    public BooleanProperty loadingSectionsProperty(boolean b) {
        loadingSections.set(b);
        return loadingSections;
    }

    public void setCallback(AddCourseCallback callback) {
        this.callback = callback;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        subjectListView.setId("staticListView");
        progressIndicator.visibleProperty().bind(loadingSections);

        feeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                feeTextField.setText(oldValue);
            } else {
                selectedFee.set(Long.parseLong(newValue));
            }
        });
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


    private void processSubjectsFromView() {
        chipViews.forEach(chipView -> {
            String yearName = chipView.getTitle();
            subjects.get().put(yearName, new ArrayList<>());
            chipView.getChips().forEach(subjectChip -> {
                String name = subjectChip.getTitle();
                subjects.get().get(yearName).add(name);
            });
        });
    }

    private void onSubmitAction() {
        if (callback == null) return;

        processSubjectsFromView();


        if (classItem == null) {
            ClassItem classItem = new ClassItem("", courseName.get(), selectedYears.get(), selectedFee.get());
            List<Section> sections = new ArrayList<>();
            subjects.forEach((s, strings) -> {
                sections.add(new Section("", courseName.get(), "", s, FXCollections.observableHashMap(), strings));
            });

            Course course = new Course(
                    classItem,
                    sections
            );

            callback.onCourseSubmit(course);
        } else {
            ClassItem classItem = new ClassItem(this.classItem.getId(), courseName.get(), selectedYears.get(), selectedFee.get());
            List<Section> sections = FXCollections.observableArrayList();
            chipViews.forEach(chipView -> {
                sections.add(chipView.getSection());
            });
            Course course = new Course(classItem, sections);

            callback.onCourseUpdate(course);
        }

    }

    private void checkCanSubmit() {
        canSubmit.set(
                selectedYears.get() <= 0 && courseName.get() != null
        );
    }

    public void setClassItem(ClassItem course) {
        this.classItem = course;
        yearsComboBox.setDisable(true);
        submitButton.setText("Update");
        feeTextField.setText(String.valueOf(course.getFee()));
        canDelete.set(true);
        selectedYears.set(course.getYears());
        courseName.set(course.getName());
        yearsComboBox.getSelectionModel().select(course.getYears());
        courseNameTextField.setText(course.getName());
        chipViews.clear();
        new Thread(() -> Platform.runLater(() -> loadSectionsWithSubjectsForClass(course))).start();
    }


    private void loadSectionsWithSubjectsForClass(ClassItem course) {
        loadingSections.set(true);
        Query classQuery = FirestoreConstants
                .sectionsCollectionReference
                .whereEqualTo("class_id", course.getId());
        try {
            classQuery.get().get().forEach(queryDocumentSnapshot -> {
                Section section = Section.fromJSON(queryDocumentSnapshot.getData());

                ChipView chipView = new ChipView("");
                chipView.setSection(section);
                chipViews.add(chipView);
            });
            loadingSections.set(false);
        } catch (InterruptedException | ExecutionException e) {
            loadingSections.set(false);
            e.printStackTrace();
        }
    }
}

