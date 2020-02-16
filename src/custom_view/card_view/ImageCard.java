package custom_view.card_view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.File;

public class ImageCard extends AnimatingCard {


    private ImageCardListener listener;
    private ImageView imageView = new ImageView();
    private BorderPane rearPane = new BorderPane();
    private BorderPane frontPane = new BorderPane();
    private Image image;
    private String url;

    public ImageCard(Image image, boolean shouldAnimate) {

        this.image = image;
        this.url = image.getUrl();

        initFrontView();
        initRearView();
        setFrontView(frontPane);
        setRearView(rearPane);

        setShouldAnimate(shouldAnimate);
    }

    void initRearView() {
        Button deleteButton = new Button("Delete");
        deleteButton.setDefaultButton(true);
        deleteButton.setOnAction(actionEvent -> {
            if (listener == null) return;
            listener.onDeleteAction(this);
        });
        rearPane.setPadding(new Insets(14));
        rearPane.setCenter(deleteButton);
    }

    public void setImage(Image image) {
        this.image = image;
        this.url = image.getUrl();
        initFrontView();
        initRearView();
        setFrontView(frontPane);
        setRearView(rearPane);
    }


    public void setImage(File file) {
        this.image = new Image(file.toURI().getPath());
        this.url = this.image.getUrl();
        initFrontView();
        initRearView();
        setFrontView(frontPane);
        setRearView(rearPane);
    }


    public void setImage(String url) {
        this.image = new Image(url);
        this.url = url;
        initFrontView();
        initRearView();
        setFrontView(frontPane);
        setRearView(rearPane);
    }

    public void initFrontView() {


        Reflection reflection = new Reflection();
        reflection.setFraction(0.4);
        imageView.setImage(image);
        imageView.setEffect(reflection);
        imageView.setSmooth(true);
        imageView.setPickOnBounds(true);
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);

//        frontPane.setPadding(new Insets(4));
        frontPane.setCenter(imageView);
//
//        setBorder(new Border(new BorderStroke(Color.DARKSLATEGREY,
//                BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1))));
//        setId("card");
//
//        setStyle("/styles/dark_metro_style.css");

        setOnMouseClicked(event -> {
            if (listener == null) return;
            if (event.getClickCount() == 2)
                listener.onClickAction(this);
        });


    }

    public String getUrl() {
        return url;
    }

    public void setListener(ImageCardListener listener) {
        this.listener = listener;
    }


}
