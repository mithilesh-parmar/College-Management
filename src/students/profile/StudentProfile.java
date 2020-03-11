package students.profile;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.Fee;
import model.Student;
import students.profile.attendance.AttendanceController;
import students.profile.back_logs.StudentBackLogController;
import students.profile.detail_view.StudentDetailsController;
import students.profile.detail_view.StudentListener;
import students.profile.fee_view.StudentFeeCallback;
import students.profile.fee_view.StudentFeeController;
import utility.StudentVerificationUtility;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class StudentProfile implements Initializable {


    public TabPane tabs;
    public ProgressIndicator progressIndicator;
    @FXML
    private Tab profileTab, attendanceTab, feeTab, backlogTab;

    private StudentProfileCallback profileCallback;

    private final String DETAIL_VIEW = "/students/profile/detail_view/StudentDetailView.fxml",
            ATTENDANCE_VIEW = "/students/profile/attendance/AttendanceView.fxml",
            FEE_VIEW = "/students/profile/fee_view/StudentFeeView.fxml",
            BACKLOG_VIEW = "/students/profile/back_logs/StudentBackLogView.fxml";

    private ObjectProperty<Student> student = new SimpleObjectProperty<>();
    private StudentDetailsController detailsController;
    private AttendanceController attendanceController;
    private StudentFeeController feeController;
    private StudentBackLogController backLogController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        progressIndicator.setVisible(false);

        student.addListener((observableValue, student1, t1) -> {
            if (t1 == null) return;
            if (detailsController != null) {
                detailsController.setStudent(t1);
            }
            if (attendanceController != null) {
                attendanceController.setStudent(t1);
            }
            if (feeController != null) {
                feeController.setStudent(t1);
            }
            if (backLogController != null) {
                backLogController.setStudent(t1);
            }
        });

        detailsController = (StudentDetailsController) loadView(DETAIL_VIEW, profileTab);
        if (detailsController != null)
            detailsController.setListener(new StudentListener() {
                @Override
                public void onStudentSubmit(Student student, File profileImage) {
                    if (profileCallback == null) return;

                    Platform.runLater(() -> checkIfStudentExist(student, profileImage));
                }

                @Override
                public void onStudentEdit(Student student, File profileImage) {
                    if (profileCallback == null) return;
                    profileCallback.onStudentEdit(student, profileImage);
                }

            });
        attendanceController = (AttendanceController) loadView(ATTENDANCE_VIEW, attendanceTab);
//        TODO fee window should close on submit
//       TODO individual fee window add operation not working
        feeController = (StudentFeeController) loadView(FEE_VIEW, feeTab);
        if (feeController != null) {

            feeController.setCallback(fee -> {
                if (profileCallback == null) return;
                profileCallback.onStudentFeeAdded(fee);
            });
        }
        backLogController = (StudentBackLogController) loadView(BACKLOG_VIEW, backlogTab);
        if (backLogController != null) {
            backLogController.setStudent(student.get());
        }
    }

    private void checkIfStudentExist(Student student, File profileImage) {

        StudentVerificationUtility.Callback callback = new StudentVerificationUtility.Callback() {
            @Override
            public void onSuccess(Student student) {
//                if student exists then show error
                progressIndicator.setVisible(false);
//                show error
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Student Exists with this Admission ID");
                alert.showAndWait();

            }

            @Override
            public void onFailure() {
//                If no student with this admission id exist then add student
                profileCallback.onStudentSubmit(student, profileImage);
                progressIndicator.setVisible(false);
            }

            @Override
            public void onStart() {
                progressIndicator.setVisible(true);
            }
        };

        try {
            StudentVerificationUtility.exist(student.getAdmissionID(), callback);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


    }


    private Object loadView(String path, Tab tab) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        try {
            tab.setContent(loader.load());
            tab.setStyle("/styles/dark_metro_style.css");
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void setCallback(StudentProfileCallback profileCallback) {
        this.profileCallback = profileCallback;
    }

    public void setStudent(Student student) {
        this.student.set(student);
    }

}
