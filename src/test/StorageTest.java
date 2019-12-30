package test;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import utility.CloudStorageUtility;
import utility.DocumentType;
import utility.DocumentUploadListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class StorageTest {
    public static void main(String[] args) throws IOException {

        CloudStorageUtility storageUtility = CloudStorageUtility.getInstance();

        storageUtility.setListener(new DocumentUploadListener() {
            @Override
            public void onSuccess(Blob blob) {
                System.out.println("Uploaded " + blob.getSelfLink());
            }

            @Override
            public void onFailure(Exception e) {
                System.out.println("error: " + e);
            }
        });

//        storageUtility.uploadDocument("pp_images", new File("/Users/mithileshparmar/Desktop/image.png"), DocumentType.IMAGE.toString());

    }
}
