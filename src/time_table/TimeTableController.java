package time_table;

import com.google.cloud.firestore.QuerySnapshot;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import listeners.DataChangeListener;
import model.Lecture;
import model.Section;
import model.Student;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import students.detail_view.StudentDetailsController;
import utility.SectionsFirestoreUtility;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TimeTableController implements Initializable, DataChangeListener {


    public TableView<Lecture> timeTableForSection;
    public ListView<Section> sectionsListView;

    public ToggleButton mondayToggleButton;
    public ToggleButton tuesdayToggleButton;
    public ToggleButton wednesdayToggleButton;
    public ToggleButton thursdayToggleButton;
    public ToggleButton saturdayToggleButton;
    public ToggleButton fridayToggleButton;

    public BorderPane mainView;
    public ProgressIndicator progressIndicator;
    public TextField searchTextField;
    public Button addLectureButton;

    private ToggleGroup dayChooser;
    private BooleanProperty loadingData = new SimpleBooleanProperty(true);
    private SectionsFirestoreUtility firestoreUtility = SectionsFirestoreUtility.getInstance();

    private Section previouslySelectedSection;
    private Toggle previouslySelectedDayOfWeek;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setupToggleButtons();
        mainView.getCenter().visibleProperty().bind(loadingData.not());
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> performSearch(oldValue, newValue));
        progressIndicator.visibleProperty().bind(loadingData);

        dayChooser.selectedToggleProperty().addListener(this::changedDayForSection);

        firestoreUtility.setListener(this);
        firestoreUtility.getSections();


        sectionsListView.getSelectionModel().selectedItemProperty().addListener(this::changedSelectedSection);
        sectionsListView.getSelectionModel().selectFirst();


        addLectureButton.setOnAction(actionEvent -> readFile());
    }

    private void readFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(addLectureButton.getScene().getWindow());
        try {
            read(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }

    }

    void read(File file) throws IOException, InvalidFormatException {
        Workbook workbook = new XSSFWorkbook(file);

        // Retrieving the number of sheets in the Workbook
        System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");


        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter dataFormatter = new DataFormatter();
        System.out.println("\n\nIterating over Rows and Columns using for-each loop\n");
        for (Row row : sheet) {
            for (Cell cell : row) {
                String cellValue = dataFormatter.formatCellValue(cell);
                System.out.print(cellValue + "\t");
            }
            System.out.println();
        }
    }


    //    TODO add listener for addition of lecture
    private void loadAddView() {


        FXMLLoader loader;
        loader = new FXMLLoader((getClass().getResource("AddLectureView.fxml")));
        final Stage stage = new Stage();
        Parent parent = null;
        try {


            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Student Details");

            parent = loader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            AddLectureController controller = loader.getController();
            controller.setListener(new LectureListener() {
                @Override
                public void onLectureAdded(Lecture lecture) {
                    close(stage);
                    loadingData.set(true);
                    firestoreUtility.addLecture(
                            lecture,
                            dayChooser.getSelectedToggle().getUserData().toString(),
                            sectionsListView.getSelectionModel().getSelectedItem()
                    );
                }
            });


            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    TODO add delete and edit functionality for lecture

//    TODO after adding a lecture select previous section and dayofweek

    @Override
    public void onDataLoaded(ObservableList data) {
        loadingData.set(false);
        sectionsListView.setItems(firestoreUtility.sections);

        if (previouslySelectedSection != null) {
            System.out.println("Selecting : " + previouslySelectedSection);
            sectionsListView.getSelectionModel().select(previouslySelectedSection);
            dayChooser.selectToggle(previouslySelectedDayOfWeek);

            sectionsListView.getFocusModel().focus(sectionsListView.getItems().indexOf(previouslySelectedSection));

        }

    }

    @Override
    public void onDataChange(QuerySnapshot data) {
        System.out.println(getClass() + " onDataChange: selected section value: " + previouslySelectedSection);
        previouslySelectedSection = sectionsListView.getSelectionModel().getSelectedItem();
        previouslySelectedDayOfWeek = dayChooser.getSelectedToggle();
        System.out.println(getClass() + " onDataChange: Setting selected section to: " + previouslySelectedSection);
        loadingData.set(true);
    }

    private void changedDayForSection(ObservableValue<? extends Toggle> observableValue, Toggle toggle, Toggle t1) {
        if (sectionsListView.getSelectionModel().getSelectedItem().getClassSchedules() != null)
            timeTableForSection.setItems(
                    sectionsListView
                            .getSelectionModel()
                            .getSelectedItem()
                            .getClassSchedules()
                            .get(observableValue.getValue().getUserData().toString())
            );
        else timeTableForSection.setItems(FXCollections.observableArrayList());
    }


    private void setupToggleButtons() {
        mondayToggleButton.setUserData("1");
        tuesdayToggleButton.setUserData("2");
        wednesdayToggleButton.setUserData("3");
        thursdayToggleButton.setUserData("4");
        fridayToggleButton.setUserData("5");
        saturdayToggleButton.setUserData("6");

        dayChooser = new ToggleGroup();
        dayChooser.getToggles().addAll(
                mondayToggleButton,
                tuesdayToggleButton,
                wednesdayToggleButton,
                thursdayToggleButton,
                fridayToggleButton,
                saturdayToggleButton
        );
    }

    @Override
    public void onError(Exception e) {

    }

    /**
     * called for filtering the observable list to show only those  that
     * matches the search text criteria
     *
     * @param oldValue
     * @param newValue
     */
    private void performSearch(String oldValue, String newValue) {
        if (loadingData.get()) return;
        // if pressing backspace then set initial values to list
        if (oldValue != null && (newValue.length() < oldValue.length())) {
            sectionsListView.setItems(firestoreUtility.sections);
        }

        // convert the searched text to uppercase
        String searchtext = newValue.toUpperCase();

        ObservableList<Section> subList = FXCollections.observableArrayList();
        for (Section p : sectionsListView.getItems()) {
            String text = p.getName().toUpperCase() + " " + p.getClassId().toUpperCase();
            // if the search text contains the manufacturer then add it to sublist
            if (text.contains(searchtext)) {
                subList.add(p);
            }

        }
        // set the items to listview that matches
        sectionsListView.setItems(subList);
    }

    public void onSearchTextEntered(KeyEvent keyEvent) {
        if (!sectionsListView.isFocused()
                && keyEvent.getCode() == KeyCode.ENTER)
            sectionsListView.requestFocus();
    }


    private void changedSelectedSection(ObservableValue<? extends Section> observableValue, Section section, Section t1) {


        if (t1 != null && t1.getClassSchedules() != null && t1.getClassSchedules().containsKey("1")) {
            timeTableForSection.setItems(t1.getClassSchedules().get("1"));
            dayChooser.getToggles().get(0).setSelected(true);
        }

    }

}
