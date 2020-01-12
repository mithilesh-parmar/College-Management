package custom_view.loading_combobox.course;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import custom_view.loading_combobox.LoadingComboBox;
import javafx.beans.property.ListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Course;
import utility.FirestoreConstants;

import java.util.List;

public class CourseLoadingComboBox extends LoadingComboBox {

    public CourseLoadingComboBox() {

    }


    @Override
    public CollectionReference getCollectionReference() {
        return FirestoreConstants.courseCollectionReference;
    }

    @Override
    public void parseData(List<QueryDocumentSnapshot> documents) {
        System.out.println("Parsing data");
        ObservableList<Object> objectObservableList = FXCollections.observableArrayList();
        ListProperty<Object> objects = comboBoxObjectListPropertyProperty();
        if (objects != null) objects.clear();
        for (QueryDocumentSnapshot document : documents)
            objectObservableList.add(Course.fromJSON(document.getData()));
        if (objects != null) {
            objects.set(objectObservableList);
        }
        dataLoadingProperty().set(false);
    }


}
