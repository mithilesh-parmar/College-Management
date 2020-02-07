package custom_view.chip_view;

import custom_view.chip_view.add_view.AddChipController;
import custom_view.chip_view.add_view.AddChipListener;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Section;


import java.io.IOException;
import java.util.List;

public class ChipView extends BorderPane {

    private ListProperty<Chip> chips = new SimpleListProperty<>(FXCollections.observableArrayList());
    private FlowPane flowPane;
    private Button iconButton;
    private StringProperty title;
    private Label titleNode = new Label("Title");

    private Section section;

    public ChipView(String title) {
        chips = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.title = new SimpleStringProperty(title);
        flowPane = new FlowPane();
        iconButton = new Button();
        setUpView();
    }

    public ChipView(List<Chip> chips, String title) {
        chips = new SimpleListProperty<>(FXCollections.observableArrayList(chips));
        flowPane = new FlowPane();
        this.title = new SimpleStringProperty(title);
        iconButton = new Button();
        setUpView();
    }

    public void setChips(ObservableList<Chip> chips) {
        this.chips.set(chips);
        setUpView();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    private void setUpView() {

        setId("container");
        setBorder(new Border(
                new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(2), new BorderWidths(1)))
        );
        setStyle("/styles/dark_metro_style.css");
        setPadding(new Insets(8));

        titleNode.textProperty().bind(title);

        setLeft(titleNode);
        BorderPane.setMargin(titleNode, new Insets(14));

        iconButton.setOnAction(actionEvent -> loadAddView());


        chips.addListener((ListChangeListener<Chip>) change -> {
            while (change.next()) {
                for (Chip chip : change.getRemoved()) {
                    flowPane.getChildren().remove(chip);
                }
                flowPane.getChildren().addAll(change.getAddedSubList());
            }
        });

        iconButton.setText("ADD");
        iconButton.setId("iconButton");
        iconButton.setDefaultButton(true);

        flowPane.setHgap(10);
        flowPane.setVgap(10);
        flowPane.setPadding(new

                Insets(8));
        if (chips.get() != null && chips.size() > 0)
            flowPane.getChildren().

                    setAll(chips);

        setCenter(flowPane);

        setRight(iconButton);
    }


    public ObservableList<Chip> getChips() {
        return chips.get();
    }

    public String getTitle() {
        return title.get();
    }

    private void loadAddView() {


        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/custom_view/chip_view/add_view/AddChipView.fxml"));
        final Stage stage = new Stage();
        Parent parent = null;
        try {


            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Chip Details");

            parent = loader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            AddChipController controller = loader.getController();

            controller.setListener(new AddChipListener() {
                @Override
                public void onChipAdded(Chip chip) {
                    close(stage);
                    chip.setCallback(chip1 -> removeSubject(chip1));
                    chips.get().add(chip);
                    section.getSubjects().add(chip.getTitle());
                }
            });
            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void setSection(Section section) {
        this.section = section;
        title.set(section.getSectionName());
        setChips(section.getSubjects());
    }

    private void removeSubject(Chip chip) {
        chips.remove(chip);
        section.getSubjects().remove(chip.getTitle());
    }

    public Section getSection() {
        return section;
    }

    public void setChips(List<String> subjects) {
        chips.clear();
        subjects.forEach(s -> {
            System.out.println("Adding new Chip: " + s);
            Chip chip = new Chip(s);
            chip.setCallback(this::removeSubject);
            chips.add(chip);
        });
    }
}
