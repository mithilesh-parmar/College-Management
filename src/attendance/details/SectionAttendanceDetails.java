package attendance.details;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import model.SectionAttendance;
import model.StudentSectionAttendance;

import java.net.URL;
import java.util.ResourceBundle;

public class SectionAttendanceDetails implements Initializable {

    public TableView<StudentSectionAttendance> attendanceTable;
    public ProgressIndicator progressIndicator;
    public Label classNameLabel;
    public Label sectionNameLabel;
    public Label dateLabel;
    public Label lectureLabel;
    public Label batchLabel;
    private ObjectProperty<SectionAttendance> sectionAttendance = new SimpleObjectProperty<>();
    private ListProperty<StudentSectionAttendance> sectionAttendances = new SimpleListProperty<>(FXCollections.observableArrayList());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        attendanceTable.itemsProperty().bind(sectionAttendances);

    }

    public void setSectionAttendance(SectionAttendance sectionAttendance) {
        this.sectionAttendance.set(sectionAttendance);

        sectionAttendance.getLectureAttendance().forEach(stringObjectMap -> {
            sectionAttendances.get().add(StudentSectionAttendance.fromJSON(stringObjectMap));
        });
        classNameLabel.setText(sectionAttendance.getClassName());
        sectionNameLabel.setText(sectionAttendance.getSectionName());
        dateLabel.setText(sectionAttendance.getDate());
        lectureLabel.setText(sectionAttendance.getSubject());
        batchLabel.setText(sectionAttendance.getBatch());
        progressIndicator.setVisible(false);
    }
}
