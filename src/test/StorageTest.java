package test;


import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.storage.*;
import constants.Constants;
import utility.CloudStorageUtility;
import utility.DocumentType;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.google.common.io.Files.getFileExtension;

public class StorageTest {
    private static StorageOptions storageOptions;
    private static Storage storage;
    private static Bucket projectBucket;
    private final static String projectBucketName = Constants.projectBucketName;

    public static void main(String[] args) throws IOException {
        Timestamp timestamp = Timestamp.now();
        System.out.println(timestamp);
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
                            projectBucketName, com.google.cloud.storage.Storage.BucketGetOption.fields(Storage.BucketField.values())
                    );

        } catch (IOException e) {
            e.printStackTrace();
        }


//        projectBucket.list(Storage.BlobListOption.prefix("documents")).getValues().forEach(blob -> {
//            Storage storage = blob.getStorage();
//
//            System.out.println(blob);
//        });

        File file = new File("/Users/mithileshparmar/Development/JAVA_PROJECTS/Firebase Demo/src/todo");
        uploadDocumentWithData("documents", "/dheeraj" + file.getName(), file, DocumentType.TEXT.toString());

    }

    static void uploadDocumentWithData(String uploadFolderPath, String fileName, File document, String documentType) {


        uploadFolderPath = uploadFolderPath + "/" + (fileName.isEmpty() ? document.getName() : (fileName + "." + getFileExtension(document.getName())));
        documentType = documentType + "/" + getFileExtension(document.getName());

        try {
            String bucketName = "my-unique-bucket";
            Bucket bucket = storage.create(BucketInfo.newBuilder(bucketName)
                    // Possible values: http://g.co/cloud/storage/docs/bucket-locations#location-mr
                    .setLocation("asia")
                    .build());

            HashMap<String, String> metaData = new HashMap<>();
            metaData.put("admission_id", fileName);
            metaData.put("name", fileName);
            metaData.put("test", "yes");


            List<Acl> aclList = new ArrayList<>();
            aclList.add(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

            BlobInfo blobInfo = BlobInfo.newBuilder("documents", "test")
                    .setMetadata(metaData)
                    .setContentType(documentType)
                    .setAcl(aclList)

                    .build();


            byte[] fileContent = Files.readAllBytes(document.toPath());
            Blob blob = projectBucket.getStorage().create(blobInfo, fileContent);

            System.out.println(blob.getMediaLink());

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    static void uploadDocument(String uploadFolderPath, String fileName, File document, String documentType) {


        uploadFolderPath = uploadFolderPath + "/" + (fileName.isEmpty() ? document.getName() : (fileName + "." + getFileExtension(document.getName())));
        documentType = documentType + "/" + getFileExtension(document.getName());

        try {
            System.out.println("Uploading to: " + uploadFolderPath);
            Blob blob = projectBucket.create(uploadFolderPath, new FileInputStream(document), documentType);
            blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
            System.out.println(blob.getMediaLink());
        } catch (IOException e) {

            e.printStackTrace();

        }

    }
}
