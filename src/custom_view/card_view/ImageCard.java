package custom_view.card_view;

import com.google.cloud.Timestamp;
import custom_view.loading_image.LoadingImage;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

public class ImageCard extends AnimatingCard {


    private ImageCardListener listener;
    private ImageView imageView = new ImageView();
    private BorderPane rearPane = new BorderPane();
    private BorderPane frontPane = new BorderPane();
    private Image image;

    private String url;

    public ImageCard(Image image, boolean shouldAnimate) {
        this.image = image;
//        this.image = new LoadingImage(image.getUrl(), 100, 100);
        this.url = image.getUrl();


        initFrontView();
        initRearView();
        setFrontView(frontPane);
        setRearView(rearPane);

        setShouldAnimate(shouldAnimate);
    }


    public ImageCard(File file, boolean shouldAnimate) {

        this.image = new Image("file:" + file.getAbsolutePath().toString());
        this.url = file.getAbsolutePath().toString();

        initFrontView();
        initRearView();
        setFrontView(frontPane);
        setRearView(rearPane);

        setShouldAnimate(shouldAnimate);
    }

    public ImageCard(String url, boolean shouldAnimate) {


        this.url = url;

        this.image = new Image(url, true);

//        initFrontView();
        initFrontView(new LoadingImage(url, 100, 100));
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
            listener.onDeleteAction(this, url);
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
        this.image = new Image("file:" + file.getAbsolutePath());
        this.url = file.getAbsolutePath();
        initFrontView();
        initRearView();
        setFrontView(frontPane);
        setRearView(rearPane);
    }


    public void setImage(String url) {
//        this.image = ;
//        this.image = new Image(new File(url).toPath().toString());
        this.url = url;
        initFrontView(new LoadingImage(url, 100, 100));
        initRearView();
        setFrontView(frontPane);
        setRearView(rearPane);

    }

    //    This function is called in events controller where each event must have a url of images hence we show loading images
    private void initFrontView(LoadingImage loadingImage) {
        System.out.println("Setting loading images");
        Reflection reflection = new Reflection();
        reflection.setFraction(0.4);
        loadingImage.setEffect(reflection);
        loadingImage.setPickOnBounds(true);

        frontPane.setCenter(loadingImage);
        setOnMouseClicked(event -> {
            if (listener == null) return;
            if (event.getClickCount() == 2)
                listener.onClickAction(this);
        });

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

        frontPane.setCenter(imageView);
        setOnMouseClicked(event -> {
            if (listener == null) return;
            if (event.getClickCount() == 2)
                listener.onClickAction(this);
        });


    }


    public String getUrl() {
        System.out.println("Returning url: " + url);
        return url;
    }

    public void setListener(ImageCardListener listener) {
        this.listener = listener;
    }


}
