package gallery_test;

import com.google.cloud.firestore.QuerySnapshot;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import listeners.DataChangeListener;
import utility.EventFirestoreUtility;

import java.net.URL;
import java.util.ResourceBundle;

public class GalleryTest implements Initializable, DataChangeListener {
    public FlowPane imageFlowPane;

    private ObservableList<String> imageUrls = FXCollections.observableArrayList();
    private EventFirestoreUtility eventFirestoreUtility = EventFirestoreUtility.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        eventFirestoreUtility.setListener(this);
        eventFirestoreUtility.getEvents();

//        imageUrls.addAll(
//                "https://upload.wikimedia.org/wikipedia/commons/thumb/8/87/Gorges-de-la-Nesque-DSC_0266.jpg/495px-Gorges-de-la-Nesque-DSC_0266.jpg",
//                "https://i.ytimg.com/vi/tetxj_VsSu4/maxresdefault.jpg",
//                "http://www.utas.edu.au/tf-assets/media/images/waves-1867285.2e16d0ba.fill-1200x600-c100.jpg",
//                "https://i.ytimg.com/vi/yL5x4-AEu1c/maxresdefault.jpg",
//                "http://i.ytimg.com/vi/j7xiyPEWkrg/maxresdefault.jpg",
//                "https://upload.wikimedia.org/wikipedia/commons/thumb/7/73/Izmir_02.jpg/1200px-Izmir_02.jpg",
//                "https://i.ytimg.com/vi/24rsjZ-6LoU/maxresdefault.jpg",
//                "http://www.visitscotland.com/cms-images/destinations/peebles/river-tweed-towards-peebles",
//                "https://i.ytimg.com/vi/423lOPkszz4/maxresdefault.jpg"
//        );

//        imageUrls.addAll(
//                " https://www.googleapis.com/download/storage/v1/b/ischool-7f729.appspot.com/o/event_imagesdsv%2Fdsv.jpg?generation=1581865500938817&alt=media",
//                "   https://www.googleapis.com/download/storage/v1/b/ischool-7f729.appspot.com/o/event_imagesdsv%2Fdsv.jpg?generation=1581865503146922&alt=media",
//                "  https://www.googleapis.com/download/storage/v1/b/ischool-7f729.appspot.com/o/event_imagesdsv%2Fdsv.jpg?generation=1581865504520617&alt=media");
//
//
        Platform.runLater(() -> imageUrls.forEach(s -> imageFlowPane.getChildren().add(createImage(s))));

//        TODO uploading images to firestore assigns similar url look into it
    }

    private Node createImage(String url) {
        System.out.println("Loading images " + url);
        ImageView imageView = null;

        final Image image = new Image(url, true);

        imageView = new ImageView(image);
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        return new StackPane(imageView, new Label(url));
    }

    @Override
    public void onDataLoaded(ObservableList data) {
        eventFirestoreUtility.events.forEach(event -> {
            imageUrls.addAll(event.getImages());
//            System.out.println(imageUrls.contains(event.getImages()));
        });
        Platform.runLater(() -> imageUrls.forEach(s -> imageFlowPane.getChildren().add(createImage(s))));
    }

    @Override
    public void onDataChange(QuerySnapshot data) {

    }

    @Override
    public void onError(Exception e) {

    }
}
