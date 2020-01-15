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

public class CourseCard extends AnimatingCard implements Initializable {

    private Label titleLabel, subTitleLabel;
    private StringProperty title, subTitle;
    private CardListener cardListener;
    private GridPane frontView = new GridPane();
    private BorderPane rearView = new BorderPane();
    private Course course;

    public void setCardListener(CardListener cardListener) {
        this.cardListener = cardListener;
    }

    public CourseCard(Course course) {

        this.title = new SimpleStringProperty(course.getName());
        this.subTitle = new SimpleStringProperty(course.getYears().toString());

        setUpFrontView();
        setUpRearView();
        setFrontView(frontView);
        setRearView(rearView);
    }

    private void setUpRearView() {

    }

    public void setCourse(Course course) {
        this.course = course;

        this.title = new SimpleStringProperty(course.getName());
        this.subTitle = new SimpleStringProperty(course.getYears().toString());

        setUpFrontView();
        setUpRearView();
        setFrontView(frontView);
        setRearView(rearView);
    }

    private void setUpFrontView() {
        titleLabel = new Label(title.get());
        subTitleLabel = new Label(subTitle.get());
        titleLabel.textProperty().bind(this.title);
        subTitleLabel.textProperty().bind(this.subTitle);


        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setMinWidth(100);
//        columnConstraints.setPrefWidth(200);
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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
