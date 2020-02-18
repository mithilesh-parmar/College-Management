package constants;

import com.google.cloud.storage.Bucket;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Constants {

    public static FileInputStream serviceAccount;

    private static final String testFile = "src/ischool-7f729-firebase-adminsdk-3r9gq-2f1029222a.json";

    static {
        try {
            serviceAccount = new FileInputStream(testFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static final String projectID = "ischool-7f729";
    public static final String profileImageFolder = "pp_images";
    public static final String eventImageFolder = "event_images";
    public static final String serviceAccountFile = "src/ischool-7f729-firebase-adminsdk-3r9gq-2f1029222a.json";

    public static final String projectBucketName = "ischool-7f729.appspot.com";

}
