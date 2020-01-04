package utility;

import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
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
import model.Leave;
import teacher_leaves.LeavesCardListener;

import java.util.List;

public class TeacherLeavesUtility {

    private static TeacherLeavesUtility instance;

    public ListProperty<Card> teacherLeavesCards;
    public ObservableList<Leave> teacherLeaves;
    private DataChangeListener listener;
    public MapProperty<Leave, Card> teacherLeaveCardMapProperty;
    private LeavesCardListener cardListener;

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
                parseTeacherLeavesData(snapshot.getDocuments());
                if (listener != null) listener.onDataLoaded(teacherLeaves);
            });
        } else {
            System.out.print("Current data: null");
            if (listener != null) listener.onError(new Exception("Current data is null"));
        }
    };


    public static TeacherLeavesUtility getInstance() {
        if (instance == null) synchronized (TeacherLeavesUtility.class) {
            instance = new TeacherLeavesUtility();
        }
        return instance;
    }


    public void getTeacherLeaves() {
        if (teacherLeaves.size() > 0) listener.onDataLoaded(teacherLeaves);
        else
            FirestoreConstants.teacherLeavesCollectionReference.addSnapshotListener(teacherDataListener);
    }


    public void setListener(DataChangeListener listener) {
        this.listener = listener;
    }

    public void setCardListener(LeavesCardListener cardListener) {
        this.cardListener = cardListener;
    }

    private TeacherLeavesUtility() {
        teacherLeaves = FXCollections.observableArrayList();
        teacherLeavesCards = new SimpleListProperty<>(FXCollections.observableArrayList());
        teacherLeaveCardMapProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());
    }


    private void parseTeacherLeavesData(List<QueryDocumentSnapshot> data) {
        teacherLeaves.clear();
        teacherLeavesCards.clear();
        for (QueryDocumentSnapshot document : data) {
            Leave leave = Leave.fromJSON(document.getData());
            Card card = new Card(leave.getTeacherName(), leave.getReason(), "", false);

            if (cardListener != null) {
                card.setListener(new CardListener() {
                    @Override
                    public void onCardClick() {
                        cardListener.onCardClick(leave);
                    }

                    @Override
                    public void onDeleteButtonClick() {

                    }

                    @Override
                    public void onEditButtonClick() {

                    }

                    @Override
                    public void onNotificationButtonClick() {

                    }

                    @Override
                    public void onContextMenuRequested(MouseEvent event) {
                        cardListener.onContextMenuRequested(leave, event);
                    }
                });
            }
            teacherLeaves.add(leave);
            teacherLeavesCards.add(card);
            teacherLeaveCardMapProperty.put(leave, card);
        }
    }

    public void updateLeave(Leave updatedLeave) {
        FirestoreConstants.teacherLeavesCollectionReference.document(updatedLeave.getId()).set(updatedLeave.toJSON());
    }


}


