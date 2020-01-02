package custom_view.loading_image;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;


public class LoadingImage extends StackPane {

    private ImageView imageView = new ImageView();
    private Image image;
    private ProgressIndicator progressIndicator = new ProgressIndicator();
    private StringProperty imageUrl = new SimpleStringProperty();
    private BooleanProperty loadingData = new SimpleBooleanProperty(true);


    private final double size = 50;
    private final String defaultUrl = "/assets/add-user.png";

    public LoadingImage(String url, double fitHeight, double fitWidth) {
        setImageUrl(url);
        progressIndicator.visibleProperty().bind(loadingData);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setPickOnBounds(true);
        imageView.setFitWidth(fitWidth == -1 ? size : fitWidth);
        imageView.setFitHeight(fitHeight == -1 ? size : fitWidth);
        loadData();
    }

    private void loadData() {

        image = new Image(getUrl(), true);
        image.progressProperty().addListener((observableValue, number, t1) -> {
            if (t1.doubleValue() == 1.0) loadingData.set(false);
        });
        image.errorProperty().addListener((observableValue, aBoolean, t1) -> {
            loadingData.set(false);

        });
        image.exceptionProperty().addListener((observableValue, e, t1) -> {
            loadingData.set(false);

        });

        imageView.setImage(image);
        getChildren().addAll(imageView, progressIndicator);

    }

    private void setImageUrl(String imageUrl) {
        this.imageUrl.set(imageUrl);
    }

    private String getUrl() {

        return (imageUrl.get() == null || imageUrl.get().isEmpty()) ? defaultUrl : imageUrl.get();
    }
}
