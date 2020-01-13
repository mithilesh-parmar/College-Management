package custom_view.card_view;

import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class AttendanceCard extends BorderPane
        implements Initializable {

    private Label courseLabel = new Label();
    private Label subjectLabel = new Label();
    private Label yearLabel = new Label();
    private Label dateLabel = new Label();
    private Label unixDateLabel = new Label();


    public AttendanceCard(String course, String subject, String year, String date, String unixDate) {
        courseLabel.setText(course);
        subjectLabel.setText(subject);
        yearLabel.setText(year);
        dateLabel.setText(date);
        unixDateLabel.setText(unixDate);
        loadData(course, subject, year, date, unixDate);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }


    public void setListener(AttendanceCardListener listener) {

        setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) listener.onCardClick();
        });

    }

    private void loadData(String className, String section, String batch, String date, String unixDate) {
        setPadding(new Insets(10));


        setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.DOTTED, new CornerRadii(4), new BorderWidths(1))));
        setLabelText(courseLabel, className);
        setLabelText(subjectLabel, section);
        setLabelText(yearLabel, batch);
        setLabelText(dateLabel, date);
        setLabelText(unixDateLabel, unixDate);




        VBox centerArea = new VBox(2);
        centerArea.setAlignment(Pos.CENTER_LEFT);
        centerArea.getChildren().addAll(courseLabel, subjectLabel, yearLabel, dateLabel, unixDateLabel);


        setRight(centerArea);


        setId("card");

        setStyle("/styles/dark_metro_style.css");


    }


    private void setLabelText(Label label, String text) {

        label.setText(text);
        label.setPadding(new Insets(0, 14, 0, 14));
    }

}
