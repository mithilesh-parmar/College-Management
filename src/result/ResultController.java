package result;

import com.google.cloud.firestore.QuerySnapshot;
import custom_view.loading_combobox.batches.BatchLoadingComboBox;
import custom_view.loading_combobox.course.ClassLoadingComboBox;
import custom_view.loading_combobox.section.SectionLoadingComboBox;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import listeners.DataChangeListener;
import model.ClassItem;
import model.Result;
import model.Section;
import utility.ResultFirestoreUtility;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
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
        loadData();
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


        submitButton.setOnAction(actionEvent -> {
            loadData();

        });

        clearButton.setOnAction(actionEvent -> {
            clearFilters();
        });

    }

    private void loadData() {
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
}
