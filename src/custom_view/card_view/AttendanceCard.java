package custom_view.card_view;

import javafx.beans.property.*;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.SectionAttendance;
import utility.DateUtility;

import java.text.SimpleDateFormat;

public class AttendanceCard extends AnimatingCard {

    private GridPane frontView;
    private GridPane rearView;

    private StringProperty courseName, year, date, subject;
    private IntegerProperty totalPresent, totalAbsent;
    private CardListener cardListener;


    public AttendanceCard(SectionAttendance sectionAttendance) {
        this.courseName = new SimpleStringProperty(sectionAttendance.getCourse());
        this.year = new SimpleStringProperty(String.valueOf(sectionAttendance.getYear()));
        this.totalPresent = sectionAttendance.presentStudentProperty();
        this.totalAbsent = sectionAttendance.absentStudentProperty();
        this.subject = new SimpleStringProperty(sectionAttendance.getSubject());


        this.date = new SimpleStringProperty(DateUtility.timeStampToReadable(sectionAttendance.getDate()));

        frontView = new GridPane();
        rearView = new GridPane();

        initFrontView();
        initRearView();
        setFrontView(frontView);
        setRearView(rearView);
        setShouldAnimate(true);

    }


    @Override
    void initFrontView() {
        Label courseNameLabel = new Label(courseName.get());
        Label yearLabel = new Label(year.get());
        Label dateLabel = new Label(date.get());

        courseNameLabel.textProperty().bind(this.courseName);
        yearLabel.textProperty().bind(this.year);


        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setMinWidth(100);
        columnConstraints.setHalignment(HPos.LEFT);

        frontView.setPadding(new Insets(8));

        frontView.getColumnConstraints()
                .addAll(columnConstraints, columnConstraints);

        frontView.add(new Label("Course: "), 0, 0);
        frontView.add(courseNameLabel, 1, 0);

        frontView.add(new Label("Year: "), 0, 1);
        frontView.add(yearLabel, 1, 1);

        frontView.add(new Label("Date: "), 0, 2);
        frontView.add(dateLabel, 1, 2);


        frontView.setHgap(4);
        frontView.setVgap(4);
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
    void initRearView() {
        Label subjectNameLabel = new Label(courseName.get());
        Label totalPresentLabel = new Label(year.get());
        Label totalAbsentLabel = new Label(date.get());

        subjectNameLabel.textProperty().bind(this.subject);
        totalPresentLabel.textProperty().bind(this.totalPresent.asString());
        totalAbsentLabel.textProperty().bind(this.totalAbsent.asString());

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setMinWidth(100);
        columnConstraints.setHalignment(HPos.LEFT);

        rearView.setPadding(new Insets(8));

        rearView.getColumnConstraints()
                .addAll(columnConstraints, columnConstraints);

        rearView.add(new Label("Subject: "), 0, 0);
        rearView.add(subjectNameLabel, 1, 0);

        rearView.add(new Label("Total Present: "), 0, 1);
        rearView.add(totalPresentLabel, 1, 1);

        rearView.add(new Label("Total Absent: "), 0, 2);
        rearView.add(totalAbsentLabel, 1, 2);


        rearView.setHgap(4);
        rearView.setVgap(4);
        rearView.setAlignment(Pos.CENTER_LEFT);

    }

}
