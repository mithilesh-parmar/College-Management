import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import constants.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utility.CloudStorageUtility;

import java.io.IOException;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        System.out.println("Starting applcation");
        Parent root = FXMLLoader.load(getClass().getResource("/home/home.fxml"));
        System.out.println("Loaded Home");
        primaryStage.setTitle("ISchool");
        primaryStage.setScene(new Scene(root));
//        primaryStage.setFullScreen(true);
        primaryStage.setMaximized(true);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        new Thread(() -> {
            GoogleCredentials credentials = null;
            try {
                System.out.println("Loading Credentials : " + Constants.serviceAccount);
                credentials = GoogleCredentials.fromStream(Constants.serviceAccount);
                System.out.println("Loaded");
            } catch (IOException e) {
                e.printStackTrace();
            }


            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(credentials)
                    .setProjectId(Constants.projectID)
                    .build();


            System.out.println("Initialing app");
            FirebaseApp.initializeApp(options);

//            Loading cloud storage credentials
            CloudStorageUtility.getInstance();
        }).start();
        launch(args);
    }

}
