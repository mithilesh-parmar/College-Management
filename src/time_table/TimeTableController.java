package time_table;

import com.google.cloud.firestore.QuerySnapshot;
import custom_view.dialog_helper.CustomDialog;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import listeners.DataChangeListener;
import model.Lecture;
import model.Section;
import model.StudentDocument;
import utility.ScreenUtility;
import utility.SectionsFirestoreUtility;

import java.io.IOException;
import java.net.URL;
import java.util.*;


public class TimeTableController implements Initializable, DataChangeListener {


    //    public ListView<Section> sectionsListView;
    public ProgressIndicator progressIndicator;
    public TextField searchTextField;

    public TableView<Lecture> mondayTableView;
    public TableView<Lecture> tuesdayTableView;
    public TableView<Lecture> wednesdayTableView;
    public TableView<Lecture> thursdayTableView;
    public TableView<Lecture> fridayTableView;
    public TableView<Lecture> saturdayTableView;
    public TableView<Lecture> sundayTableView;

    public Button addButton;
    public ListView scheduleView;
    public TreeView sectionsTreeView;
    public Label selectedSectionLabel;


    private BooleanProperty loadingData = new SimpleBooleanProperty(true);
    private SectionsFirestoreUtility firestoreUtility = SectionsFirestoreUtility.getInstance();


    private ObjectProperty<Section> selectedSection = new SimpleObjectProperty<>();
    private BooleanProperty canViewSchedule = new SimpleBooleanProperty(false);

    private ListProperty<Lecture> mondaySchedule = new SimpleListProperty<>(FXCollections.observableArrayList()),
            tuesdaySchedule = new SimpleListProperty<>(FXCollections.observableArrayList()),
            wednesdaySchedule = new SimpleListProperty<>(FXCollections.observableArrayList()),
            thursdaySchedule = new SimpleListProperty<>(FXCollections.observableArrayList()),
            fridaySchedule = new SimpleListProperty<>(FXCollections.observableArrayList()),
            saturdaySchedule = new SimpleListProperty<>(FXCollections.observableArrayList()),
            sundaySchedule = new SimpleListProperty<>(FXCollections.observableArrayList());


    private ContextMenu cellContextMenu = new ContextMenu();
    private MenuItem deleteMenuButton = new MenuItem("Delete");
    private MenuItem editMenuButton = new MenuItem("Edit");
    private MenuItem cancelMenuButton = new MenuItem("Cancel");

    private TreeItem<Section> rootNode = new TreeItem<>();


    public TimeTableController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        scheduleView.setId("staticListView");

        sectionsTreeView.setRoot(rootNode);


        selectedSection.addListener((observableValue, section, t1) -> {
            if (t1 == null) return;
            canViewSchedule.set(true);
            selectedSectionLabel.setText(t1.getClassName() + " - " + t1.getSectionName());
        });

        addButton.setPadding(new Insets(8));

        cellContextMenu.getItems().addAll(deleteMenuButton, editMenuButton, cancelMenuButton);

//        sectionsListView.setCellFactory(sectionListView -> new SectionListCell());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        scrollPane.setPannable(true);
        scrollPane.setOnMouseClicked(mouseEvent -> {
            if (cellContextMenu.isShowing()) cellContextMenu.hide();
        });

//        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> performSearch(oldValue, newValue));
        progressIndicator.visibleProperty().bind(loadingData);

        addButton.visibleProperty().bind(canViewSchedule);


        mondayTableView.itemsProperty().bind(mondaySchedule);
        tuesdayTableView.itemsProperty().bind(tuesdaySchedule);
        wednesdayTableView.itemsProperty().bind(wednesdaySchedule);
        thursdayTableView.itemsProperty().bind(thursdaySchedule);
        fridayTableView.itemsProperty().bind(fridaySchedule);
        saturdayTableView.itemsProperty().bind(saturdaySchedule);
        sundayTableView.itemsProperty().bind(sundaySchedule);


        firestoreUtility.setListener(this);
        firestoreUtility.getSections();

        addButton.setOnAction(actionEvent -> loadAddView(null));


//        sectionsListView.getSelectionModel().selectedItemProperty().addListener((observableValue, section, t1) -> {
//            if (t1 == null) return;
//            selectedSection.set(t1);
//            mondaySchedule.setValue(t1.getClassSchedules().get("1"));
//            tuesdaySchedule.setValue(t1.getClassSchedules().get("2"));
//            wednesdaySchedule.setValue(t1.getClassSchedules().get("3"));
//            thursdaySchedule.setValue(t1.getClassSchedules().get("4"));
//            fridaySchedule.setValue(t1.getClassSchedules().get("5"));
//            saturdaySchedule.setValue(t1.getClassSchedules().get("6"));
//            sundaySchedule.setValue(t1.getClassSchedules().get("7"));
//
//        });

