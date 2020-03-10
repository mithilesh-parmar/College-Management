package exams.add_exam;

import com.google.cloud.Timestamp;
import custom_view.loading_combobox.batches.BatchLoadingComboBox;
import custom_view.loading_combobox.class_section_combobox.ClassSectionComboBox;
import custom_view.loading_combobox.class_section_combobox.ClassSectionListener;
import custom_view.loading_combobox.course.ClassLoadingComboBox;
import custom_view.loading_combobox.section.SectionLoadingComboBox;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import model.*;
import utility.DateUtility;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddExamController implements Initializable {

    public ClassSectionComboBox classSectionComboBox;
    public Button deleteButton;
    public DatePicker datePicker;
    private AddExamCallback listener;

    public TextField examNameTextField;
    public BatchLoadingComboBox batchComboBox;
    public Button submitButton;

    public ListView<Exam.SubjectExam> subjectListView;
    public TextField timeTextField;

    private BooleanProperty canSubmit = new SimpleBooleanProperty(false);

    private StringProperty selectedClassName = new SimpleStringProperty();
    private StringProperty examName = new SimpleStringProperty();
    private StringProperty selectedBatch = new SimpleStringProperty();
    private StringProperty selectedTime = new SimpleStringProperty();
    private ObjectProperty<Section> selectedSection = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> selectedDate = new SimpleObjectProperty<>();

    private ListProperty<Exam.SubjectExam> subjectList = new SimpleListProperty<>(FXCollections.observableArrayList());

    private Exam exam;

    public void setExam(Exam exam) {
        this.exam = exam;
        submitButton.setText("Update");

        timeTextField.setText(exam.getTime());
        examNameTextField.setText(exam.getName());
        subjectList.setAll(exam.getSubjects());
        batchComboBox.setValue(exam.getBatch(), true);

        classSectionComboBox.getClassComboBox().setValue(exam.getClassName(), true);
        classSectionComboBox.getSectionComboBox().setValue(exam.getSection(), true);

        if (exam.getDate() != null) {
            selectedDate.set(DateUtility.dateToLocalDate(exam.getDate()));
            datePicker.setValue(DateUtility.dateToLocalDate(exam.getDate()));
        }
        canSubmit.set(true);
        submitButton.visibleProperty().unbind();
        submitButton.setVisible(true);
        deleteButton.setVisible(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        datePicker.valueProperty().addListener((observableValue, localDate, t1) -> {
            if (t1 == null) return;
            selectedDate.set(t1);
            checkReadyToSubmit();
        });
        deleteButton.setVisible(false);
        batchComboBox.getComboBox().setPrefWidth(150);
        batchComboBox.getComboBox().setMinWidth(150);
        batchComboBox.setAlignment(Pos.CENTER_LEFT);
        subjectListView.itemsProperty().bind(subjectList);
        subjectListView.setId("staticListView");

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


        classSectionComboBox.setListener(new ClassSectionListener() {
            @Override
            public void onSectionSelected(Section section) {
                selectedSection.set(section);
                checkReadyToSubmit();
            }

            @Override
            public void onClassSelected(ClassItem classItem) {
                selectedClassName.set(classItem.getName());
                checkReadyToSubmit();
            }
        });
//        classSectionComboBox.getSectionComboBox().setListener(selctedItem -> {
//            selectedSection.set(((Section) selctedItem));
//
//            checkReadyToSubmit();
//        });
//
//        classSectionComboBox.getClassComboBox().setListener(selectedItem -> {
//            selectedClassName.set(((ClassItem) selectedItem).getName());
//
//            checkReadyToSubmit();
//        });

        timeTextField.textProperty().addListener((observableValue, s, t1) -> {
            selectedTime.set(t1);
            checkReadyToSubmit();
        });


        selectedSection.addListener((observableValue, s, t1) -> {
            subjectList.get().clear();

            t1.getSubjects().forEach(subject -> subjectList.get().add(new Exam.SubjectExam(subject, LocalDate.now())));
            checkReadyToSubmit();
        });

        submitButton.setOnAction(actionEvent -> {


            if (listener == null) return;
//            if exam value was updated
            if (exam != null)
                listener.onExamUpdated(exam, new Exam(
                        this.exam.getId(),
                        this.exam.getBatch(),
                        this.exam.getClassName(),
                        examName.get(),
                        this.exam.getSection(),
                        DateUtility.timeStampToReadable(DateUtility.localDateToTimestamp(selectedDate.get())),
                        selectedTime.get(),
                        DateUtility.localDateToTimestamp(selectedDate.get()),
                        subjectList
                ));
//            if new exam is being created
            else listener.onAddExam(new Exam(
                    "",
                    selectedBatch.get(),
                    selectedClassName.get(),
                    examName.get(),
                    selectedSection.get().getSectionName(),
                    DateUtility.timeStampToReadable(DateUtility.localDateToTimestamp(selectedDate.get())),
                    selectedTime.get(),
                    DateUtility.localDateToTimestamp(selectedDate.get()),
                    subjectList
            ));

        });

        deleteButton.setOnAction(actionEvent -> {
            if (listener == null) return;
            if (exam == null) return;
            listener.onExamDelete(exam);
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
                dateSelected = selectedDate.get() != null,
                batchSelected = selectedBatch.get() != null && !selectedBatch.get().isEmpty();

//        System.out.println(
//                "\nSection " + selectedSection.get() + " " + sectionSelected
//                        + "\nClass " + selectedClassName.get() + " " + classNameSelected
//                        + "\nTime " + selectedTime.get() + " " + timeSelected
//                        + "\nExam Name " + examName.get() + " " + examNameSelected
//                        + "\nBatch " + selectedBatch.get() + " " + batchSelected
//        );


        canSubmit.set(
                (sectionSelected && classNameSelected && timeSelected && examNameSelected && batchSelected && dateSelected)
        );

    }


    private static class ExamSubjectCell extends ListCell<Exam.SubjectExam> {

        public interface Callback {
            void onDeleteAction();
        }

        private final Label subjectNameLabel = new Label();
        private final DatePicker datePicker = new DatePicker();
        private final Button cancelButton = new Button();
        private final String GRAPHICS_PATH = "/assets/delete.png";
        private BorderPane borderPane = new BorderPane();


        public ExamSubjectCell(Callback callback) {
            super();

            cancelButton.setDefaultButton(true);
            cancelButton.setOnAction(actionEvent -> {
                callback.onDeleteAction();
            });

            ImageView imageView = new ImageView();
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
            imageView.setSmooth(true);
            imageView.setCache(true);
            imageView.setImage(new Image(GRAPHICS_PATH));
            cancelButton.setText("Delete");
//            cancelButton.setGraphic(imageView);
//            cancelButton.visibleProperty().bind(selectedProperty());


        }


        @Override
        public void updateItem(Exam.SubjectExam obj, boolean empty) {
            super.updateItem(obj, empty);


            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                borderPane.setPadding(new Insets(4));
                datePicker.valueProperty().bindBidirectional(obj.dateProperty());
                subjectNameLabel.setText(obj.getName());
                datePicker.setValue(datePicker.getValue());
                borderPane.setLeft(subjectNameLabel);
                borderPane.setCenter(datePicker);
                borderPane.setRight(cancelButton);
                BorderPane.setMargin(subjectNameLabel, new Insets(4));
                BorderPane.setMargin(cancelButton, new Insets(4));
                BorderPane.setMargin(datePicker, new Insets(4));

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
