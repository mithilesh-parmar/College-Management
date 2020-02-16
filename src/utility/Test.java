package utility;


import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.effect.Reflection;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.net.URI;
import java.util.Objects;
import java.util.stream.Stream;

public class Test extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        HostServices hs = getHostServices();
        String folder = hs.resolveURI(hs.getDocumentBase(), "/Users/mithileshparmar/Desktop/test");

        int[] index = {0};

        Unit[] images =
                Stream.of(Objects.requireNonNull(new File(new URI(folder).getPath()).list()))
                        .map(name -> hs.resolveURI(folder, name))
                        .map(url -> new Unit(url, index[0]++))
                        .toArray(Unit[]::new);

        for (int i = 0; i < images.length; i++) {
            System.out.println(images[i].url);
        }
        Group container = new Group();
        container.setStyle("-fx-background-color:derive(black, 20%)");
        container.getChildren().addAll(images);

        Slider slider = new Slider(0, images.length - 1, 0);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setBlockIncrement(1);
        slider.setSnapToTicks(true);

        container.getChildren().add(slider);

        Scene scene = new Scene(container, 1000, 400, true);
        scene.setFill(Color.rgb(33, 33, 33));

        stage.setScene(scene);
        stage.getScene().setCamera(new PerspectiveCamera());
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();

        slider.translateXProperty().bind(stage.widthProperty().divide(2).subtract(slider.widthProperty().divide(2)));
        slider.setTranslateY(10);

        // FxTransformer.sliders(new DoubleProperty[] {x, z, rotation}, new String[] {"x", "z", "rotation"}, stage, -360, 360).show();
        // new FxTransformer(images, IntStream.range(0, images.length).mapToObj(i -> "images["+i+"]").toArray(String[]::new), stage, -500, 1000).show();

        slider.valueProperty().addListener((p, o, n) -> {
            if (n.doubleValue() == n.intValue())
                Stream.of(images).forEach(u -> u.update(n.intValue(), stage.getWidth(), stage.getHeight()));
        });

        container.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getTarget() instanceof Unit)
                slider.setValue(((Unit) e.getTarget()).index);
        });

        Button close = new Button("X");
        close.setOnAction(e -> System.exit(0));
        close.getStyleClass().clear();
        close.setStyle("-fx-text-fill:white;-fx-font-size:15;-fx-font-weight:bold;-fx-font-family:'Comic Sans MS';");

        container.getChildren().add(close);
        close.translateXProperty().bind(stage.widthProperty().subtract(15));

        slider.setValue(5);
    }

    public static class Unit extends ImageView {
        final static Reflection reflection = new Reflection();
        final static Point3D rotationAxis = new Point3D(0, 90, 1);

        String url;

        static {
            reflection.setFraction(0.5);
        }

        final int index;
        final Rotate rotate = new Rotate(0, rotationAxis);
        final TranslateTransition transition = new TranslateTransition(Duration.millis(300), this);

        public Unit(String imageUrl, int index) {
            super(imageUrl);
            this.url = imageUrl;
            setEffect(reflection);
            setUserData(index);

            this.index = index;
            getTransforms().add(rotate);
        }

        public void update(int currentIndex, double width, double height) {
            int ef = index - currentIndex;
            double middle = width / 2 - 100;
            boolean b = ef < 0;

            setTranslateY(height / 2 - getImage().getHeight() / 2);
            double x, z, theta, pivot;

            if (ef == 0) {
                z = -300;
                x = middle;
                theta = 0;
                pivot = b ? 200 : 0;
            } else {
                x = middle + ef * 82 + (b ? -147 : 147);
                z = -78.588;
                pivot = b ? 200 : 0;
                theta = b ? 46 : -46;
            }
            rotate.setPivotX(pivot);
            rotate.setAngle(theta);

            transition.pause();
            transition.setToX(x);
            transition.setToZ(z);
            transition.play();
        }

    }

}