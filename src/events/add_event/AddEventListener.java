package events.add_event;

import javafx.stage.Stage;
import model.Event;
import org.apache.poi.ss.formula.functions.Even;

public interface AddEventListener {
    void onEventAdded(Event event);

    void onEventUpdated(Event updatedEvent);

    default void close(Stage stage) {
        stage.close();
    }

}
