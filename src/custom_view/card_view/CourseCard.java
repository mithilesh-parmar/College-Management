package custom_view.card_view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class CourseCard extends AnimatingCard implements Initializable {

    private Label titleLabel, subTitleLabel;
    private StringProperty title, subTitle;
    private CardListener cardListener;
    private BorderPane frontView = new BorderPane();
    private BorderPane rearView = new BorderPane();

    public void setCardListener(CardListener cardListener) {
        this.cardListener = cardListener;
    }

    public CourseCard(String title, String subTitle) {

        this.title = new SimpleStringProperty(title);
        this.subTitle = new SimpleStringProperty(subTitle);

        setUpFrontView();
        setUpRearView();
        setFrontView(frontView);
        setRearView(rearView);
    }

    private void setUpRearView() {

        rearView.setCenter(new Label("Rear View"));
    }

    private void setUpFrontView() {
        titleLabel = new Label(title.get());
        subTitleLabel = new Label(subTitle.get());
        titleLabel.textProperty().bind(this.title);
        subTitleLabel.textProperty().bind(this.subTitle);
        frontView.setTop(titleLabel);
        frontView.setBottom(subTitleLabel);

        BorderPane.setMargin(titleLabel, new Insets(4));

        BorderPane.setMargin(subTitleLabel, new Insets(4));
        setBorder(new Border(new BorderStroke(Color.SLATEGREY,
                BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1))));
        setPadding(new Insets(14));
        setId("card");

        setStyle("/styles/dark_metro_style.css");

        setOnMouseClicked(event -> {
            if (cardListener != null) cardListener.onCardClick();
        });
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
