package custom_view.loading_combobox.section;

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
import model.ClassItem;
import model.Section;
import utility.FirestoreConstants;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class SectionLoadingComboBox extends LoadingComboBox {

    private ListProperty<Object> sections =
            new SimpleListProperty<>(FXCollections.observableArrayList());

    public void showItemFor(String filter) {
        System.out.println(filter);
        dataLoadingProperty().set(true);
        List<Object> collect = sections
                .get()
                .stream()
                .filter(o -> ((Section) o).getClassName().toUpperCase().contains(filter.toUpperCase()))
                .collect(Collectors.toList());
        setFilteredList(FXCollections.observableArrayList(collect));
        dataLoadingProperty().set(false);
    }

    public ObservableList<Object> getSections() {
        return sections.get();
    }

    public ListProperty<Object> sectionsProperty() {
        return sections;
    }

    public void showItemFor(ClassItem classItem) {
        String filter = classItem.getName();
        System.out.println(filter);
        dataLoadingProperty().set(true);
        List<Object> collect = sections
                .get()
                .stream()
                .filter(o -> ((Section) o).getClassName().toUpperCase().contains(filter.toUpperCase()))
                .collect(Collectors.toList());
        setFilteredList(FXCollections.observableArrayList(collect));
        dataLoadingProperty().set(false);
    }

    public void setSection(String sectionName, boolean disable) {
        dataLoadingProperty().set(true);
        List<Object> collect = sections
                .get()
                .stream()
                .filter(o -> ((Section) o).getSectionName().toUpperCase().contains(sectionName.toUpperCase()))
                .collect(Collectors.toList());
        System.out.println("Found: " + collect);
        if (collect.size() > 0)
            setValue(collect.get(0), disable);
        dataLoadingProperty().set(false);
    }

    public Section getSelectedItem(String filter) {


        System.out.println("Filter: " + filter);
        System.out.println(sections);
        List<Object> collect = sections
                .get()
                .stream()
                .filter(o -> ((Section) o).getSectionName().toUpperCase().contains(filter.toUpperCase()))
                .collect(Collectors.toList());
        System.out.println(collect);

        return (Section) collect.get(0);
    }

    @Override
    public CollectionReference getCollectionReference() {
        return FirestoreConstants.sectionsCollectionReference;
    }

    @Override
    public void parseData(List<QueryDocumentSnapshot> documents) {
        System.out.println("Parsing data");
        ObservableList<Object> objectObservableList = FXCollections.observableArrayList();
        ListProperty<Object> objects = comboBoxObjectListPropertyProperty();
        if (objects != null) objects.clear();
        for (QueryDocumentSnapshot document : documents)
            objectObservableList.add(Section.fromJSON(document.getData()));
        if (objects != null) {
            objects.set(objectObservableList);
            sections.set(objectObservableList);
        }
        setOriginalList(objects);
        dataLoadingProperty().set(false);
    }

    public Collection<Object> getItems() {
        return comboBoxObjectListPropertyProperty().get();
    }
}
