package custom_view.loading_combobox.class_section_combobox;


import custom_view.loading_combobox.course.ClassLoadingComboBox;
import custom_view.loading_combobox.section.SectionLoadingComboBox;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import model.ClassItem;
import model.Section;


public class ClassSectionComboBox extends HBox {

    public ClassLoadingComboBox classComboBox;
    public SectionLoadingComboBox sectionComboBox;
    public Button editButton;
    private ObjectProperty<ClassItem> selectedClass = new SimpleObjectProperty<>();
    private ObjectProperty<Section> selectedSection = new SimpleObjectProperty<>();
//    private BooleanProperty disableSection = new SimpleBooleanProperty(false);


    private ClassSectionListener listener;

    private BooleanProperty isEditable = new SimpleBooleanProperty(false);


    public ClassSectionComboBox() {
        setSpacing(5);
        setAlignment(Pos.CENTER_LEFT);

        classComboBox = new ClassLoadingComboBox();
        sectionComboBox = new SectionLoadingComboBox();
        editButton = new Button("Edit");

        editButton.setOnAction(actionEvent -> {
            classComboBox.getComboBox().disableProperty().set(false);
        });


        classComboBox.setListener(selctedItem -> {
            selectedClass.set((ClassItem) selctedItem);
        });
        selectedClass.addListener((observableValue, classItem, t1) -> {
            sectionComboBox.showItemFor(t1);
            sectionComboBox.getComboBox().disableProperty().set(false);
            if (listener != null) listener.onClassSelected(t1);
        });
        sectionComboBox.setListener(selctedItem -> {
            selectedSection.set((Section) selctedItem);
            if (listener != null) listener.onSectionSelected((Section) selctedItem);
        });


        getChildren()
                .addAll(
                        new Label("Class: "),
                        classComboBox,
                        new Label("Section: "),
                        sectionComboBox,
                        editButton);

    }


    public void setClass(String className) {
        classComboBox.setValue(className, true);
    }

    public void setSection(String sectionName) {
        sectionComboBox.setValue(sectionName, true);
    }

    public void setListener(ClassSectionListener listener) {
        this.listener = listener;
    }

    public ClassLoadingComboBox getClassComboBox() {
        return classComboBox;
    }

    public void setClassComboBox(ClassLoadingComboBox classComboBox) {
        this.classComboBox = classComboBox;
    }

    public SectionLoadingComboBox getSectionComboBox() {
        return sectionComboBox;
    }

    public void setSectionComboBox(SectionLoadingComboBox sectionComboBox) {
        this.sectionComboBox = sectionComboBox;
    }

    public ClassItem getSelectedClass() {
        return selectedClass.get();
    }

    public ObjectProperty<ClassItem> selectedClassProperty() {
        return selectedClass;
    }

    public void setSelectedClass(ClassItem selectedClass) {
        this.selectedClass.set(selectedClass);
    }

    public Section getSelectedSection() {
        return selectedSection.get();
    }

    public ObjectProperty<Section> selectedSectionProperty() {
        return selectedSection;
    }

    public void setSelectedSection(Section selectedSection) {
        this.selectedSection.set(selectedSection);
    }


}
