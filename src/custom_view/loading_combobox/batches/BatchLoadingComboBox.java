package custom_view.loading_combobox.batches;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import custom_view.loading_combobox.LoadingComboBox;
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
import model.ClassItem;
import utility.FirestoreConstants;

import java.util.List;

public class BatchLoadingComboBox extends LoadingComboBox {


    @Override
    public CollectionReference getCollectionReference() {
        return FirestoreConstants.batchCollectionReference;
    }



    @Override
    public void parseData(List<QueryDocumentSnapshot> documents) {
        System.out.println("Parsing data");
        ObservableList<Object> objectObservableList = FXCollections.observableArrayList();
        ListProperty<Object> objects = comboBoxObjectListPropertyProperty();
        if (objects != null) objects.clear();
        for (QueryDocumentSnapshot document : documents)
            objectObservableList.add(Batch.fromJSON(document.getData()));
        if (objects != null) {
            objects.set(objectObservableList);
        }
        setOriginalList(objects);
        dataLoadingProperty().set(false);
    }
}
