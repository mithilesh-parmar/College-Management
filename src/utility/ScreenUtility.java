package utility;

import javafx.stage.Screen;

public class ScreenUtility {

    public static double getScreenHeight() {
        return Screen.getPrimary().getBounds().getHeight();
    }


    public static double getScreenWidth() {
        return Screen.getPrimary().getBounds().getWidth();
    }


    public static double getScreenHalfHeight() {
        return getScreenHeight() / 2;
    }


    public static double getScreenThreeFourthWidth() {
        return getScreenWidth() * 0.75;
    }

    public static double getScreenHalfWidth() {
        return getScreenWidth() / 2;
    }
}

