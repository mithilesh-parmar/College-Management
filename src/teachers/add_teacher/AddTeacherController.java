package teachers.add_teacher;

import custom_view.ImageButton;
import javafx.beans.property.*;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import model.Teacher;
import teachers.TeacherDetailViewListener;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class AddTeacherController implements Initializable {


    public Button submitButton;
    public TextField nameField;
    public TextField emailField;
    public ImageButton profileImageView;
    public ComboBox<Boolean> verifiedComboBox;
    public ComboBox<Boolean> profileCompleteComboBox;
    public HBox comboBoxView;


    private TeacherDetailViewListener listener;
//    private File profileImageFile;

    private Teacher teacher;


    private BooleanProperty selectedVerifiedValue = new SimpleBooleanProperty();
    private BooleanProperty selectedProfileCompleteValue = new SimpleBooleanProperty();
    private StringProperty selectedNameValue = new SimpleStringProperty();
    private StringProperty selectedEmailValue = new SimpleStringProperty();
    private ObjectProperty<File> selectedProfileValue = new SimpleObjectProperty<>();

    private BooleanProperty canSubmit = new SimpleBooleanProperty(false);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        verifiedComboBox.getItems().addAll(true, false);
        profileCompleteComboBox.getItems().addAll(true, false);

        comboBoxView.setSpacing(15);

        submitButton.visibleProperty().bind(canSubmit);

        profileImageView.setListener(file -> {
            if (file == null) return;
            selectedProfileValue.set(file);
            checkCanSubmit();
        });

        verifiedComboBox.valueProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1 == null) return;
            selectedVerifiedValue.set(t1);
            checkCanSubmit();
        });

        profileCompleteComboBox.valueProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1 == null) return;
            selectedProfileCompleteValue.set(t1);
            checkCanSubmit();
        });

        nameField.textProperty().addListener((observableValue, s, t1) -> {
            if (t1.isEmpty()) return;
            selectedNameValue.set(t1);
            checkCanSubmit();
        });

        emailField.textProperty().addListener((observableValue, s, t1) -> {
            if (t1.isEmpty()) return;
            selectedEmailValue.set(t1);
            checkCanSubmit();
        });

        submitButton.setOnAction(actionEvent -> submitTeacher());

    }

    private void submitTeacher() {
        if (listener == null || teacher == null) return;

        listener.onTeacherSubmit(new Teacher(
                this.teacher.getID(),
                selectedNameValue.get(),
                selectedEmailValue.get(),
                this.teacher.getProfilePictureUrl(), // this value is ususally empty if there's a prev url else we first specify prev url and pass the new file to listener where first the image is uploaded and the url will be updated
                this.teacher.getToken(),
                this.teacher.getVerificationCode(),
                selectedVerifiedValue.get(),
                selectedProfileCompleteValue.get()
        ), selectedProfileValue.get());

    }

    private void checkCanSubmit() {

        canSubmit.set(
                selectedEmailValue.get() != null && !selectedEmailValue.get().isEmpty()
                        && selectedNameValue.get() != null && !selectedNameValue.get().isEmpty()

        );
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
        fillTeacherData();
    }

    private void fillTeacherData() {

        nameField.setText(teacher.getName());
        selectedNameValue.set(teacher.getName());

        emailField.setText(teacher.getEmail());
        selectedEmailValue.set(teacher.getName());


        if (teacher.getProfilePictureUrl() != null)
            profileImageView.setImage(teacher.getProfilePictureUrl());

        selectedProfileCompleteValue.set(teacher.isProfileCompleted());
        profileCompleteComboBox.setValue(teacher.isProfileCompleted());

        selectedVerifiedValue.set(teacher.isVerified());
        verifiedComboBox.setValue(teacher.isVerified());

        checkCanSubmit();
    }

    public void setListener(TeacherDetailViewListener listener) {
        this.listener = listener;
    }
}
