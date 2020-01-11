package utility;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import listeners.DataChangeListener;
import model.StudentDocument;

import java.io.File;

public class StudentDocumentCloudStorageUtility {

    private CloudStorageUtility storageUtility = CloudStorageUtility.getInstance();
    private static StudentDocumentCloudStorageUtility instance;
    public ListProperty<StudentDocument> studentDocuments = new SimpleListProperty<>(FXCollections.observableArrayList());
    private DataChangeListener listener;

    private StudentDocumentCloudStorageUtility() {
    }


    public void getAllDocuments() {
        if (listener != null) listener.onDataLoaded(null);
        new Thread(() -> {
            if (listener != null) listener.onDataChange(null);
            Iterable<Blob> documents = storageUtility
                    .projectBucket
                    .list(Storage.BlobListOption.prefix("documents"))
                    .getValues();

            parseStudentDocumentsData(documents);
        }).start();
    }


    public void getStudentDocument(String admissionId) {
        if (listener != null) listener.onDataChange(null);
        new Thread(() -> {
            Iterable<Blob> values = storageUtility
                    .projectBucket
                    .list(Storage.BlobListOption.prefix("documents/" + admissionId))
                    .getValues();
            parseStudentDocumentsData(values);
        }).start();
    }

    public void uploadDocument(String registerationNumber, File document) {
        if (listener != null) listener.onDataChange(null);
        StudentDocument studentDocument = new StudentDocument(document, "documents/" + registerationNumber + "/" + document.getName(), registerationNumber, document.getName(), "", "");
        storageUtility.uploadStudentDocument(studentDocument);
        if (listener != null) listener.onDataLoaded(null);
    }

    private void parseStudentDocumentsData(Iterable<Blob> documents) {
        Platform.runLater(() -> {
            studentDocuments.clear();


            documents.forEach(blob -> {

                studentDocuments.get()
                        .add(new StudentDocument(null, blob.getBucket() + blob.getName(),
                                blob.getName().split("/")[1],
                                blob.getName().split("/")[2],
                                blob.getContentType(),
                                blob.getMediaLink()
                        ));
            });
            if (listener != null) listener.onDataLoaded(null);
        });
    }

    public void setListener(DataChangeListener dataChangeListener) {
        this.listener = dataChangeListener;
    }

    public static StudentDocumentCloudStorageUtility getInstance() {
        if (instance == null) synchronized (StudentDocumentCloudStorageUtility.class) {
            instance = new StudentDocumentCloudStorageUtility();
        }
        return instance;
    }
}
