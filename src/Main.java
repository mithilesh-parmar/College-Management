import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import constants.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utility.CloudStorageUtility;
import utility.FirestoreConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {


        Parent root = FXMLLoader.load(getClass().getResource("home/home.fxml"));
        primaryStage.setTitle("ISchool");
        primaryStage.setScene(new Scene(root));
//        primaryStage.setFullScreen(true);
        primaryStage.setMaximized(true);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }


    public static void main(String[] args) throws IOException {


        new Thread(() -> {
            GoogleCredentials credentials = null;
            try {
                credentials = GoogleCredentials.fromStream(Constants.serviceAccount);
            } catch (IOException e) {
                e.printStackTrace();
            }


            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(credentials)
                    .setProjectId(Constants.projectID)
                    .build();


            FirebaseApp.initializeApp(options);

//            Loading cloud storage credentials
            CloudStorageUtility.getInstance();
        }).start();


        launch(args);
    }
}
