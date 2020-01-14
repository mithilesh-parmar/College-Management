package utility;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Query;
import model.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.ExecutionException;


public class ExcelSheetUtility {

    private AttendanceListener listener;

    public void processAttendanceSheet(File file, Course course, String subject, Date date, int year) {
        System.out.println("Processing sheet");
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
//            Map<String, List<Map<String, Object>>> lectureAttendance = new HashMap<>();

            List<Map<String, Object>> lectureAttendance = new ArrayList<>();


            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
//            sheet name contains the subject name
                Sheet sheet = workbook.getSheetAt(i);
//            subject name with empty arraylist
//                lectureAttendance.put(subject.getName(), new ArrayList<>());

                for (Row row : sheet) {
//                add map of student data to that particular subject
                    DataFormatter dataFormatter = new DataFormatter();
                    String present = dataFormatter.formatCellValue(row.getCell(1));
                    String admission_id = dataFormatter.formatCellValue(row.getCell(0));
//                    lectureAttendance.get(subject.getName()).add(getStudentAttendanceMap(present, admission_id));
                    lectureAttendance.add(getStudentAttendanceMap(present, admission_id));

//                Add student attendance to firestore
//                    addStudentAttendanceData(
//                            sheet.getSheetName(),
//                            admission_id,
//                            Timestamp.now().toString(),
//                            present.toUpperCase().equals("P")
//                    );

                    addStudentAttendanceData(
                            subject,
                            course,
                            year,
                            admission_id,
                            date,
                            present.toUpperCase().equals("P")
                    );

                }
//            removing first column {contains student id - present} it is the header
//                lectureAttendance.get(subject.getName()).remove(0);
                lectureAttendance.remove(0);

            }

//        System.out.println(lectureAttendance);
            addAttendanceForSection(lectureAttendance, course, subject, date, year);
        } catch (IOException | ExecutionException | InterruptedException | InvalidFormatException e) {

            if (listener != null) listener.onAttendanceUploadError();
            e.printStackTrace();
        }
    }


    private Map<String, Object> getStudentAttendanceMap(String present, String admissionId) {
//        This method is for attendance for whole section
        HashMap<String, Object> lectureData = new HashMap<>();
        lectureData.put("present", present);
        lectureData.put("admission_id", admissionId);

        return lectureData;
    }

    private void addStudentAttendanceData(
            String subject,
            Course course,
            int year,
            String admission_id,
            Date date,
            boolean present)
            throws ExecutionException, InterruptedException {

        System.out.println("Adding student data for subject: " + subject +
                " course: " + course +
                " year: " + year +
                " admission_id: " + admission_id +
                " date: " + date);

        Query studentRef = FirestoreConstants
                .studentCollectionReference
                .whereEqualTo("admission_id", admission_id)
                .whereEqualTo("year", year)
                .whereEqualTo("course", course.getName());

        studentRef.get().get().getDocuments().forEach(queryDocumentSnapshot -> {
            System.out.println(queryDocumentSnapshot.getData());
            HashMap<String, Object> lectureData = new HashMap<>();
            lectureData.put("date", date);
            lectureData.put("lecture", subject);
            lectureData.put("present", present);
//            write data to firestore
            ApiFuture<DocumentReference> attendance = queryDocumentSnapshot
                    .getReference()
                    .collection("attendance")
                    .add(lectureData);
            if (attendance.isDone() && listener != null) listener.onAttendanceUploadFinish();

        });

    }

    private void addAttendanceForSection(List<Map<String, Object>> lectureAttendance, Course course, String subject, Date date, int year) {
        Map<String, Object> json = new HashMap<>();
        json.put("course", course.getName());
        json.put("subject", subject);
        json.put("date", date);
        json.put("date_unix", Timestamp.of(date));
        json.put("year", year);
        json.put("lecture_attendance", lectureAttendance);
        ApiFuture<DocumentReference> add = FirestoreConstants.sectionAttendanceCollectionReference.add(json);
        if (add.isDone() && listener != null) listener.onAttendanceUploadFinish();

    }

    private void addAttendanceForSection(Map<String, List<Map<String, Object>>> lectureAttendance, Course course, Subject subject, Date date, int year) {
        Map<String, Object> json = new HashMap<>();
        json.put("course", course.getName());
        json.put("subject", subject.getName());
        json.put("date", date);
        json.put("date_unix", Timestamp.of(date));
        json.put("year", year);
        json.put("lecture_attendance", lectureAttendance);
        ApiFuture<DocumentReference> add = FirestoreConstants.sectionAttendanceCollectionReference.add(json);
        if (add.isDone() && listener != null) listener.onAttendanceUploadFinish();

    }


    public void setListener(AttendanceListener listener) {
        this.listener = listener;
    }
}
