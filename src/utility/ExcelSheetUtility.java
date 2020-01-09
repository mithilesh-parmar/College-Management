package utility;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Query;
import model.Batch;
import model.Section;
import model.StudentClass;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;


public class ExcelSheetUtility {

    private AttendanceListener listener;

    public void processAttendanceSheet(File file, Batch batch, StudentClass studentClass, Section section) {
        try {


//        Read the excel workbook
            Workbook workbook = new XSSFWorkbook(file);
            if (listener != null) listener.onAttendanceUploadStart();
        /*
           Example
                   contains SubjectName->[
                   {present:true,student_id:213,lecture:English},
                   {present:true,student_id:435,lecture:Maths},
                   {present:true,student_id:657,lecture:Hindi}
                   ]
         */
            Map<String, List<Map<String, Object>>> lectureAttendance = new HashMap<>();


            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
//            sheet name contains the subject name
                Sheet sheet = workbook.getSheetAt(i);
//            subject name with empty arraylist
                lectureAttendance.put(sheet.getSheetName(), new ArrayList<>());
                for (Row row : sheet) {
//                add map of student data to that particular subject
                    DataFormatter dataFormatter = new DataFormatter();
                    String present = dataFormatter.formatCellValue(row.getCell(1));
                    String admission_id = dataFormatter.formatCellValue(row.getCell(0));
                    lectureAttendance.get(sheet.getSheetName()).add(addAttendanceForStudent(present, admission_id));

//                Add student attendance to firestore
                    addStudentAttendanceData(
                            sheet.getSheetName(),
                            admission_id,
                            Timestamp.now().toString(),
                            present.toUpperCase().equals("P")
                    );

                }
//            removing first column {contains student id - present} it is the header
                lectureAttendance.get(sheet.getSheetName()).remove(0);

            }

//        System.out.println(lectureAttendance);
            addAttendanceForSection(lectureAttendance, batch, studentClass, section);
        } catch (IOException | ExecutionException | InterruptedException | InvalidFormatException e) {

            if (listener != null) listener.onAttendanceUploadError();
            e.printStackTrace();
        }
    }


    private Map<String, Object> addAttendanceForStudent(String present, String admissionId) {
//        This method is for attendance for whole section
        HashMap<String, Object> lectureData = new HashMap<>();
        lectureData.put("present", present);
        lectureData.put("admssion_id", admissionId);

        return lectureData;
    }

    private void addStudentAttendanceData(String subjectName, String admissionId, String date, boolean present)
            throws ExecutionException, InterruptedException {

        Query studentRef = FirestoreConstants.studentCollectionReference.whereEqualTo("admission_id", admissionId);

        studentRef.get().get().getDocuments().forEach(queryDocumentSnapshot -> {
//            System.out.println(queryDocumentSnapshot.getData());
            HashMap<String, Object> lectureData = new HashMap<>();
            lectureData.put("date", date);
            lectureData.put("lecture", subjectName);
            lectureData.put("present", present);
//            write data to firestore
            ApiFuture<DocumentReference> attendance = queryDocumentSnapshot.getReference().collection("attendance").add(lectureData);
            if (attendance.isDone() && listener != null) listener.onAttendanceUploadFinish();

        });

    }

    private void addAttendanceForSection(Map<String, List<Map<String, Object>>> lectureAttendance, Batch batch, StudentClass studentClass, Section section) {
        Map<String, Object> json = new HashMap<>();
        json.put("batch", batch.getName());
        json.put("class_name", studentClass.getClassname());
        json.put("date", Timestamp.now());
        json.put("date_unix", Timestamp.now());
        json.put("section", section.getName());
        json.put("lecture_attendance", lectureAttendance);
        ApiFuture<DocumentReference> add = FirestoreConstants.sectionAttendanceCollectionReference.add(json);
        if (add.isDone() && listener != null) listener.onAttendanceUploadFinish();

    }


    public void setListener(AttendanceListener listener) {
        this.listener = listener;
    }
}
