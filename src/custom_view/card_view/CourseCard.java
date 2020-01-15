package custom_view.card_view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.Course;

import java.net.URL;
import java.util.ResourceBundle;

public class CourseCard extends AnimatingCard {

    private StringProperty title, subTitle;
    private CardListener cardListener;
    private GridPane frontView = new GridPane();
    private BorderPane rearView = new BorderPane();

    public void setCardListener(CardListener cardListener) {
        this.cardListener = cardListener;
    }

    public CourseCard(Course course) {

        this.title = new SimpleStringProperty(course.getName());
        this.subTitle = new SimpleStringProperty(course.getYears().toString());

        initFrontView();
        initRearView();
        setFrontView(frontView);
        setRearView(rearView);
    }

    void initRearView() {

    }

    public void setCourse(Course course) {

        this.title = new SimpleStringProperty(course.getName());
        this.subTitle = new SimpleStringProperty(course.getYears().toString());

        initFrontView();
        initRearView();
        setFrontView(frontView);
        setRearView(rearView);
    }

    void initFrontView() {
        Label titleLabel = new Label(title.get());
        Label subTitleLabel = new Label(subTitle.get());
        titleLabel.textProperty().bind(this.title);
        subTitleLabel.textProperty().bind(this.subTitle);


        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setMinWidth(100);
        columnConstraints.setHalignment(HPos.LEFT);

        frontView.setPadding(new Insets(8));

        frontView.getColumnConstraints()
                .addAll(columnConstraints, columnConstraints);

        frontView.add(new Label("Course: "), 0, 0);
        frontView.add(titleLabel, 1, 0);

        frontView.add(new Label("Years: "), 0, 1);
        frontView.add(subTitleLabel, 1, 1);


        frontView.setHgap(10);
        frontView.setVgap(10);
        frontView.setAlignment(Pos.CENTER_LEFT);


        setBorder(new Border(new BorderStroke(Color.SLATEGREY,
                BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1))));
        setPadding(new Insets(14));
        setId("card");

        setStyle("/styles/dark_metro_style.css");

        setOnMouseClicked(event -> {
            if (cardListener == null) return;
            if (event.getClickCount() == 2)
                cardListener.onCardClick();
        });


    }


}
