package students.detail_view;

import custom_view.ImageButton;
import custom_view.loading_combobox.class_section_combobox.ClassSectionComboBox;
import custom_view.loading_combobox.batches.BatchLoadingComboBox;
import custom_view.loading_combobox.class_section_combobox.ClassSectionListener;
import javafx.beans.property.*;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.ClassItem;
import model.Section;
import model.Student;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class StudentDetailsController implements Initializable {


    public ImageButton profileImageView;
    public TextField nameField;
    public TextField emailField;
    public Button submitButton;
    public TextField parentNumberTextField;
    public TextField requestedRemarkTextField;

    public BatchLoadingComboBox batchComboBox;
    public ClassSectionComboBox classSectionComboBox;

    private Student student;
    private StudentListener listener;

    private StringProperty selectedClassName = new SimpleStringProperty();
    private StringProperty selectedBatch = new SimpleStringProperty();
    private StringProperty selectedSection = new SimpleStringProperty();

    private StringProperty selectedName = new SimpleStringProperty();
    private StringProperty selectedEmail = new SimpleStringProperty();
    private StringProperty selectedParentNumber = new SimpleStringProperty();
    private StringProperty selectedRemark = new SimpleStringProperty();
    private ObjectProperty<File> selectedProfilePicture = new SimpleObjectProperty<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        profileImageView.setListener(file -> selectedProfilePicture.set(file));
        nameField.textProperty().addListener((observableValue, s, t1) -> selectedName.set(t1));
        emailField.textProperty().addListener((observableValue, s, t1) -> selectedEmail.set(t1));
        parentNumberTextField.textProperty().addListener((observableValue, s, t1) -> selectedParentNumber.set(t1));
        requestedRemarkTextField.textProperty().addListener((observableValue, s, t1) -> selectedRemark.set(t1));


        batchComboBox.setListener(selctedItem -> selectedBatch.set(String.valueOf(selctedItem)));


        classSectionComboBox.setListener(new ClassSectionListener() {
            @Override
            public void onSectionSelected(Section section) {
                System.out.println("Section Selected: " + section);
                selectedSection.set(section.getSectionName());
            }

            @Override
            public void onClassSelected(ClassItem classItem) {
                System.out.println("Class Selected: " + classItem);
                selectedClassName.set(classItem.getName());
            }
        });

        submitButton.setOnAction(actionEvent -> {
            if (listener == null) return;

            listener.onStudentSubmit(new Student(
                    student.getID(),
                    student.getAdmissionID(),
                    selectedBatch.get(),
                    student.getAddress(),
                    student.getProfilePictureURL(),
                    selectedSection.get(),
                    student.getToken(),
                    selectedParentNumber.get(),
                    selectedRemark.get(),
                    selectedName.get(),
                    selectedClassName.get(),
                    selectedEmail.get(),
                    student.getSectionID(),
                    student.getScholarship(),
                    student.isRequested(),
                    student.isVerified(),
                    student.isProfileCompleted()
            ), selectedProfilePicture.get());
        });


    }


    public void setListener(StudentListener listener) {
        this.listener = listener;
    }


    private void updateStudentDetails(Student student) {
        if (student.getProfilePictureURL() != null) {
            profileImageView.setImage(student.getProfilePictureURL());
        }

        nameField.setText(student.getName());
        selectedName.set(student.getName());

        emailField.setText(student.getEmail());
        selectedEmail.set(student.getEmail());


        parentNumberTextField.setText(student.getParentNumber());
        selectedParentNumber.set(student.getParentNumber());

        requestedRemarkTextField.setText(student.getRequestedRemark());
        selectedRemark.set(student.getRequestedRemark());

        selectedClassName.set(student.getClassName());

        batchComboBox.setValue(student.getBatch(), false);
        selectedBatch.set(student.getBatch());

        classSectionComboBox.setClass(student.getClassName());
        selectedClassName.set(student.getClassName());

        classSectionComboBox.setSection(student.getSection());
        selectedSection.set(student.getSection());
    }

    public void setStudent(Student student) {
        this.student = student;
        updateStudentDetails(student);
    }
}