        scheduleView.visibleProperty().bind(canViewSchedule);

        canViewSchedule.addListener((observableValue, aBoolean, t1) -> {
            if (t1 == null) return;
            if (t1) scrollPane.setContent(scheduleView);
        });

        cellContextMenu.setHideOnEscape(true);
        cellContextMenu.setAutoHide(true);

        sectionsTreeView.getSelectionModel().selectedItemProperty().addListener((observableValue, o, t1) -> {
            if (t1 == null) return;
//            if (!((TreeItem) t1).isLeaf()) return;
            TreeItem treeItem = (TreeItem) t1;
            Section section = (Section) treeItem.getValue();
            selectedSection.set(section);
            addClassSchedulesToTables(section);
        });
    }

    private void addClassSchedulesToTables(Section section) {
        mondaySchedule.setValue(section.getClassSchedules().get("1"));
        tuesdaySchedule.setValue(section.getClassSchedules().get("2"));
        wednesdaySchedule.setValue(section.getClassSchedules().get("3"));
        thursdaySchedule.setValue(section.getClassSchedules().get("4"));
        fridaySchedule.setValue(section.getClassSchedules().get("5"));
        saturdaySchedule.setValue(section.getClassSchedules().get("6"));
        sundaySchedule.setValue(section.getClassSchedules().get("7"));
    }

    public void onTableCellClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            Lecture selectedLecture = (Lecture) ((TableView) mouseEvent.getSource())
                    .getSelectionModel()
                    .getSelectedItem();

            ButtonType editButton = new ButtonType("Edit");
            ButtonType deleteButton = new ButtonType("Delete");

            Optional result = CustomDialog.showDialogWithTwoButtons("Choose Action",
                    "Select Action For \nName: " + selectedLecture.getName() +
                            "\nStart Time: " + selectedLecture.getStartTime() + "\nEnd Time: " +
                            selectedLecture.getEndTime() + " ?",
                    editButton, deleteButton);

            result.ifPresent(o -> {
                if (o == editButton) {
                    loadAddView(selectedLecture);
                } else if (o == deleteButton) {
                    deleteLecture(selectedLecture, selectedSection.get());
                }
            });

        }
    }

    //    TODO add buttons to table if asked for
    private void addButtonToTable() {
        TableColumn<Lecture, Void> colBtn = new TableColumn("Actions");

        Callback<TableColumn<Lecture, Void>, TableCell<Lecture, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Lecture, Void> call(final TableColumn<Lecture, Void> param) {
                final TableCell<Lecture, Void> cell = new TableCell<>() {

                    private final HBox hBox = new HBox(15);

                    private final Button downloadButton = new Button("Edit");

                    {
                        downloadButton.setOnAction((ActionEvent event) -> {
                            Lecture data = getTableView().getItems().get(getIndex());
                            System.out.println(data);

                        });
                    }

                    private final Button deleteButton = new Button("Delete");

                    {
                        deleteButton.setOnAction((ActionEvent event) -> {
                            Lecture data = getTableView().getItems().get(getIndex());
                            System.out.println(data);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            hBox.getChildren().setAll(downloadButton, deleteButton);
                            setGraphic(hBox);
                        }
                    }
                };

                return cell;
            }
        };

        colBtn.setCellFactory(cellFactory);

        mondayTableView.getColumns().add(colBtn);
        tuesdayTableView.getColumns().add(colBtn);
        wednesdayTableView.getColumns().add(colBtn);
        thursdayTableView.getColumns().add(colBtn);
        fridayTableView.getColumns().add(colBtn);
        saturdayTableView.getColumns().add(colBtn);
        sundayTableView.getColumns().add(colBtn);

    }

    private void deleteLecture(Lecture lecture, Section section) {
        if (showConfirmationAlert(lecture)) {
            loadingData.set(true);
            firestoreUtility.deleteLecture(lecture, section);
        }
    }

    private boolean showConfirmationAlert(Lecture lecture) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Delete Lecture");
        alert.setHeaderText("Are you sure that you want to delete\nName: " + lecture.getName() + "\nwith Start Time: " + lecture.getStartTime() + "\nEnd Time: " + lecture.getEndTime() + " ?");

        alert.showAndWait();

        return alert.getResult() == ButtonType.OK;
    }

    private void loadAddView(Lecture lecture) {


        FXMLLoader loader;
        loader = new FXMLLoader((getClass().getResource("AddLectureView.fxml")));
        final Stage stage = new Stage();
        stage.setHeight(ScreenUtility.getScreenHeight() * 0.70);
        stage.setWidth(ScreenUtility.getScreenThreeFourthWidth());
        Parent parent = null;
        try {


            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Lecture Details");

            parent = loader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            AddLectureController controller = loader.getController();
            controller.setSection(selectedSection.get());
            if (lecture != null) controller.setLecture(lecture);
            controller.setListener(new LectureListener() {
                @Override
                public void onLectureAdded(Lecture lecture) {
                    close(stage);
                    loadingData.set(true);
                    firestoreUtility.addLecture(lecture, selectedSection.get());
                }

                @Override
                public void onLectureUpdated(Lecture updatedLecture) {
                    close(stage);
                    loadingData.set(true);
                    firestoreUtility.updateLecture(lecture, updatedLecture, selectedSection.get());
                }
            });


            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onDataLoaded(ObservableList data) {
//        sectionsListView.setItems(firestoreUtility.sections);
//        sectionsListView.getSelectionModel().selectFirst();
//        selectedSection.set(sectionsListView.getItems().get(0));

        loadingData.set(false);

        processSections(firestoreUtility.sections);
        sectionsTreeView.getSelectionModel().selectFirst();
        selectedSection.set((Section) sectionsTreeView.getRoot().getValue());
        canViewSchedule.set(false);
    }

    private void processSections(ObservableList<Section> sections) {
        rootNode.setExpanded(true);

        rootNode.getChildren().clear();
        for (Section section : sections) {
            TreeItem<Section> leaf = new TreeItem<Section>(section);

            boolean found = false;
            for (TreeItem<Section> branch : rootNode.getChildren()) {
                if (branch.getValue().getClassID().matches(section.getClassID())) {
                    branch.getChildren().add(leaf);
                    found = true;
                    break;
                }
            }
            if (!found) {
                TreeItem<Section> branch = new TreeItem<>();
                branch.setValue(section);
                branch.setGraphic(new Label(section.getClassName()));
                rootNode.getChildren().add(branch);
                branch.getChildren().add(leaf);
            }
        }

        sectionsTreeView.setRoot(rootNode);


    }

    @Override
    public void onDataChange(QuerySnapshot data) {
        loadingData.set(true);
    }


    @Override
    public void onError(Exception e) {
        e.printStackTrace();
        loadingData.set(false);
    }

    /**
     * called for filtering the observable list to show only those  that
     * matches the search text criteria
     * <p>
     * //     * @param oldValue
     * //     * @param newValue
     */
//    private void performSearch(String oldValue, String newValue) {
//        if (loadingData.get()) return;
//        // if pressing backspace then set initial values to list
//        if (oldValue != null && (newValue.length() < oldValue.length())) {
////            sectionsListView.setItems(firestoreUtility.sections);
//        }
//
//        // convert the searched text to uppercase
//        String searchtext = newValue.toUpperCase();
//
//        ObservableList<Section> subList = FXCollections.observableArrayList();
//        for (Section p : sectionsListView.getItems()) {
//            String text = p.getSectionName().toUpperCase() + " " + p.getClassName();
//            // if the search text contains the manufacturer then add it to sublist
//            if (text.contains(searchtext)) {
//                subList.add(p);
//            }
//
//        }
//        // set the items to listview that matches
//        sectionsListView.setItems(subList);
//    }
    public void onSearchTextEntered(KeyEvent keyEvent) {
//        if (!sectionsListView.isFocused()
//                && keyEvent.getCode() == KeyCode.ENTER)
//            sectionsListView.requestFocus();
    }


//    private static class SectionListCell extends ListCell<Section> {
//
//        private final Label classNameLabel = new Label();
//        private final Label sectionNameLabel = new Label();
//        private VBox vBox = new VBox(5);
//
//
//        @Override
//        public void updateItem(Section obj, boolean empty) {
//            super.updateItem(obj, empty);
//            if (empty) {
//                setText(null);
//                setGraphic(null);
//            } else {
//                classNameLabel.setText("Course: " + obj.getClassName());
//
//                sectionNameLabel.setText("Year: " + obj.getSectionName());
//                vBox.getChildren().setAll(classNameLabel, sectionNameLabel);
//                setGraphic(vBox);
//            }
//        }
//    }
}
