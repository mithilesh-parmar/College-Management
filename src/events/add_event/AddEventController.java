package events.add_event;

import com.google.cloud.Timestamp;
import custom_view.image_gallery_view.Gallery;
import custom_view.time_textfield.TimeTextField;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import model.Event;

import java.net.URL;

import java.sql.Time;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class AddEventController implements Initializable {
    public TextField titleTextField;
    public TextField descriptionTextField;
    public Button submitButton;
    public DatePicker eventDatePicker;
    public DatePicker createdAtDatePicker;
    public TimeTextField eventTimeTextField;
    public Gallery galleryView;

    private AddEventListener listener;

    private Event event;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        galleryView.setShowAddButton(true);

        submitButton.setOnAction(actionEvent -> {


            if (listener == null) return;


            if (event == null) {
                System.out.println("Adding new event");
//            Initially setting id to null
                listener.onEventAdded(new Event(
                        "",
                        titleTextField.getText(),
                        descriptionTextField.getText(),
                        eventTimeTextField.getTime(),
                        asTimeStamp(createdAtDatePicker.getValue()),
                        asTimeStamp(eventDatePicker.getValue()),
                        galleryView.getImageUrls()
                ));
            } else {
                //                Updating the values with new value while keeping the id same
                System.out.println(galleryView.getImageUrls());
                listener.onEventUpdated(
                        new Event(
                                event.getId(),
                                titleTextField.getText(),
                                descriptionTextField.getText(),
                                eventTimeTextField.getTime(),
                                asTimeStamp(createdAtDatePicker.getValue()),
                                asTimeStamp(eventDatePicker.getValue()),
                                galleryView.getImageUrls()
                        )
                );
            }


        });
    }

    public void setEvent(Event event) {
        this.event = event;
        loadData(event);
    }

    private void loadData(Event event) {
        System.out.println(event.toJSON());
        titleTextField.setText(event.getTitle());
        descriptionTextField.setText(event.getDescription());
        galleryView.setImageView(event.getImages());
        System.out.println(event.getTime());
        eventTimeTextField.setText(event.getTime());
        createdAtDatePicker.setValue(asLocalDate(event.getCreatedAt().toDate()));
        eventDatePicker.setValue(asLocalDate(event.getEventDate().toDate()));
    }

    private static LocalDate asLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private Timestamp asTimeStamp(LocalDate localDate) {
        return Timestamp.of(Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
    }

    public void setListener(AddEventListener listener) {
        this.listener = listener;
    }
}

