package utility;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import constants.Constants;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import listeners.DataChangeListener;
import model.Teacher;

import java.io.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TeacherFirestoreUtility {


    private static TeacherFirestoreUtility instance;
    public ObservableList<Teacher> teachers;
    private DataChangeListener listener;
    private DocumentUploadListener documentUploadListener;

    private EventListener<QuerySnapshot> teacherDataListener = (snapshot, e) -> {
        if (e != null) {
            System.err.println("Listen failed: " + e);
            if (listener != null) listener.onError(new Exception("Listen failed: " + e));
            return;
        }
        if (snapshot != null && !snapshot.isEmpty()) {
            Platform.runLater(() -> {
                if (listener != null) listener.onDataChange(snapshot);
                System.out.println("Encountered Changes");
                parseTeachersData(snapshot.getDocuments());
                if (listener != null) listener.onDataLoaded(teachers);
            });
        } else {
            System.out.print("Current data: null");
            if (listener != null) listener.onError(new Exception("Current data is null"));
        }
    };


    private TeacherFirestoreUtility() {
        teachers = FXCollections.observableArrayList();
    }

    public static TeacherFirestoreUtility getInstance() {
        if (instance == null) {
            synchronized (StudentFirestoreUtility.class) {
                instance = new TeacherFirestoreUtility();
            }
        }
        return instance;
    }

    public void getTeachers() {

        if (teachers.size() > 0) listener.onDataLoaded(teachers);
        else
            FirestoreConstants.teacherCollectionReference.addSnapshotListener(teacherDataListener);

    }

    public void addTeacherToFirestore(Teacher teacher, File profileImage) {
        CloudStorageUtility storageUtility = CloudStorageUtility.getInstance();
        storageUtility.setListener(new DocumentUploadListener() {
            @Override
            public void onSuccess(Blob blob) {

                teacher.setProfilePictureUrl(blob.getSelfLink());
                FirestoreConstants.teacherCollectionReference.add(teacher.toJSON());

                documentUploadListener.onSuccess(blob);
            }

            @Override
            public void onFailure(Exception e) {
                documentUploadListener.onFailure(e);
            }
        });

        new Thread(() -> storageUtility.uploadDocument(Constants.profileImageFolder, teacher.getNameWithoutSpaces(), profileImage, DocumentType.IMAGE.toString())).start();

    }

    public void setDocumentUploadListener(DocumentUploadListener documentUploadListener) {
        this.documentUploadListener = documentUploadListener;
    }

    public void setListener(DataChangeListener listener) {
        this.listener = listener;
    }


    public ObservableList<Teacher> parseTeachersData(List<QueryDocumentSnapshot> data) {
        teachers.clear();
        for (QueryDocumentSnapshot document : data) teachers.add(Teacher.fromJSON(document.getData()));
        return teachers;
    }
}

