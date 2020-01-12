package custom_view.loading_combobox.classes;

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
import model.StudentClass;
import utility.FirestoreConstants;

import java.util.List;


//TODO extract classes from firestore
public class ClassLoadingComboBox extends StackPane {

    private ListProperty<StudentClass> classList = new SimpleListProperty<>();

    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);


    private LoadingComboBoxListener listener;

    private EventListener<QuerySnapshot> classDataListener = (snapshot, e) -> {
        if (e != null) {
            System.err.println("Listen failed: " + e);
            return;
        }
        if (snapshot != null && !snapshot.isEmpty()) {
            Platform.runLater(() -> {
                parseClassData(snapshot.getDocuments());
            });
        } else {
            System.out.print("Current data: null");

        }
    };

    public void setListener(LoadingComboBoxListener listener) {
        this.listener = listener;
    }

    public ClassLoadingComboBox() {

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.visibleProperty().bind(dataLoading);
        ComboBox<StudentClass> studentClassComboBox = new ComboBox<>();


        getChildren().addAll(studentClassComboBox, progressIndicator);


        studentClassComboBox.itemsProperty().bind(classList);

        loadSections();

        studentClassComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, section, t1) -> {
            if (listener != null) listener.onItemSelected(t1);
        });

    }


    private void loadSections() {
        dataLoading.set(true);
        if (classList.size() > 0) return;
        FirestoreConstants.classCollectionReference.addSnapshotListener(classDataListener);
    }

    private void parseClassData(List<QueryDocumentSnapshot> data) {
        ObservableList<StudentClass> sectionObservableList = FXCollections.observableArrayList();
        if (classList != null) classList.clear();
        for (QueryDocumentSnapshot document : data)
            sectionObservableList.add(StudentClass.fromJSON(document.getData()));

        if (classList != null) {
            classList.set(sectionObservableList);
        }
        dataLoading.set(false);
    }

}
