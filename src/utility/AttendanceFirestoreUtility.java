package utility;

import attendance.AttendanceViewCardListener;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import custom_view.card_view.AttendanceCard;
import custom_view.card_view.CardListener;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.util.Pair;
import listeners.DataChangeListener;
import model.Batch;
import model.ClassItem;
import model.Section;
import model.SectionAttendance;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

                AttendanceCard card = new AttendanceCard(item);
                card.setCardListener(new CardListener() {
                    @Override
                    public void onCardClick() {
                        cardListener.onCardClick(item);
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

                    }
                });
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

    public ObservableList getSubjectAttendance(Section section, String subject) {
        List<AttendanceCard> collect = attendanceCards
                .stream()
                .filter(attendanceCard -> attendanceCard.getAttendance().getSectionName().toUpperCase().contains(section.getSectionName().toUpperCase()))
                .filter(attendanceCard -> attendanceCard.getAttendance().getSubject().toUpperCase().contains(subject.toUpperCase()))
                .collect(Collectors.toList());
        return FXCollections.observableArrayList(collect);
    }


    public ObservableList getSectionAttendance(ClassItem classItem, Section section) {
        List<AttendanceCard> collect = attendanceCards
                .stream()
                .filter(attendanceCard -> attendanceCard.getAttendance().getSectionName().toUpperCase().contains(section.getSectionName().toUpperCase()))
                .filter(attendanceCard -> attendanceCard.getAttendance().getClassName().toUpperCase().contains(classItem.getName().toUpperCase()))
                .collect(Collectors.toList());
        return FXCollections.observableArrayList(collect);
    }

    public ObservableList getBatchAttendance(Batch batch) {
        List<AttendanceCard> collect = attendanceCards
                .stream()
                .filter(attendanceCard -> attendanceCard.getAttendance().getBatch().toUpperCase().contains(batch.getName().toUpperCase()))
                .collect(Collectors.toList());
        return FXCollections.observableArrayList(collect);

    }
}
