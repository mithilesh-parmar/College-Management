package custom_view.time_textfield;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeTextField extends BorderPane {

    private ToggleGroup timeToggleGroup = new ToggleGroup();
    private ToggleButton amToggleButton = new ToggleButton("AM");
    private ToggleButton pmToggleButton = new ToggleButton("PM");
    private TextField timeTextField = new TextField();

    private StringProperty time = new SimpleStringProperty();
    private ObjectProperty<Toggle> selectedToggle = new SimpleObjectProperty<>();
    private TimeTextFieldListener listener;

    public TimeTextField() {

        HBox hBox = new HBox(amToggleButton, pmToggleButton);
        BorderPane.setMargin(hBox, new Insets(0, 5, 0, 5));

        LocalTime localTime = LocalTime.now();

        amToggleButton.setUserData("AM");
        pmToggleButton.setUserData("PM");

        timeToggleGroup.getToggles().addAll(amToggleButton, pmToggleButton);
        String[] split = localTime.format(DateTimeFormatter.ofPattern("hh:mm:a")).split(":");

        selectToggle(split);

        time.set(localTime.format(DateTimeFormatter.ofPattern("hh:mm")));

        selectedToggle.bind(timeToggleGroup.selectedToggleProperty());
        timeTextField.setText(time.get());
        setStyle("/styles/dark_metro_style.css");
        setLeft(timeTextField);
        setRight(hBox);

        timeTextField.textProperty().addListener((observableValue, s, t1) -> {
            if (listener == null) return;
            listener.onTimeSelected(timeTextField.getText() + timeToggleGroup.getSelectedToggle().getUserData());
        });

    }

    private void selectToggle(String[] split) {
        boolean isMorning = split[split.length - 1].toUpperCase().equals("AM");
        timeToggleGroup.selectToggle(isMorning ? amToggleButton : pmToggleButton);
    }

    public String getText() {
        return time.get();
    }


    public String getTime() {
        return timeTextField.getText() + timeToggleGroup.getSelectedToggle().getUserData();
    }

    public ToggleGroup getTimeToggleGroup() {
        return timeToggleGroup;
    }

    public void setTimeToggleGroup(ToggleGroup timeToggleGroup) {
        this.timeToggleGroup = timeToggleGroup;
    }

    public ToggleButton getAmToggleButton() {
        return amToggleButton;
    }

    public void setAmToggleButton(ToggleButton amToggleButton) {
        this.amToggleButton = amToggleButton;
    }

    public ToggleButton getPmToggleButton() {
        return pmToggleButton;
    }

    public void setPmToggleButton(ToggleButton pmToggleButton) {
        this.pmToggleButton = pmToggleButton;
    }

    public TextField getTimeTextField() {
        return timeTextField;
    }

    public void setTimeTextField(TextField timeTextField) {
        this.timeTextField = timeTextField;
    }


    public StringProperty timeProperty() {
        return time;
    }

    public void setTime(String time) {
        this.time.set(time);
        timeTextField.setText(time);
    }

    public Toggle getSelectedToggle() {
        return selectedToggle.get();
    }

    public ObjectProperty<Toggle> selectedToggleProperty() {
        return selectedToggle;
    }

    public void setSelectedToggle(Toggle selectedToggle) {
        this.selectedToggle.set(selectedToggle);
    }

    public void setText(String time) {

        String[] split = time.split(":");
        selectToggle(split);
        setTime(split[0] + ":" + split[1]);
    }
}
