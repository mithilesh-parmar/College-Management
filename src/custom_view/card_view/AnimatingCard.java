package custom_view.card_view;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public abstract class AnimatingCard extends StackPane {

    private Node frontView;
    private Node rearView;
    private BooleanProperty ready = new SimpleBooleanProperty(false);
    private BooleanProperty shouldAnimate = new SimpleBooleanProperty(false);
    private BooleanProperty frontViewVisible = new SimpleBooleanProperty(true);

    AnimatingCard() {

        setOnMouseEntered(event -> showRearView());
        setOnMouseExited(event -> showFrontView());
        ready.addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                rearView.setOpacity(0.0);
                getChildren().addAll(rearView, frontView);
            }
        });
    }

    abstract void initFrontView();

    abstract void initRearView();


    void setFrontView(Node frontView) {
        this.frontView = frontView;
        this.frontView.visibleProperty().bind(frontViewVisible);
        this.frontView.opacityProperty().addListener((observableValue, number, t1) -> {
            if (t1.doubleValue() < 0.5) {
                frontViewVisible.set(false);
            } else frontViewVisible.set(true);
        });
        checkIfReady();
    }

    public void setShouldAnimate(boolean shouldAnimate) {
        this.shouldAnimate.set(shouldAnimate);
    }


    void setRearView(Node rearView) {
        this.rearView = rearView;
        checkIfReady();
    }

    private void checkIfReady() {
        ready.set(rearView != null && frontView != null);
    }

    private void showFrontView() {
        if (frontView == null || rearView == null || !shouldAnimate.get()) return;
        createTransition(frontView, frontView.getOpacity(), 1.0).play();
        createTransition(rearView, rearView.getOpacity(), 0.0).play();
    }


    private void showRearView() {
        if (frontView == null || rearView == null || !shouldAnimate.get()) return;
        createTransition(rearView, rearView.getOpacity(), 1.0).play();
        createTransition(frontView, frontView.getOpacity(), 0.0).play();
    }

    private FadeTransition createTransition(Node node, double from, double to) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(node);
        fadeTransition.setInterpolator(Interpolator.EASE_BOTH);
        fadeTransition.setDuration(new Duration(300));
        fadeTransition.setFromValue(from);
        fadeTransition.setToValue(to);
        fadeTransition.setCycleCount(1);
        fadeTransition.setAutoReverse(true);
        return fadeTransition;
    }
}
