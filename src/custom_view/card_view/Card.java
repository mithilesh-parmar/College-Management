package custom_view.card_view;

import custom_view.icon_button.IconButton;
import custom_view.loading_image.LoadingImage;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class Card
        extends BorderPane
        implements Initializable {

    private LoadingImage imageView;
    private Label nameLabel = new Label();
    private Label emailLabel = new Label();
    private Button editButton = new IconButton("", "/assets/edit.png"),
            deleteButton = new IconButton("", "/assets/delete.png"),
            notificationsButton = new IconButton("", "/assets/notification.png");


    public Card(String name, String email, String imageUrl) {
        nameLabel.setText(name);
        emailLabel.setText(email);
        imageView = new LoadingImage(imageUrl, -1, -1);
        loadData(name, email, imageUrl);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }

    public void setListener(CardListener listener) {
        editButton.setOnAction(actionEvent -> listener.onEditButtonClick());
        deleteButton.setOnAction(actionEvent -> listener.onDeleteButtonClick());
        notificationsButton.setOnAction(actionEvent -> listener.onNotificationButtonClick());
        setOnMouseClicked(event -> listener.onCardClick());
    }

    private void loadData(String name, String email, String imageUrl) {
        setPadding(new Insets(10));
        setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.DOTTED, new CornerRadii(4), new BorderWidths(1))));
        nameLabel.setText(name);
        emailLabel.setText(email);


        setLeft(imageView);
        setMargin(imageView, new Insets(15));

        VBox vBox = new VBox(2);
        vBox.setAlignment(Pos.CENTER_LEFT);
        vBox.getChildren().addAll(nameLabel, emailLabel);


        HBox hBox = new HBox();
        hBox.setSpacing(5);
        hBox.getChildren().addAll(editButton, notificationsButton, deleteButton);


        vBox.getChildren().add(hBox);
        setRight(vBox);


        setId("card");

        setStyle("/styles/dark_metro_style.css");


    }


}
