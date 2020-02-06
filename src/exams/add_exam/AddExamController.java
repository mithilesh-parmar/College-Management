package exams.add_exam;

import com.google.cloud.Timestamp;
import custom_view.loading_combobox.batches.BatchLoadingComboBox;
import custom_view.loading_combobox.course.ClassLoadingComboBox;
import custom_view.loading_combobox.section.SectionLoadingComboBox;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import model.*;
import utility.DateUtility;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddExamController implements Initializable {

    private AddExamCallback listener;

    public TextField examNameTextField;
    public BatchLoadingComboBox batchComboBox;
    public ClassLoadingComboBox classNameComboBoc;
    public SectionLoadingComboBox sectionComboBox;
    public Button submitButton;

    public ListView<Exam.SubjectExam> subjectListView;
    public TextField timeTextField;

    private BooleanProperty canSubmit = new SimpleBooleanProperty(false);

    private StringProperty selectedClassName = new SimpleStringProperty();
    private StringProperty examName = new SimpleStringProperty();
    private StringProperty selectedBatch = new SimpleStringProperty();
    private StringProperty selectedTime = new SimpleStringProperty();
    private ObjectProperty<Section> selectedSection = new SimpleObjectProperty<>();

    private ListProperty<Exam.SubjectExam> subjectList = new SimpleListProperty<>(FXCollections.observableArrayList());

    private Exam exam;

    public void setExam(Exam exam) {
        this.exam = exam;
        submitButton.setText("Delete");

        timeTextField.setText(exam.getTime());
        examNameTextField.setText(exam.getName());
        subjectList.setAll(exam.getSubjects());
        batchComboBox.setValue(exam.getBatch(), true);
        classNameComboBoc.setValue(exam.getClassName(), true);
        sectionComboBox.setValue(exam.getSection(), true);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        subjectListView.itemsProperty().bind(subjectList);

        submitButton.visibleProperty().bind(canSubmit);
        subjectListView.setCellFactory(listView1 -> new ExamSubjectCell(() -> {
            subjectList.remove(subjectListView.getSelectionModel().getSelectedItem());
        }));

        examNameTextField.textProperty().addListener((observableValue, s, t1) -> {
            examName.set(t1);
            checkReadyToSubmit();
        });

        batchComboBox.setListener(selctedItem -> {
            selectedBatch.set(String.valueOf(selctedItem));
            checkReadyToSubmit();

        });


        sectionComboBox.setListener(selctedItem -> {
            selectedSection.set(((Section) selctedItem));

            checkReadyToSubmit();
        });

        classNameComboBoc.setListener(selectedItem -> {
            selectedClassName.set(((ClassItem) selectedItem).getName());

            checkReadyToSubmit();
        });

        timeTextField.textProperty().addListener((observableValue, s, t1) -> {
            selectedTime.set(t1);
            checkReadyToSubmit();
        });


        selectedClassName.addListener((observableValue, s, t1) -> {
            sectionComboBox.showItemFor(t1);
            checkReadyToSubmit();
        });

        selectedSection.addListener((observableValue, s, t1) -> {
            subjectList.get().clear();

            t1.getSubjects().forEach(subject -> {
                subjectList.get().add(new Exam.SubjectExam(subject, LocalDate.now()));
            });
            checkReadyToSubmit();
        });

        submitButton.setOnAction(actionEvent -> {


            if (listener == null) return;
            if (exam != null)
                listener.onAddExam(new Exam(
                        this.exam.getId(),
                        this.exam.getBatch(),
                        this.exam.getClassName(),
                        this.exam.getName(),
                        selectedSection.get().getSectionName(),
                        exam.getDateReadable(),
                        selectedTime.get(),
                        exam.getDate(),
                        subjectList

                ));
            else listener.onAddExam(new Exam(
                    "",
                    selectedBatch.get(),
                    selectedClassName.get(),
                    examName.get(),
                    selectedSection.get().getSectionName(),
                    "",
                    selectedTime.get(),
                    null,
                    subjectList
            ));

        });

    }

    public void setListener(AddExamCallback listener) {
        this.listener = listener;
    }

    private void checkReadyToSubmit() {

        boolean sectionSelected = selectedSection.get() != null,
                classNameSelected = selectedClassName.get() != null && !selectedClassName.get().isEmpty(),
                timeSelected = selectedTime.get() != null && !selectedTime.get().isEmpty(),
                examNameSelected = examName.get() != null && !examName.get().isEmpty(),
                batchSelected = selectedBatch.get() != null && !selectedBatch.get().isEmpty();

        System.out.println(
                "\nSection " + selectedSection.get() + " " + sectionSelected
                        + "\nClass " + selectedClassName.get() + " " + classNameSelected
                        + "\nTime " + selectedTime.get() + " " + timeSelected
                        + "\nExam Name " + examName.get() + " " + examNameSelected
                        + "\nBatch " + selectedBatch.get() + " " + batchSelected
        );


        canSubmit.set(
                (sectionSelected && classNameSelected && timeSelected && examNameSelected && batchSelected)
        );

    }


    private static class ExamSubjectCell extends ListCell<Exam.SubjectExam> {

        public interface Callback {
            void onDeleteAction();
        }

        private final Label subjectNameLabel = new Label();
        private final DatePicker datePicker = new DatePicker();
        private final Button cancelButton = new Button();

        private BorderPane borderPane = new BorderPane();


        public ExamSubjectCell(Callback callback) {
            super();

            cancelButton.setDefaultButton(true);
            cancelButton.setOnAction(actionEvent -> {
                callback.onDeleteAction();
            });

            cancelButton.visibleProperty().bind(selectedProperty());


        }


        @Override
        public void updateItem(Exam.SubjectExam obj, boolean empty) {
            super.updateItem(obj, empty);


            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                datePicker.valueProperty().bindBidirectional(obj.dateProperty());
                subjectNameLabel.setText(obj.getName());
                datePicker.setValue(datePicker.getValue());
                borderPane.setLeft(subjectNameLabel);
                borderPane.setCenter(datePicker);
                borderPane.setRight(cancelButton);
                setGraphic(borderPane);
            }
        }

        public Label getSubjectNameLabel() {
            return subjectNameLabel;
        }


        public DatePicker getDatePicker() {
            return datePicker;
        }

        public Button getCancelButton() {
            return cancelButton;
        }

        public BorderPane getBorderPane() {
            return borderPane;
        }

        public void setBorderPane(BorderPane borderPane) {
            this.borderPane = borderPane;
        }
    }


}
