package test;


import com.google.api.services.storage.Storage;
import com.google.cloud.storage.Blob;
import constants.Constants;
import utility.CloudStorageUtility;
import utility.DocumentType;

import java.io.*;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StorageTest {
    public static void main(String[] args) throws IOException {


        CloudStorageUtility storageUtility = CloudStorageUtility.getInstance();
//
        Blob b = storageUtility.uploadDocument(Constants.profileImageFolder, "", new File("/Users/mithileshparmar/Desktop/test.png"), DocumentType.IMAGE.toString());


        System.out.println(b.getMediaLink());

    }
}
