package custom_view.loading_combobox.section;

import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import custom_view.loading_combobox.DataLoadingListener;
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
import model.Section;
import utility.FirestoreConstants;

import java.util.List;

public class SectionLoadingComboBox extends StackPane {


    private ListProperty<Section> sectionsList = new SimpleListProperty<>();

    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);


    private DataLoadingListener listener;

    private EventListener<QuerySnapshot> sectionsDataListener = (snapshot, e) -> {
        if (e != null) {
            System.err.println("Listen failed: " + e);
            return;
        }
        if (snapshot != null && !snapshot.isEmpty()) {
            Platform.runLater(() -> {
                parseSectionsData(snapshot.getDocuments());
            });
        } else {
            System.out.print("Current data: null");

        }
    };


    public SectionLoadingComboBox() {

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.visibleProperty().bind(dataLoading);
        ComboBox<Section> sectionComboBox = new ComboBox<>();


        getChildren().addAll(sectionComboBox, progressIndicator);


        sectionComboBox.itemsProperty().bind(sectionsList);

        loadSections();

        sectionComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, section, t1) -> {
            if (listener != null) listener.onItemSelected(t1);
        });

    }


    private void loadSections() {
        dataLoading.set(true);
        FirestoreConstants.sectionsCollectionReference.addSnapshotListener(sectionsDataListener);
    }

    private void parseSectionsData(List<QueryDocumentSnapshot> data) {
        ObservableList<Section> sectionObservableList = FXCollections.observableArrayList();
        if (sectionsList != null) sectionsList.clear();
        for (QueryDocumentSnapshot document : data) sectionObservableList.add(Section.fromJSON(document.getData()));

        if (sectionsList != null) {
            sectionsList.set(sectionObservableList);
        }
        dataLoading.set(false);
    }
}
