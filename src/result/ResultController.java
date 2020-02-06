package result;

import com.google.cloud.firestore.QuerySnapshot;
import custom_view.loading_combobox.batches.BatchLoadingComboBox;
import custom_view.loading_combobox.course.ClassLoadingComboBox;
import custom_view.loading_combobox.section.SectionLoadingComboBox;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import listeners.DataChangeListener;
import model.*;
import utility.ResultFirestoreUtility;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ResultController implements Initializable, DataChangeListener {

    public TableView<Result> resultTable;
    public BatchLoadingComboBox batchComboBox;
    public SectionLoadingComboBox sectionComboBox;
    public ClassLoadingComboBox classComboBox;
    public Button submitButton;
    public ProgressIndicator progressIndicator;
    public HBox filterView;
    public Button clearButton;

    private ResultFirestoreUtility firestoreUtility = ResultFirestoreUtility.getInstance();
    private StringProperty selectedClassName = new SimpleStringProperty();
    private StringProperty selectedBatch = new SimpleStringProperty();
    private ObjectProperty<Section> selectedSection = new SimpleObjectProperty<>();
    private BooleanProperty canSubmit = new SimpleBooleanProperty(false);
    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);
    private ListProperty<Result> results = new SimpleListProperty<Result>(FXCollections.observableArrayList());


    @Override

    public void initialize(URL url, ResourceBundle resourceBundle) {
        firestoreUtility.getResults();
        submitButton.disableProperty().bind(canSubmit.not());
        clearButton.disableProperty().bind(canSubmit.not());
        resultTable.getSelectionModel().selectedItemProperty().addListener((observableValue, result, t1) -> viewMarksForExam(t1));

        progressIndicator.visibleProperty().bind(dataLoading);
        resultTable.itemsProperty().bind(results);
        submitButton.setDefaultButton(true);
        filterView.visibleProperty().bind(dataLoading.not());
        firestoreUtility.setListener(this);
        batchComboBox.setListener(selctedItem -> {
            selectedBatch.set(String.valueOf(selctedItem));
            checkReadyToSubmit();
        });
        sectionComboBox.setListener(selctedItem -> {
            selectedSection.set(((Section) selctedItem));
            checkReadyToSubmit();
        });
        classComboBox.setListener(selectedItem -> {
            selectedClassName.set(((ClassItem) selectedItem).getName());
            checkReadyToSubmit();
        });
        selectedClassName.addListener((observableValue, s, t1) -> {
            sectionComboBox.showItemFor(t1);
            checkReadyToSubmit();
        });


        submitButton.setOnAction(actionEvent -> loadData());

        clearButton.setOnAction(actionEvent -> clearFilters());

    }

    public ResultController() {
    }

    private void viewMarksForExam(Result result) {

        Stage primaryStage = new Stage();

        primaryStage.initOwner(submitButton.getScene().getWindow());

        ObservableList<SubjectScore> scores = FXCollections.observableArrayList();

        TableView tableView = new TableView();
        tableView.setStyle("/styles/materail_style.css");

        TableColumn<String, String> column1 = new TableColumn<>("Subject");
        column1.setCellValueFactory(new PropertyValueFactory<>("subjectName"));


        TableColumn<String, String> column2 = new TableColumn<>("Theory");
        column2.setCellValueFactory(new PropertyValueFactory<>("theoryMarks"));

        TableColumn<String, String> column3 = new TableColumn<>("Practical");
        column3.setCellValueFactory(new PropertyValueFactory<>("practicalMarks"));

        TableColumn<String, String> column4 = new TableColumn<>("Pass");
        column4.setCellValueFactory(new PropertyValueFactory<>("pass"));


        tableView.getColumns().addAll(column1, column2, column3, column4);


        for (Map.Entry entry : result.getSubjects().entrySet()) {
            HashMap map = (HashMap) entry.getValue();

            scores.add(
                    new SubjectScore(
                            (String) entry.getKey(),
                            (long) map.get("th_mark"),
                            (long) map.get("pr_mark"),
                            (boolean) map.get("pass") ? "Pass" : "Fail"
                    )
            );
        }

        tableView.setItems(scores);

        VBox vbox = new VBox(tableView);

        Scene scene = new Scene(vbox);

        primaryStage.setScene(scene);

        primaryStage.show();

    }

    private void loadData() {
        if (selectedSection.get() == null
                && selectedClassName.get() == null
                && selectedClassName.get().isEmpty()
                && selectedBatch.get().isEmpty()
                && selectedBatch.get() == null
        ) return;
        results.setAll(firestoreUtility
                .results
                .stream()
                .filter(result -> result.getBatch().toUpperCase().contains(selectedBatch.get().toUpperCase()))
                .filter(result -> result.getClassName().toUpperCase().contains(selectedClassName.get().toUpperCase()))
                .filter(result -> result.getSection().toUpperCase().contains(selectedSection.get().getSectionName().toUpperCase()))
                .collect(Collectors.toList()));
    }


    private void checkReadyToSubmit() {
        results.clear();
        boolean sectionSelected = selectedSection.get() != null,
                classNameSelected = selectedClassName.get() != null && !selectedClassName.get().isEmpty(),
                batchSelected = selectedBatch.get() != null && !selectedBatch.get().isEmpty();
        canSubmit.set((sectionSelected && classNameSelected && batchSelected));
    }

    private void clearFilters() {
        classComboBox.reset();
        batchComboBox.reset();
        sectionComboBox.reset();
        setAllData();
    }

    private void setAllData() {
        results.setAll(firestoreUtility.results);
    }

    @Override
    public void onDataLoaded(ObservableList data) {
        dataLoading.set(false);
        results.setAll(firestoreUtility.results);
    }

    @Override
    public void onDataChange(QuerySnapshot data) {
        dataLoading.set(true);
    }

    @Override
    public void onError(Exception e) {
        dataLoading.set(false);
    }

    public class SubjectScore {
        private String subjectName, pass;
        private long theoryMarks, practicalMarks;

        public SubjectScore(String subjectName, long theoryMarks, long practicalMarks, String pass) {
            this.subjectName = subjectName;
            this.theoryMarks = theoryMarks;
            this.practicalMarks = practicalMarks;
            this.pass = pass;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }


        public Long getTheoryMarks() {
            return theoryMarks;
        }

        public void setTheoryMarks(Long theoryMarks) {
            this.theoryMarks = theoryMarks;
        }

        public Long getPracticalMarks() {
            return practicalMarks;
        }

        public void setPracticalMarks(Long practicalMarks) {
            this.practicalMarks = practicalMarks;
        }

        public String getPass() {
            return pass;
        }

        public void setPass(String pass) {
            this.pass = pass;
        }
    }

}
