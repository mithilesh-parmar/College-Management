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

public class CourseCard extends BorderPane implements Initializable {

    private Label titleLabel, subTitleLabel;
    private StringProperty title, subTitle;
    private CardListener cardListener;

    public void setCardListener(CardListener cardListener) {
        this.cardListener = cardListener;
    }

    public CourseCard(String title, String subTitle) {
        this.title = new SimpleStringProperty(title);
        this.subTitle = new SimpleStringProperty(subTitle);
        titleLabel = new Label(title);
        subTitleLabel = new Label(subTitle);
        titleLabel.textProperty().bind(this.title);
        subTitleLabel.textProperty().bind(this.subTitle);
        setTop(titleLabel);
        setBottom(subTitleLabel);

        BorderPane.setMargin(titleLabel, new Insets(4));

        BorderPane.setMargin(subTitleLabel, new Insets(4));
        setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.DOTTED, new CornerRadii(4), new BorderWidths(1))));
        setPadding(new Insets(14));
        setId("card");

        setStyle("/styles/dark_metro_style.css");

        setOnMouseClicked(event -> {
            if (cardListener != null) cardListener.onCardClick();
        });
    }

    public void setMirrorLayout() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
