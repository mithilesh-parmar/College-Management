package utility;

import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import custom_view.card_view.CardListener;
import custom_view.card_view.LeaveCard;
import custom_view.card_view.LeaveCardListener;
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

import java.util.List;

public class TeacherLeavesUtility {

    private static TeacherLeavesUtility instance;

    public ListProperty<LeaveCard> teacherLeavesCards;
    public ListProperty<LeaveCard> approvedLeavesCards;
    public ListProperty<LeaveCard> declinedLeavesCards;
    public ListProperty<LeaveCard> pendingLeavesCards;

    public ObservableList<Leave> teacherLeaves;
    private DataChangeListener listener;
    public MapProperty<Leave, LeaveCard> teacherLeaveCardMapProperty;
    private LeaveCardListener cardListener;

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

    public LeaveCardListener getCardListener() {
        return cardListener;
    }

    public void setCardListener(LeaveCardListener cardListener) {
        this.cardListener = cardListener;
    }

    private TeacherLeavesUtility() {
        teacherLeaves = FXCollections.observableArrayList();
        teacherLeavesCards = new SimpleListProperty<>(FXCollections.observableArrayList());
        teacherLeaveCardMapProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());
        approvedLeavesCards = new SimpleListProperty<>(FXCollections.observableArrayList());
        pendingLeavesCards = new SimpleListProperty<>(FXCollections.observableArrayList());
        declinedLeavesCards = new SimpleListProperty<>(FXCollections.observableArrayList());

    }


    private void parseTeacherLeavesData(List<QueryDocumentSnapshot> data) {
        teacherLeaves.clear();
        teacherLeavesCards.clear();
        approvedLeavesCards.clear();
        pendingLeavesCards.clear();
        declinedLeavesCards.clear();
        for (QueryDocumentSnapshot document : data) {
            Leave leave = Leave.fromJSON(document.getData());
            LeaveCard card = new LeaveCard(leave);

            card.setListener(cardListener);

            teacherLeaves.add(leave);
            teacherLeavesCards.add(card);
            if (leave.getStatus() == 0) {
                declinedLeavesCards.add(card);
            } else if (leave.getStatus() == 1) {
                approvedLeavesCards.add(card);
            } else if (leave.getStatus() == 2) {
                pendingLeavesCards.add(card);
            }
            teacherLeaveCardMapProperty.put(leave, card);
        }
    }

    public void updateLeave(Leave updatedLeave) {
        System.out.println("Updating leave: " + updatedLeave.toJSON());
        FirestoreConstants.teacherLeavesCollectionReference.document(updatedLeave.getId()).set(updatedLeave.toJSON());
    }


}


