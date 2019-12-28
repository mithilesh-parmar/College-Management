package custom_view;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import utility.SearchCallback;

import java.io.IOException;

public class SearchTextFieldController extends HBox {


    private SearchCallback callback;


    public TextField searchTextField;


    public SearchTextFieldController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/custom_view/SearchTextField.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);


        try {
            fxmlLoader.load();

            searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (callback != null) callback.performSearch(oldValue, newValue);
            });

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }


    public void setCallback(SearchCallback callback) {
        this.callback = callback;
    }
}
