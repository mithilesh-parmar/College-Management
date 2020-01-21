package utility;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
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

    public void processAttendanceSheet(File file,
                                       String className,
                                       String subject,
                                       Date date,
                                       String year,
                                       String batch) {
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
//                    lectureAttendance.add(getStudentAttendanceMap(present, admission_id));

//                Add student attendance to firestore
//                    addStudentAttendanceData(
//                            sheet.getSheetName(),
//                            admission_id,
//                            Timestamp.now().toString(),
//                            present.toUpperCase().equals("P")
//                    );
                    Map<String, Object> studentMap = addStudentAttendanceData(
                            subject,
                            className,
                            year,
                            admission_id,
                            date,
                            present.toUpperCase().equals("P")
                    );
                    System.out.println("Studdent map in section " + studentMap);
                    if (studentMap.size() > 0) {
                        System.out.println("adding to lecture " + studentMap);
                        lectureAttendance.add(studentMap);
                    }


                }
//            removing first column {contains student id - present} it is the header
//                lectureAttendance.get(subject.getName()).remove(0);
//                lectureAttendance.remove(0);

            }

//        System.out.println(lectureAttendance);
            addAttendanceForSection(lectureAttendance, className, subject, date, year, batch);
        } catch (IOException | ExecutionException | InterruptedException | InvalidFormatException e) {

            if (listener != null) listener.onAttendanceUploadError();
            e.printStackTrace();
        }
    }


    private Map<String, Object> addStudentAttendanceData(
            String subject,
            String className,
            String section,
            String admission_id,
            Date date,
            boolean present)
            throws ExecutionException, InterruptedException {

        System.out.println("Adding student data for subject: " + subject +
                " course: " + className +
                " year: " + section +
                " admission_id: " + admission_id +
                " date: " + date);

        Map<String, Object> studentData = new HashMap<>();

        Query studentRef = FirestoreConstants
                .studentCollectionReference
                .whereEqualTo("admission_id", admission_id)
                .whereEqualTo("section", section)
                .whereEqualTo("class_name", className);

        studentRef.get().get().getDocuments().forEach(queryDocumentSnapshot -> {
            System.out.println("Query Document: " + queryDocumentSnapshot.getData());
            Student student = Student.fromJSON(queryDocumentSnapshot.getData());

            System.out.println("Student Data Found: " + student.toJSON());
            studentData.put("admission_id", student.getID());
            studentData.put("email", student.getEmail());
            studentData.put("pp_url", student.getProfilePictureURL());
            studentData.put("stud_id", student.getID());
            studentData.put("present", present);

            System.out.println(studentData);
//            write data to firestore
            HashMap<String, Object> lectureData = new HashMap<>();
            lectureData.put("date", date);
            lectureData.put("lecture", subject);
            lectureData.put("present", present);
            ApiFuture<DocumentReference> attendance = queryDocumentSnapshot
                    .getReference()
                    .collection("attendance")
                    .add(lectureData);
            if (attendance.isDone() && listener != null) listener.onAttendanceUploadFinish();


        });

        return studentData;
    }

    private void addAttendanceForSection(List<Map<String, Object>> lectureAttendance, String className, String subject, Date date, String year, String batch) {
        Map<String, Object> json = new HashMap<>();
        json.put("class_name", className);
        json.put("subject", subject);
        json.put("date", DateUtility.dateToStringForFirestore(date));
        json.put("date_unix", Timestamp.of(date));
        json.put("section", year);
        json.put("batch", batch);
        json.put("lecture_attendance", lectureAttendance);
        ApiFuture<DocumentReference> add = FirestoreConstants.sectionAttendanceCollectionReference.add(json);
        if (add.isDone() && listener != null) listener.onAttendanceUploadFinish();

    }

//    private void addAttendanceForSection(Map<String, List<Map<String, Object>>> lectureAttendance, Course course, Subject subject, Date date, int year) {
//        Map<String, Object> json = new HashMap<>();
//        json.put("course", course.getName());
//        json.put("subject", subject.getName());
//        json.put("date", date);
//        json.put("date_unix", Timestamp.of(date));
//        json.put("year", year);
//        json.put("lecture_attendance", lectureAttendance);
//        ApiFuture<DocumentReference> add = FirestoreConstants.sectionAttendanceCollectionReference.add(json);
//        if (add.isDone() && listener != null) listener.onAttendanceUploadFinish();
//
//    }


    public void setListener(AttendanceListener listener) {
        this.listener = listener;
    }
}
