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
import model.Leave;
import utility.DateUtility;

public class LeaveCard extends AnimatingCard {

    private StringProperty title, subTitle, fromDate, tillDate;


    private GridPane frontView;
    private GridPane rearView;
    private LeaveCardListener listener;

    private Leave leave;


    public LeaveCardListener getListener() {
        return listener;
    }

    public void setListener(LeaveCardListener listener) {
        this.listener = listener;
    }

    public LeaveCard(Leave leave) {
        this.leave = leave;
        this.title = new SimpleStringProperty(leave.getTeacherName());
        this.subTitle = new SimpleStringProperty(leave.getReason());
        this.fromDate = new SimpleStringProperty(DateUtility.timeStampToReadable(leave.getStartDate()));
        this.tillDate = new SimpleStringProperty(DateUtility.timeStampToReadable(leave.getEndDate()));
        frontView = new GridPane();
        rearView = new GridPane();

        initFrontView();
        initRearView();
        setFrontView(frontView);
        setRearView(rearView);
        setShouldAnimate(true);

    }

    @Override
    public void initFrontView() {
        Label teacherNameLabel = new Label(title.get());
        Label reasonLabel = new Label(subTitle.get());


        teacherNameLabel.textProperty().bind(this.title);
        reasonLabel.textProperty().bind(this.subTitle);


        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setMinWidth(50);
        columnConstraints.setHalignment(HPos.LEFT);

        frontView.setPadding(new Insets(4));

        frontView.getColumnConstraints()
                .addAll(columnConstraints, columnConstraints);

        frontView.add(new Label("Name: "), 0, 0);
        frontView.add(teacherNameLabel, 1, 0);

        frontView.add(new Label("Reason: "), 0, 1);
        frontView.add(reasonLabel, 1, 1);


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
        Label fromDateLabel = new Label(fromDate.get());
        Label tillDateLabel = new Label(tillDate.get());


        fromDateLabel.textProperty().bind(this.fromDate);
        tillDateLabel.textProperty().bind(this.tillDate);


        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setMinWidth(100);
        columnConstraints.setHalignment(HPos.LEFT);

        rearView.setPadding(new Insets(4));

        rearView.getColumnConstraints()
                .addAll(columnConstraints, columnConstraints);

        Button acceptButton = new Button("Accept");
        acceptButton.setDefaultButton(true);
        Button declineButton = new Button("Decline");
        Button editButton = new Button("Edit");

        acceptButton.setOnAction(actionEvent -> listener.onAcceptAction(leave));
        editButton.setOnAction(actionEvent -> listener.onClick(leave));
        declineButton.setOnAction(actionEvent -> listener.onDeclineAction(leave));

        rearView.add(new Label("Start Date: "), 0, 0);
        rearView.add(fromDateLabel, 1, 0);

        rearView.add(new Label("End Date: "), 0, 1);
        rearView.add(tillDateLabel, 1, 1);


        rearView.add(acceptButton, 0, 2);
        rearView.add(declineButton, 1, 2);
        rearView.add(editButton, 2, 2);


        rearView.setHgap(4);
        rearView.setVgap(4);
        rearView.setAlignment(Pos.CENTER_LEFT);

    }
}
