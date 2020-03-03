package utility;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import listeners.DataChangeListener;
import model.StudentDocument;

import java.io.File;
import java.nio.file.Path;

public class StudentDocumentCloudStorageUtility {

    private CloudStorageUtility storageUtility = CloudStorageUtility.getInstance();
    private static StudentDocumentCloudStorageUtility instance;
    public ListProperty<StudentDocument> studentDocuments = new SimpleListProperty<>(FXCollections.observableArrayList());
    private DataChangeListener listener;

    private CloudStorageStudentDocumentListener studentDocumentListener;

    private StudentDocumentCloudStorageUtility() {
    }

    public void deleteDocument(StudentDocument studentDocument, DocumentUploadListener listener) {
        if (studentDocumentListener != null) studentDocumentListener.onStart();
        storageUtility.setListener(listener);
        new Thread(() -> {
            storageUtility
                    .projectBucket
                    .list(Storage.BlobListOption.prefix(studentDocument.getCloudStoragePath()))
                    .getValues()
                    .forEach(blob -> {
                        System.out.println("Deleting blob " + blob);
                        blob.delete();
                    });
            if (studentDocumentListener != null) studentDocumentListener.onFinish();
        }
        ).start();
    }

    public void downloadDocument(StudentDocument studentDocument) {
        new Thread(() -> {
            if (studentDocumentListener != null) studentDocumentListener.onStart();
            storageUtility
                    .projectBucket
                    .get(studentDocument.getCloudStoragePath())
                    .downloadTo(Path.of(studentDocument.getFileName()));
            if (studentDocumentListener != null) studentDocumentListener.onFinish();
        }).start();
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

    public void uploadDocument(String registrationNumber, File document, DocumentUploadListener listener) {

        StudentDocument studentDocument = new StudentDocument(document, "documents/" + registrationNumber + "/" + document.getName(), registrationNumber, document.getName(), "", "");
        storageUtility.uploadStudentDocument(studentDocument);
        storageUtility.setListener(listener);

    }

    private void parseStudentDocumentsData(Iterable<Blob> documents) {
        Platform.runLater(() -> {
            studentDocuments.clear();


            documents.forEach(blob -> {

                studentDocuments.get()
                        .add(new StudentDocument(null, blob.getName(),
                                blob.getName().split("/")[1],
                                blob.getName().split("/")[2],
                                blob.getContentType(),
                                blob.getMediaLink()
                        ));
            });
            if (listener != null) listener.onDataLoaded(null);
        });
    }

    public void setStudentDocumentListener(CloudStorageStudentDocumentListener studentDocumentListener) {
        this.studentDocumentListener = studentDocumentListener;
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
