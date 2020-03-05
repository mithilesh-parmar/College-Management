package custom_view;

import javafx.animation.TranslateTransition;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.control.Slider;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import utility.ScreenUtility;

import java.util.ArrayList;
import java.util.List;

public class ImageCarousel extends Group {

    private List<Unit> images;
    private Slider slider;

    public ImageCarousel() {



        setStyle("-fx-background-color:derive(black, 20%)");
//        container.getChildren().addAll(images);

        slider = new Slider();
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setBlockIncrement(1);
        slider.setSnapToTicks(true);

        getChildren().add(slider);

//        Scene scene = new Scene(this, 1000, 400, true);
//        scene.setFill(Color.rgb(33, 33, 33));


        slider.setTranslateY(10);;


        slider.valueProperty().addListener((p, o, n) -> {
            if (n.doubleValue() == n.intValue())
                images.stream().forEach(unit -> unit.update(n.intValue(), ScreenUtility.getScreenHalfWidth(), ScreenUtility.getScreenHalfHeight()));
        });

        addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getTarget() instanceof Unit) {

            }
            slider.setValue(((Unit) e.getTarget()).index);
        });


    }

    public void setImages(List<String> images) {
        List<Unit> units = new ArrayList();
        for (int i = 0; i < images.size(); i++) {
            units.add(new Unit(images.get(i), i));
        }

        this.images = units;
        getChildren().addAll(units);

    }

    private static class Unit extends ImageView {
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
            super(new Image(imageUrl, true));
            this.url = imageUrl;
            setEffect(reflection);
            setFitHeight(150);
            setFitWidth(150);
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
