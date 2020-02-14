package custom_view.dialog_helper;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.Optional;

public class CustomDialog {

    public static Optional<String> showInputDialogWithOneParameter(String title, String promptedText) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(title);

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField studentAdmissionField = new TextField();
        studentAdmissionField.setPromptText(promptedText);

        gridPane.add(studentAdmissionField, 1, 0);
        gridPane.add(new Label(promptedText), 0, 0);

        dialog.getDialogPane().setContent(gridPane);

        // Request focus on the username field by default.
        Platform.runLater(studentAdmissionField::requestFocus);

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return studentAdmissionField.getText();
            }
            return null;
        });


        return dialog.showAndWait();
    }


    public static Optional<Pair<String, String>> showInputDialogWithTwoParameter(String title, String promptedTextOne, String promptedTextTwo, String separatorText) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle(title);

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField from = new TextField();
        from.setPromptText(promptedTextOne);
        TextField to = new TextField();
        to.setPromptText(promptedTextTwo);

        gridPane.add(from, 0, 0);
        gridPane.add(new Label(" " + separatorText + " "), 1, 0);
        gridPane.add(to, 2, 0);

        dialog.getDialogPane().setContent(gridPane);

        // Request focus on the username field by default.
        Platform.runLater(from::requestFocus);

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(from.getText(), to.getText());
            }
            return null;
        });

        return dialog.showAndWait();
    }


}
