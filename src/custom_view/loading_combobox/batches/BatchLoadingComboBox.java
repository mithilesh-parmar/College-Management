package custom_view.loading_combobox.batches;

import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import custom_view.loading_combobox.LoadingComboBoxListener;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import model.Batch;
import utility.FirestoreConstants;

import java.util.List;

public class BatchLoadingComboBox extends StackPane {

    private ListProperty<Batch> batchList = new SimpleListProperty<>();

    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);


    private LoadingComboBoxListener listener;

    private EventListener<QuerySnapshot> classDataListener = (snapshot, e) -> {
        if (e != null) {
            System.err.println("Listen failed: " + e);
            return;
        }
        if (snapshot != null && !snapshot.isEmpty()) {
            Platform.runLater(() -> {
                parseBatchData(snapshot.getDocuments());
            });
        } else {
            System.out.print("Current data: null");

        }
    };

    public void setListener(LoadingComboBoxListener listener) {
        this.listener = listener;
    }

    public BatchLoadingComboBox() {

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.visibleProperty().bind(dataLoading);
        ComboBox<Batch> batchComboBox = new ComboBox<>();


        getChildren().addAll(batchComboBox, progressIndicator);


        batchComboBox.itemsProperty().bind(batchList);

        loadSections();

        batchComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, section, t1) -> {
            if (listener != null) listener.onItemSelected(t1);
        });

    }


    private void loadSections() {
        dataLoading.set(true);
        if (batchList.size() > 0) return;
        FirestoreConstants.batchCollectionReference.addSnapshotListener(classDataListener);
    }

    private void parseBatchData(List<QueryDocumentSnapshot> data) {
        ObservableList<Batch> sectionObservableList = FXCollections.observableArrayList();
        if (batchList != null) batchList.clear();
        for (QueryDocumentSnapshot document : data)
            sectionObservableList.add(Batch.fromJSON(document.getData()));

        if (batchList != null) {
            batchList.set(sectionObservableList);
        }
        dataLoading.set(false);
    }

}
