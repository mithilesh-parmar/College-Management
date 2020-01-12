package custom_view.loading_combobox.subject;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import custom_view.loading_combobox.LoadingComboBox;
import javafx.beans.property.ListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Subject;
import utility.FirestoreConstants;

import java.util.List;

public class SubjectLoadingComboBox extends LoadingComboBox {

    public SubjectLoadingComboBox() {
    }


    @Override
    public CollectionReference getCollectionReference() {
        return FirestoreConstants.subjectCollectionReference;
    }

    @Override
    public void parseData(List<QueryDocumentSnapshot> documents) {
        System.out.println("Parsing data");
        ObservableList<Object> objectObservableList = FXCollections.observableArrayList();
        ListProperty<Object> objects = comboBoxObjectListPropertyProperty();
        if (objects != null) objects.clear();
        for (QueryDocumentSnapshot document : documents)
            objectObservableList.add(Subject.fromJSON(document.getData()));
        if (objects != null) {
            objects.set(objectObservableList);
        }
        dataLoadingProperty().set(false);
    }
}
