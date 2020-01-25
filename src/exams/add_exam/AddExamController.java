package exams.add_exam;

import com.google.cloud.Timestamp;
import custom_view.loading_combobox.batches.BatchLoadingComboBox;
import custom_view.loading_combobox.course.ClassLoadingComboBox;
import custom_view.loading_combobox.section.SectionLoadingComboBox;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.*;
import utility.DateUtility;

import java.net.URL;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddExamController implements Initializable {

    public TextField examNameTextField;
    public BatchLoadingComboBox batchComboBox;
    public ClassLoadingComboBox classNameComboBoc;
    public SectionLoadingComboBox sectionComboBox;
    public DatePicker datePicker;
    public Button submitButton;

    public ListView listView;

    private BooleanProperty canSubmit = new SimpleBooleanProperty(false);
    private StringProperty selectedClassName = new SimpleStringProperty();
    private ObjectProperty<Section> selectedSection = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> selectedDate = new SimpleObjectProperty<>();
    private StringProperty examName = new SimpleStringProperty();
    private StringProperty selectedBatch = new SimpleStringProperty();
    private ListProperty<String> subjectList = new SimpleListProperty<>(FXCollections.observableArrayList());


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        listView.itemsProperty().bind(subjectList);

        listView.setCellFactory(listView1 -> new ExamSubjectCell(() -> {
            subjectList.remove(listView.getSelectionModel().getSelectedItem());
        }));
//        submitButton.visibleProperty().bind(canSubmit);

        batchComboBox.setListener(selctedItem -> {
            selectedBatch.set(String.valueOf(selctedItem));
            checkReadyToSubmit();
//            exam.setBatch(String.valueOf(selctedItem));
        });


        sectionComboBox.setListener(selctedItem -> {
            selectedSection.set(((Section) selctedItem));
//            exam.setSection(((Section) selctedItem).getSectionName());
            checkReadyToSubmit();
        });

        classNameComboBoc.setListener(selectedItem -> {
            selectedClassName.set(((ClassItem) selectedItem).getName());
//            exam.setClassName(((ClassItem) selectedItem).getName());
            checkReadyToSubmit();
        });
        datePicker.valueProperty().addListener((observableValue, localDate, t1) -> {
            selectedDate.set(t1);
//            exam.setDate(t1);
            checkReadyToSubmit();
        });


        selectedClassName.addListener((observableValue, s, t1) -> {
            sectionComboBox.showItemFor(t1);
            checkReadyToSubmit();
        });

        selectedSection.addListener((observableValue, s, t1) -> {
            subjectList.get().setAll(t1.getSubjects());
            checkReadyToSubmit();
        });

        submitButton.setOnAction(actionEvent -> {
            listView.getItems().forEach(o -> System.out.println(o));
        });

    }


    private void checkReadyToSubmit() {

        boolean sectionSelected = selectedSection.get() != null,
                classNameSelected = selectedClassName.get() != null,
                dateSelected = selectedDate.get() != null,
                examNameSelected = examName.get() != null,
                batchSelected = selectedBatch.get() != null;


        canSubmit.set(
                (sectionSelected && classNameSelected && dateSelected && examNameSelected && batchSelected)
        );
        System.out.println(canSubmit);
//        System.out.println(exam.toJSON());
    }


    private static class ExamSubjectCell extends ListCell<String> {

        public interface Callback {
            void onDeleteAction();
        }

        private final Label subjectNameLabel = new Label();
        private ObjectProperty<LocalDate> pickedDate = new SimpleObjectProperty<>();
        private StringProperty readableDate = new SimpleStringProperty(DateUtility.timeStampToReadable(Timestamp.now()));
        private final DatePicker datePicker = new DatePicker();
        private final Button cancelButton = new Button();

        private BorderPane borderPane = new BorderPane();


        public ExamSubjectCell(Callback callback) {
            super();


            pickedDate.addListener((observableValue, localDate, t1) -> readableDate.set(
                    DateUtility.timeStampToReadable(Timestamp.of(DateUtility.localDateToDate(pickedDate.get())))
            ));
            datePicker.valueProperty().addListener((observableValue, localDate, t1) -> pickedDate.set(t1));
            cancelButton.setDefaultButton(true);
            cancelButton.setOnAction(actionEvent -> {
                callback.onDeleteAction();
            });

            cancelButton.visibleProperty().bind(selectedProperty());
        }


        @Override
        public void updateItem(String obj, boolean empty) {
            super.updateItem(obj, empty);


            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                subjectNameLabel.setText(obj);

                borderPane.setLeft(subjectNameLabel);
                borderPane.setCenter(datePicker);
                borderPane.setRight(cancelButton);
                setGraphic(borderPane);
            }
        }

        public Label getSubjectNameLabel() {
            return subjectNameLabel;
        }

        public LocalDate getPickedDate() {
            return pickedDate.get();
        }

        public ObjectProperty<LocalDate> pickedDateProperty() {
            return pickedDate;
        }

        public void setPickedDate(LocalDate pickedDate) {
            this.pickedDate.set(pickedDate);
        }

        public String getReadableDate() {
            return readableDate.get();
        }

        public StringProperty readableDateProperty() {
            return readableDate;
        }

        public void setReadableDate(String readableDate) {
            this.readableDate.set(readableDate);
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
