package utility;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import constants.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class CloudStorageUtility {

    private DocumentUploadListener listener;
    private static CloudStorageUtility instance;
    private StorageOptions storageOptions;
    private Storage storage;
    private Bucket projectBucket;
    private final String projectBucketName = Constants.projectBucketName;


    private CloudStorageUtility() {

        try {

            storageOptions = StorageOptions
                    .newBuilder()
                    .setCredentials(
                            GoogleCredentials
                                    .fromStream(new FileInputStream("ischool-7f729-firebase-adminsdk-3r9gq-2f1029222a.json"))
                    ).build();

            storage = storageOptions.getService();

            projectBucket = storage
                    .get(
                            projectBucketName, Storage.BucketGetOption.fields(Storage.BucketField.values())
                    );

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Bucket getProjectBucket() {
        return projectBucket;
    }

    public void setListener(DocumentUploadListener listener) {
        this.listener = listener;
    }

    public static CloudStorageUtility getInstance() {
        if (instance == null) synchronized (CloudStorageUtility.class) {
            instance = new CloudStorageUtility();
        }
        return instance;
    }

    public Blob getDocument(String srcFilename) {
        Blob blob = storage.get(BlobId.of(projectBucketName, srcFilename));
        return blob;
    }

    public Blob uploadDocument(String uploadFolderPath, String fileName, File document, String documentType) {


        uploadFolderPath = uploadFolderPath + "/" + (fileName.isEmpty() ? document.getName() : (fileName + "." + getFileExtension(document.getName())));
        documentType = documentType + "/" + getFileExtension(document.getName());

        try {
            System.out.println("Uploading to: " + uploadFolderPath);
            Blob blob = projectBucket.create(uploadFolderPath, new FileInputStream(document), documentType);


            blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

            if (listener != null) listener.onSuccess(blob);
            return blob;
        } catch (FileNotFoundException e) {
            listener.onFailure(e);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    private String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else return "";
    }

    public void listAllBuckets() {
        Page<Blob> blobs = projectBucket.list();
        for (Blob blob : blobs.iterateAll()) {
            // do something with the blob
            System.out.println(blob.toString());

        }

    }

}
