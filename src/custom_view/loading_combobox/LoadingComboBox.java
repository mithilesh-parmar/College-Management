package custom_view.loading_combobox;

import com.google.cloud.firestore.*;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public abstract class LoadingComboBox extends StackPane {


    private ListProperty<Object> comboBoxObjectListProperty = new SimpleListProperty<>();
    private ListProperty<Object> filteredList = new SimpleListProperty<>();

    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);

    private Object value;

    EventListener<QuerySnapshot> dataListener = (snapshot, e) -> {
        if (e != null) {
            System.err.println("Listen failed: " + e);
            return;
        }
        if (snapshot != null && !snapshot.isEmpty()) {
            System.out.println("data received");
            Platform.runLater(() -> {
                parseData(snapshot.getDocuments());
            });
        } else {
            System.out.print("Current data: null");
        }
    };
    private LoadingComboBoxListener listener;
    private ComboBox<Object> comboBox;

    public void setValue(Object value, boolean disable) {
        this.value = value;
        dataLoading.set(false);
        comboBox.setValue(value);
        comboBox.setDisable(disable);
    }


    public void setListener(LoadingComboBoxListener listener) {
        this.listener = listener;
    }

    protected LoadingComboBox() {

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.visibleProperty().bind(dataLoading);

        comboBox = new ComboBox<>();



        getChildren().addAll(comboBox, progressIndicator);
        getCollectionReference().addSnapshotListener(dataListener);

        comboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, section, t1) -> {
            if (listener != null) listener.onItemSelected(t1);
        });

    }

    public Object getSelectedItem() {
        return comboBox.getSelectionModel().getSelectedItem();
    }

    public void setFilteredList(ObservableList<Object> filteredList) {
        this.filteredList.set(filteredList);
        comboBox.setItems(filteredList);

    }

    public ComboBox<Object> getComboBox() {
        return comboBox;
    }

    public void reset() {
        comboBox.getSelectionModel().clearSelection();
    }

    public void setOriginalList(ObservableList<Object> list) {
        comboBox.setItems(list);
    }

    protected void setEventListener(EventListener<QuerySnapshot> eventListener) {
        getCollectionReference().addSnapshotListener(eventListener);
        System.out.println("Adding event Listener " + listener + " To collection " + getCollectionReference());
    }


    public abstract CollectionReference getCollectionReference();

    public abstract void parseData(List<QueryDocumentSnapshot> documents);

    public ObservableList<Object> getComboBoxObjectListProperty() {
        return comboBoxObjectListProperty.get();
    }

    public ListProperty<Object> comboBoxObjectListPropertyProperty() {
        return comboBoxObjectListProperty;
    }

    public void setComboBoxObjectListProperty(ObservableList<Object> comboBoxObjectListProperty) {
        this.comboBoxObjectListProperty.set(comboBoxObjectListProperty);
    }

    public boolean isDataLoading() {
        return dataLoading.get();
    }

    public BooleanProperty dataLoadingProperty() {
        return dataLoading;
    }

    public void setDataLoading(boolean dataLoading) {
        this.dataLoading.set(dataLoading);
    }

    public LoadingComboBoxListener getListener() {
        return listener;
    }
}
