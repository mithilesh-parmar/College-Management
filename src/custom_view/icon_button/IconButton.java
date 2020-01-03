package custom_view.icon_button;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;


public class IconButton extends Button {

    private ImageView imageView = new ImageView();
    private Label label = new Label();
    private StringProperty iconPath = new SimpleStringProperty();
    private StringProperty text = new SimpleStringProperty();

    private HBox hBox = new HBox(5);

    public IconButton() {
        load();
    }


    public IconButton(String label, String iconPath) {
        super(label);
        setIconPath(iconPath);
        load();
    }

    private void load() {
        if (iconPath.get() != null && !iconPath.get().isEmpty()) {
            Image image = new Image(iconPath.get(), true);
            imageView.setImage(image);
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
        }


        label.setText(text.get());

        label.setAlignment(Pos.CENTER_LEFT);

        hBox.getChildren().addAll(imageView, label);

        setGraphic(hBox);
        setAlignment(Pos.CENTER);
    }

    public void setFitWidth(double fitWidth) {
        imageView.setFitWidth(fitWidth);
    }

    public void setFitHeight(double fitHeight) {
        imageView.setFitHeight(fitHeight);
    }

    public String getIconPath() {
        return iconPath.get();
    }

    public StringProperty iconPathProperty() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath.set(iconPath);
    }
}
