package custom_view.loading_combobox.course;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import custom_view.loading_combobox.LoadingComboBox;
import javafx.beans.property.ListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.ClassItem;
import utility.FirestoreConstants;

import java.util.List;
import java.util.stream.Collectors;

public class ClassLoadingComboBox extends LoadingComboBox {

    public ClassLoadingComboBox() {

    }

    public void showItemFor(String filter) {
        dataLoadingProperty().set(true);
        List<Object> collect = comboBoxObjectListPropertyProperty()
                .get()
                .stream()
                .filter(o -> ((ClassItem) o).getName().toUpperCase().contains(filter.toUpperCase()))
                .collect(Collectors.toList());
        setFilteredList(FXCollections.observableArrayList(collect));
    }

    public ObservableList<Object> getItems(){
        return comboBoxObjectListPropertyProperty().get();
    }

    @Override
    public CollectionReference getCollectionReference() {
        return FirestoreConstants.classCollectionReference;
    }

    @Override
    public void parseData(List<QueryDocumentSnapshot> documents) {
        System.out.println("Parsing data");
        ObservableList<Object> objectObservableList = FXCollections.observableArrayList();
        ListProperty<Object> objects = comboBoxObjectListPropertyProperty();
        if (objects != null) objects.clear();
        for (QueryDocumentSnapshot document : documents)
            objectObservableList.add(ClassItem.fromJSON(document.getData()));
        if (objects != null) {
            objects.set(objectObservableList);
        }
        setOriginalList(objects);

        dataLoadingProperty().set(false);
    }


}
