package exams;

import com.google.cloud.firestore.QuerySnapshot;
import custom_view.SearchTextFieldController;
import custom_view.card_view.ExamCard;
import custom_view.card_view.ExamCardListener;
import exams.add_exam.AddExamCallback;
import exams.add_exam.AddExamController;
import javafx.geometry.Insets;
import result.ResultController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import listeners.DataChangeListener;
import model.Exam;
import utility.ExamFirestoreUtility;
import utility.ResultFirestoreUtility;
import utility.ScreenUtility;
import utility.SearchCallback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ExamController implements Initializable, DataChangeListener, SearchCallback, ExamCardListener {

    public ScrollPane scroll;
    public FlowPane examFlowPane;
    public SearchTextFieldController searchTextField;
    public ProgressIndicator progressIndicator;
    public Button addButton;
    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);
    private ExamFirestoreUtility firestoreUtility = ExamFirestoreUtility.getInstance();
    private ResultFirestoreUtility resultFirestoreUtility = ResultFirestoreUtility.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        examFlowPane.setHgap(10);
        examFlowPane.setVgap(10);
        examFlowPane.setAlignment(Pos.TOP_LEFT);
        examFlowPane.setPadding(new Insets(14));

        scroll.setContent(examFlowPane);
        scroll.viewportBoundsProperty().addListener((ov, oldBounds, bounds) -> {
            examFlowPane.setPrefWidth(bounds.getWidth());
            examFlowPane.setPrefHeight(bounds.getHeight());
        });

        addButton.setOnAction(actionEvent -> loadEditView(null));
        examFlowPane.getChildren().addAll(firestoreUtility.examCards);


        progressIndicator.visibleProperty().bind(dataLoading);
        firestoreUtility.setListener(this);
        firestoreUtility.setExamCardListener(this);
        firestoreUtility.getExams();
        searchTextField.setCallback(this);
    }

    private void loadEditView(Exam exam) {
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/exams/add_exam/AddExamView.fxml"));
        final Stage stage = new Stage();
        stage.setHeight(ScreenUtility.getScreenHeight() * 0.70);
        stage.setWidth(ScreenUtility.getScreenHalfWidth());
        Parent parent = null;
        try {
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Exam Details");

            parent = loader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);

            AddExamController controller = loader.getController();

            if (exam != null)
                controller.setExam(exam);
            controller.setListener(new AddExamCallback() {
                @Override
                public void onAddExam(Exam exam) {
                    close(stage);
                    dataLoading.set(true);
                    firestoreUtility.addExam(exam);
                }
            });


            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDataLoaded(ObservableList data) {
        dataLoading.set(false);
        examFlowPane.getChildren().setAll(firestoreUtility.examCards);
    }

    @Override
    public void onDataChange(QuerySnapshot data) {
        dataLoading.set(true);
    }

    @Override
    public void onError(Exception e) {
        dataLoading.set(false);
    }


    @Override
    public void onClick(Exam exam) {

        loadEditView(exam);

    }

    @Override
    public void onResultClick(Exam exam) {
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/result/ResultView.fxml"));
        final Stage stage = new Stage();
        Parent parent = null;
        try {
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Results");

            parent = loader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);

            ResultController controller = loader.getController();

//            controller.setExam(exam);

            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void performSearch(String oldValue, String newValue) {

        if (dataLoading.get()) return;
        // if pressing backspace then set initial values to list
        if (oldValue != null && (newValue.length() < oldValue.length())) {

            examFlowPane.getChildren().setAll(firestoreUtility.examCards);
        }

        // convert the searched text to uppercase
        String searchtext = newValue.toUpperCase();

        ObservableList<ExamCard> subList = FXCollections.observableArrayList();
        for (Exam p : firestoreUtility.exams) {
            String text =
                    p.getName().toUpperCase() + " "
                            + p.getClassName().toUpperCase() + " "
                            + p.getTime() + " " + p.getBatch() + " "
                            + p.getDateReadable() + " "
                            + p.getSection();
            // if the search text contains the manufacturer then add it to sublist
            if (text.contains(searchtext)) {
                subList.add(firestoreUtility.examCardMapProperty.get(p));
            }

        }
        examFlowPane.getChildren().setAll(subList);
    }

}

