package custom_view.card_view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.Exam;
import utility.DateUtility;

public class ExamCard extends AnimatingCard {

    private StringProperty title, subTitle, date, time;

    private GridPane frontView, rearView;

    private ExamCardListener listener;
    private Exam exam;


    public ExamCard(Exam exam) {
        this.exam = exam;


        this.title = new SimpleStringProperty(exam.getName());
        this.subTitle = new SimpleStringProperty(exam.getClassName());

        this.date = new SimpleStringProperty(exam.getDate() == null ? "Not Specified" : DateUtility.timeStampToReadable(exam.getDate()));

        this.time = new SimpleStringProperty(exam.getTime());
        frontView = new GridPane();
        rearView = new GridPane();

        initFrontView();
        initRearView();
        setFrontView(frontView);
        setRearView(rearView);
        setShouldAnimate(true);

    }

    public ExamCardListener getListener() {
        return listener;
    }

    public void setListener(ExamCardListener listener) {
        this.listener = listener;
    }

    @Override
    public void initFrontView() {
        Label examNameLabel = new Label(title.get());
        Label classNameLabel = new Label(subTitle.get());


        examNameLabel.textProperty().bind(this.title);
        classNameLabel.textProperty().bind(this.subTitle);


        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setMinWidth(50);
        columnConstraints.setHalignment(HPos.LEFT);

        frontView.setPadding(new Insets(4));

        frontView.getColumnConstraints()
                .addAll(columnConstraints, columnConstraints);

        frontView.add(new Label("Name: "), 0, 0);
        frontView.add(examNameLabel, 1, 0);

        frontView.add(new Label("Class: "), 0, 1);
        frontView.add(classNameLabel, 1, 1);


        frontView.setHgap(4);
        frontView.setVgap(4);
        frontView.setAlignment(Pos.CENTER_LEFT);


        setBorder(new Border(new BorderStroke(Color.SLATEGREY,
                BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1))));
        setPadding(new Insets(14));
        setId("card");

        setStyle("/styles/dark_metro_style.css");


    }

    @Override
    void initRearView() {
        Label dateLabel = new Label(date.get());
        Label timeLabel = new Label(time.get());


        Button editButton = new Button("Edit");
        editButton.setOnAction(actionEvent -> listener.onClick(exam));

        Button viewResultButton = new Button("View Result");
        viewResultButton.setDefaultButton(true);
        viewResultButton.setOnAction(actionEvent -> listener.onResultClick(exam));

        dateLabel.textProperty().bind(this.date);
        timeLabel.textProperty().bind(this.time);


        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setMinWidth(100);
        columnConstraints.setHalignment(HPos.LEFT);

        rearView.setPadding(new Insets(4));

        rearView.getColumnConstraints()
                .addAll(columnConstraints, columnConstraints);


        rearView.add(new Label("Date: "), 0, 0);
        rearView.add(dateLabel, 1, 0);

        rearView.add(new Label("Time: "), 0, 1);
        rearView.add(timeLabel, 1, 1);

//        rearView.add(viewResultButton, 0, 2, 2, 2);
        rearView.add(editButton, 1, 2, 2, 2);


        rearView.setHgap(4);
        rearView.setVgap(4);
        rearView.setAlignment(Pos.CENTER_LEFT);

    }
}
