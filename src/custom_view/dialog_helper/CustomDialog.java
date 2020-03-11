package custom_view.dialog_helper;

import custom_view.loading_combobox.batches.BatchLoadingComboBox;
import custom_view.loading_combobox.class_section_combobox.ClassSectionComboBox;
import custom_view.loading_combobox.class_section_combobox.ClassSectionListener;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import model.Batch;
import model.ClassItem;
import model.Section;

import java.time.LocalDate;
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

    public static Optional<LocalDate> showInputDialogWithDatePicker(String title, String promptedText) {
        Dialog<LocalDate> dialog = new Dialog<>();
        dialog.setTitle(title);

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));


        DatePicker datePicker = new DatePicker();

        gridPane.add(datePicker, 1, 0);
        gridPane.add(new Label(promptedText), 0, 0);

        dialog.getDialogPane().setContent(gridPane);

        // Request focus on the username field by default.
        Platform.runLater(datePicker::requestFocus);

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return datePicker.getValue();
            }
            return null;
        });


        return dialog.showAndWait();
    }

    public static Optional<Boolean> showInputDialogWithRadioButton(String title, String promptedText) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle(title);

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));


        ToggleGroup group = new ToggleGroup();

        RadioButton firstButton = new RadioButton("True");
        firstButton.setUserData(true);
        RadioButton secondButton = new RadioButton("False");
        secondButton.setUserData(false);


        group.getToggles().addAll(firstButton, secondButton);

        gridPane.add(firstButton, 1, 0);
        gridPane.add(secondButton, 2, 0);
        gridPane.add(new Label(promptedText), 0, 0);

        dialog.getDialogPane().setContent(gridPane);

        // Request focus on the username field by default.
        Platform.runLater(firstButton::requestFocus);

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return (Boolean) group.getSelectedToggle().getUserData();
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


    public static Optional showDialogWithTwoButtons(String title, String message, ButtonType buttonOne, ButtonType buttonTwo) {
        Dialog dialog = new Dialog<>();
        dialog.setTitle(title);

        // Set the button types.
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonOne, buttonTwo, cancelButton);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        gridPane.add(new Label(message), 0, 0, 2, 2);
        dialog.getDialogPane().setContent(gridPane);
        return dialog.showAndWait();
    }


    public static Optional<Pair<Section, String>> showDialogWithClassSectionAndSubjectComboBox(String title, boolean showEditButton) {
        Dialog<Pair<Section, String>> dialog = new Dialog<>();

        ObjectProperty<Section> selectedSection = new SimpleObjectProperty<>();

        dialog.setTitle(title);

        // Set the button types.
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType submitButton = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(cancelButton);
        dialog.getDialogPane().getButtonTypes().addAll(submitButton);

        ClassSectionComboBox classSectionComboBox = new ClassSectionComboBox();
        classSectionComboBox.setIsEditable(showEditButton);
        classSectionComboBox.getSectionComboBox().setMinWidth(100);
        classSectionComboBox.getClassComboBox().setMinWidth(100);
        ComboBox<String> subjectComboBox = new ComboBox();
        subjectComboBox.setMinWidth(80);
        subjectComboBox.setMinHeight(30);

        classSectionComboBox.setListener(new ClassSectionListener() {
            @Override
            public void onSectionSelected(Section section) {
                selectedSection.set(section);
                subjectComboBox.getItems().setAll(selectedSection.get().getSubjects());
            }

            @Override
            public void onClassSelected(ClassItem classItem) {

            }
        });


        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));


        gridPane.add(classSectionComboBox, 0, 0, 2, 2);

        gridPane.add(new Label("Subject"), 0, 3);
        gridPane.add(subjectComboBox, 1, 3, 2, 2);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButton) {
                return new Pair<Section, String>(selectedSection.get(), subjectComboBox.getSelectionModel().getSelectedItem());
            }
            return null;
        });


        dialog.getDialogPane().setContent(gridPane);
        return dialog.showAndWait();

    }


    public static Optional<Pair<ClassItem, Section>> showDialogWithClassAndSectionComboBox(String title) {
        Dialog<Pair<ClassItem, Section>> dialog = new Dialog<>();

        ObjectProperty<Section> selectedSection = new SimpleObjectProperty<>();
        ObjectProperty<ClassItem> selectedClass = new SimpleObjectProperty<>();

        dialog.setTitle(title);

        // Set the button types.
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType submitButton = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(cancelButton);
        dialog.getDialogPane().getButtonTypes().addAll(submitButton);

        ClassSectionComboBox classSectionComboBox = new ClassSectionComboBox();

        classSectionComboBox.getSectionComboBox().setMinWidth(100);
        classSectionComboBox.getClassComboBox().setMinWidth(100);

        classSectionComboBox.setListener(new ClassSectionListener() {
            @Override
            public void onSectionSelected(Section section) {
                selectedSection.set(section);
            }

            @Override
            public void onClassSelected(ClassItem classItem) {
                selectedClass.set(classItem);
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));


        gridPane.add(classSectionComboBox, 0, 0, 4, 2);


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButton) {
                return new Pair<>(selectedClass.get(), selectedSection.get());
            }
            return null;
        });


        dialog.getDialogPane().setContent(gridPane);
        return dialog.showAndWait();

    }

    public static Optional<Batch> showDialogWithBatchComboBox(String title) {
        Dialog<Batch> dialog = new Dialog<>();

        ObjectProperty<Batch> selectedBatch = new SimpleObjectProperty<>();

        dialog.setTitle(title);

        // Set the button types.
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType submitButton = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(cancelButton);
        dialog.getDialogPane().getButtonTypes().addAll(submitButton);


        BatchLoadingComboBox comboBox = new BatchLoadingComboBox();
        comboBox.setMinWidth(100);
        comboBox.setListener(result -> {
            selectedBatch.set((Batch) result);
        });

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));


        gridPane.add(comboBox, 0, 0, 2, 2);


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButton) {
                return selectedBatch.get();
            }
            return null;
        });


        dialog.getDialogPane().setContent(gridPane);
        return dialog.showAndWait();

    }

}
