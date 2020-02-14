package custom_view.card_view;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.File;

public class ImageCard extends AnimatingCard {


    private CardListener cardListener;
    private ImageView imageView = new ImageView();
    //    private GridPane frontView = new GridPane();
    private Pane rearPane = new Pane();

    private Pane frontPane = new Pane();

    private Image image;

    public void setCardListener(CardListener cardListener) {
        this.cardListener = cardListener;
    }

    public ImageCard(Image image) {

        this.image = image;

        initFrontView();
        initRearView();
        setFrontView(frontPane);
        setRearView(rearPane);
        setShouldAnimate(true);
    }

    void initRearView() {

        Button deleteButton = new Button("Delete");
//        editButton.setOnAction(actionEvent -> listener.onClick(exam));


        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setMinWidth(100);
        columnConstraints.setHalignment(HPos.LEFT);

        rearPane.setPadding(new Insets(14));
        rearPane.getChildren().add(deleteButton);
    }

    public void setImage(Image image) {
        this.image = image;
        initFrontView();
        initRearView();
        setFrontView(frontPane);
        setRearView(rearPane);
    }


    public void setImage(File file) {
        this.image = new Image(file.toURI().getPath());
        initFrontView();
        initRearView();
        setFrontView(frontPane);
        setRearView(rearPane);
    }


    public void setImage(String url) {
        this.image = new Image(url);
        initFrontView();
        initRearView();
        setFrontView(frontPane);
        setRearView(rearPane);
    }

    public void initFrontView() {

        imageView.setImage(image);
        imageView.setSmooth(true);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setMinWidth(100);
        columnConstraints.setHalignment(HPos.LEFT);

        frontPane.setPadding(new Insets(8));
        frontPane.getChildren().add(imageView);

//        frontPane.getColumnConstraints()
//                .addAll(columnConstraints, columnConstraints);
//
//        frontPane.add(new Label("Course: "), 0, 0);
//        frontPane.add(titleLabel, 1, 0);
//
//        frontPane.setHgap(10);
//        frontPane.setVgap(10);
//        frontPane.setAlignment(Pos.CENTER_LEFT);


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
