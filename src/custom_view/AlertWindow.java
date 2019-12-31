package custom_view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertWindow {



    public static void showDeletionWindow(String content) {


        Stage dialogStage = new Stage();

        GridPane grd_pan = new GridPane();
        grd_pan.setAlignment(Pos.CENTER);
        grd_pan.setHgap(10);
        grd_pan.setVgap(10);//pading

        Scene scene = new Scene(grd_pan, 300, 150);
        dialogStage.setScene(scene);
        dialogStage.setTitle("Delete");
        dialogStage.initModality(Modality.WINDOW_MODAL);

        Label lab_alert = new Label(content);
        grd_pan.add(lab_alert, 0, 1);

        Button cancel = new Button("No");
        cancel.setOnAction(arg0 -> {


            dialogStage.hide();

        });
        Button yesButton = new Button("Yes");
        yesButton.setOnAction(arg0 -> {

        });

        grd_pan.add(cancel, 0, 2);

        dialogStage.show();

    }
}
