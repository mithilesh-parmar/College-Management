package custom_view.card_view;

import custom_view.icon_button.IconButton;
import custom_view.loading_image.LoadingImage;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class AttendanceCard extends BorderPane
        implements Initializable {

    private Label classLabel = new Label();
    private Label sectionLabel = new Label();
    private Label batchLabel = new Label();
    private Label dateLabel = new Label();
    private Label unixDateLabel = new Label();


    public AttendanceCard(String className, String section, String batch, String date, String unixDate) {
        classLabel.setText(className);
        sectionLabel.setText(section);
        batchLabel.setText(batch);
        dateLabel.setText(date);
        unixDateLabel.setText(unixDate);
        loadData(className, section, batch, date, unixDate);
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
        setLabelText(classLabel, className);
        setLabelText(sectionLabel, section);
        setLabelText(batchLabel, batch);
        setLabelText(dateLabel, date);
        setLabelText(unixDateLabel, unixDate);




        VBox centerArea = new VBox(2);
        centerArea.setAlignment(Pos.CENTER_LEFT);
        centerArea.getChildren().addAll(classLabel, sectionLabel, batchLabel, dateLabel, unixDateLabel);


        setRight(centerArea);


        setId("card");

        setStyle("/styles/dark_metro_style.css");


    }


    private void setLabelText(Label label, String text) {

        label.setText(text);
        label.setPadding(new Insets(0, 14, 0, 14));
    }

}
