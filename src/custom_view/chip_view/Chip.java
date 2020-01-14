package custom_view.chip_view;

import custom_view.icon_button.IconButton;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import javax.security.auth.callback.Callback;


public class Chip extends HBox {

    public interface ChipCallback {
        void onCancel(Chip chip);
    }

    private StringProperty title;
    private DoubleProperty spacing;
    private Button icon;
    private Label label;
    private ChipCallback callback;

    public Chip(String title) {
        this.title = new SimpleStringProperty(title);
        this.spacing = new SimpleDoubleProperty(5);
        this.icon = new Button();
        setUpView();
    }

    public Chip(String title, ChipCallback callback) {
        this.title = new SimpleStringProperty(title);
        this.spacing = new SimpleDoubleProperty(5);
        this.icon = new Button();
        this.callback = callback;
        setUpView();
    }

    public void setCallback(ChipCallback callback) {
        this.callback = callback;
    }

    public Chip(String title, double spacing, Button icon) {
        this.title = new SimpleStringProperty(title);
        this.spacing = new SimpleDoubleProperty(spacing);
        this.icon = icon;
        setUpView();
    }


    private void setUpView() {
        setAlignment(Pos.CENTER);
        icon.setOnAction(actionEvent -> {
            if (callback != null) {
                callback.onCancel(this);
            }
        });
        Image image = new Image("/assets/delete.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(10);
        imageView.setFitHeight(10);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setPickOnBounds(true);
        icon.setGraphic(imageView);
        icon.setId("deleteButton");
        label = new Label(title.get());
        label.textProperty().bind(title);
        setSpacing(spacing.doubleValue());
        setPadding(new Insets(6, 4, 6, 4));
        getChildren().addAll(label, icon);
        setBorder(new Border(new BorderStroke(Color.SALMON,
                BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1))));
        setStyle("/styles/dark_metro_style.css");
        setId("chip");

    }
}
