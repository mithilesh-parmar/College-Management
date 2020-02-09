package custom_view.class_section_accordion;

import com.google.cloud.firestore.Query;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.StackPane;
import model.ClassItem;
import model.Section;
import utility.FirestoreConstants;

import java.util.concurrent.ExecutionException;

public class ClassSectionTitledPane extends TitledPane {

    public interface EventCallback {
        void onClick(ClassItem classItem, Section s);
    }

    private EventCallback callback;
    private ClassItem classItem;
    private ListProperty<Section> sections = new SimpleListProperty<>(FXCollections.observableArrayList());

    private StackPane stackPane;
    private ListView<Section> sectionListView;
    private ProgressIndicator progressIndicator = new ProgressIndicator();
    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);
    private Thread thread = new Thread(this::loadSections);

    public ClassSectionTitledPane(ClassItem classItem) {
        this.classItem = classItem;
        setExpanded(false);

        expandedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                thread.start();
            } else {
                sectionListView.getSelectionModel().clearSelection();
                if (thread.isAlive()) thread.interrupt();
            }
        });
        setText(classItem.getName());
        sectionListView = new ListView<>();
        sectionListView.itemsProperty().bind(sections);
        stackPane = new StackPane();
        progressIndicator.visibleProperty().bind(dataLoading);
        stackPane.getChildren().addAll(sectionListView, progressIndicator);
        setContent(stackPane);

        sectionListView.getSelectionModel().selectedItemProperty().addListener((observableValue, section, t1) -> {
            if (callback == null) return;
            callback.onClick(classItem, t1);
        });

    }

    public void clearSelection() {
        sectionListView.getSelectionModel().clearSelection();
    }

    public void setCallback(EventCallback callback) {
        this.callback = callback;
    }

    private void loadSections() {
        Query query = FirestoreConstants
                .sectionsCollectionReference
                .whereEqualTo("class_id", classItem.getId())
                .whereEqualTo("class_name", classItem.getName());
        try {
            dataLoading.set(true);
            sections.clear();
            query.get().get().forEach(queryDocumentSnapshot -> {
                sections.get().add(Section.fromJSON(queryDocumentSnapshot.getData()));
            });
            dataLoading.set(false);
        } catch (InterruptedException | ExecutionException e) {
            dataLoading.set(false);
            e.printStackTrace();
        }
    }

}
