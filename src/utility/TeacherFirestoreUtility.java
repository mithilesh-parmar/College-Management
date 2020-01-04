package utility;

import com.google.cloud.firestore.*;
import com.google.cloud.storage.Blob;
import constants.Constants;
import custom_view.card_view.Card;
import custom_view.card_view.CardListener;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import listeners.DataChangeListener;
import model.Notification;
import model.Teacher;
import teachers.TeacherCardListener;

import java.io.*;
import java.util.List;

public class TeacherFirestoreUtility {


    private static TeacherFirestoreUtility instance;
    public ObservableList<Teacher> teachers;
    public ListProperty<Card> teacherCards;
    private DataChangeListener listener;
    private DocumentUploadListener documentUploadListener;
    public MapProperty<Teacher, Card> teacherCardMapProperty;
    private TeacherCardListener cardListener;

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
        teacherCards = new SimpleListProperty<>(FXCollections.observableArrayList());
        teacherCardMapProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());
    }

    public void setCardListener(TeacherCardListener cardListener) {
        this.cardListener = cardListener;
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

    public void deleteTeacher(Teacher teacher) {
        if (teacher == null) return;
        FirestoreConstants.teacherCollectionReference.document(teacher.getID()).delete();
    }

    public void updateTeacherDetails(Teacher teacher, File profileImage) {


        if (profileImage == null) {

            FirestoreConstants.teacherCollectionReference.document(teacher.getID()).update(teacher.toJSON());
            documentUploadListener.onSuccess(null);
        } else {
            CloudStorageUtility storageUtility = CloudStorageUtility.getInstance();

            storageUtility.setListener(new DocumentUploadListener() {
                @Override
                public void onSuccess(Blob blob) {

                    teacher.setProfilePictureUrl(blob.getMediaLink());

                    FirestoreConstants.teacherCollectionReference.document(teacher.getID()).update(teacher.toJSON());
                    documentUploadListener.onSuccess(blob);
                }

                @Override
                public void onFailure(Exception e) {
                    documentUploadListener.onFailure(e);
                }
            });

            new Thread(() -> storageUtility.uploadDocument(Constants.profileImageFolder, teacher.getNameWithoutSpaces(), profileImage, DocumentType.IMAGE.toString())).start();

        }


    }

    public void publishNotification(Teacher teacher, Notification notification) {
        FirestoreConstants.teacherCollectionReference.document(teacher.getID()).collection("push_notification").add(notification.toJSON());
    }


    //    TODO add id field
    private void setTeacherDataToFirestore(Teacher teacher) {
        String teachNameAndEmail = teacher.getName() + " " + teacher.getEmail();
        System.out.println("Teacher name and email: " + teachNameAndEmail);


//        FirestoreConstants.teacherCollectionReference.document(teacher.getID()).update(teacher.toJSON());
    }

    public void addTeacherToFirestore(Teacher teacher, File profileImage) {
        CloudStorageUtility storageUtility = CloudStorageUtility.getInstance();
        storageUtility.setListener(new DocumentUploadListener() {
            @Override
            public void onSuccess(Blob blob) {

                teacher.setProfilePictureUrl(blob.getMediaLink());
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


    private void parseTeachersData(List<QueryDocumentSnapshot> data) {
        teachers.clear();
        teacherCards.clear();
        for (QueryDocumentSnapshot document : data) {
            Teacher teacher = Teacher.fromJSON(document.getData());
            Card card = new Card(teacher.getName(), teacher.getEmail(), teacher.getProfilePictureUrl(),true);


            if (cardListener != null) {
                card.setListener(new CardListener() {
                    @Override
                    public void onCardClick() {
                        cardListener.onCardClick(teacher);
                    }

                    @Override
                    public void onDeleteButtonClick() {
                        cardListener.onDeleteButtonClick(teacher);
                    }

                    @Override
                    public void onEditButtonClick() {
                        cardListener.onEditButtonClick(teacher);
                    }

                    @Override
                    public void onNotificationButtonClick() {
                        cardListener.onNotificationButtonClick(teacher);
                    }

                    @Override
                    public void onContextMenuRequested(MouseEvent event) {
                        cardListener.onContextMenuRequested(teacher,event);
                    }
                });

            }


            teachers.add(teacher);
            teacherCards.add(card);
            teacherCardMapProperty.put(teacher, card);
        }
    }
}

