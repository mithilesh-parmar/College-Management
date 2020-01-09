package utility;

import Attendance.AttendanceViewCardListener;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import custom_view.card_view.AttendanceCard;
import custom_view.card_view.AttendanceCardListener;
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
import model.Section;
import model.SectionAttendance;
import model.Teacher;

import java.util.List;

public class AttendanceFirestoreUtility {


    private static AttendanceFirestoreUtility instance;

    private AttendanceViewCardListener cardListener;

    public void setListener(DataChangeListener listener) {
        this.listener = listener;
    }

    private DataChangeListener listener;
    public ObservableList<SectionAttendance> sectionAttendances;
    public MapProperty<SectionAttendance, AttendanceCard> sectionAttendanceCardMapProperty;
    public ListProperty<AttendanceCard> attendanceCards;

    private EventListener<QuerySnapshot> attendanceListener = (snapshot, e) -> {
        if (e != null) {
            System.err.println("Listen failed: " + e);
            if (listener != null) listener.onError(new Exception("Listen failed: " + e));
            return;
        }
        if (snapshot != null && !snapshot.isEmpty()) {
            Platform.runLater(() -> {
                if (listener != null) listener.onDataChange(snapshot);
                System.out.println("Encountered Changes");
                parseSectionAttendanceData(snapshot.getDocuments());
                if (listener != null) listener.onDataLoaded(sectionAttendances);
            });
        } else {
            System.out.print("Current data: null");
            if (listener != null) listener.onError(new Exception("Current data is null"));
        }
    };

    private AttendanceFirestoreUtility() {
        sectionAttendances = FXCollections.observableArrayList();
        sectionAttendanceCardMapProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());
        attendanceCards = new SimpleListProperty<>(FXCollections.observableArrayList());

    }

    public void getAttendance() {
        if (sectionAttendances.size() > 0) listener.onDataLoaded(sectionAttendances);
        else FirestoreConstants
                .sectionAttendanceCollectionReference
                .addSnapshotListener(attendanceListener);

    }

    private void parseSectionAttendanceData(List<QueryDocumentSnapshot> data) {
        sectionAttendances.clear();
        attendanceCards.clear();
        for (QueryDocumentSnapshot document : data) {
            if (document.getData().containsKey("lecture_attendance")) {
                System.out.println(document.getData());
                SectionAttendance item = SectionAttendance.fromJSON(document.getData());

                AttendanceCard card = new AttendanceCard(
                        item.getClassName(),
                        item.getSection(),
                        item.getBatch(),
                        item.getDate(),
                        item.getDateUnix().toString()
                );

                if (cardListener != null) {
                    card.setListener(() -> {
                        cardListener.onCardClick(item);
                    });
                }

                sectionAttendances.add(item);
                attendanceCards.add(card);
                sectionAttendanceCardMapProperty.put(item, card);
            }


        }
    }


    public void setCardListener(AttendanceViewCardListener cardListener) {
        this.cardListener = cardListener;
    }

    public static AttendanceFirestoreUtility getInstance() {
        if (instance == null) synchronized (AttendanceFirestoreUtility.class) {
            instance = new AttendanceFirestoreUtility();
        }
        return instance;
    }


}
