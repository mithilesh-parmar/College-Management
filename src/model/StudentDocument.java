package model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import utility.DocumentType;

import java.io.File;

public class StudentDocument {

    private ObjectProperty<File> file;
    private StringProperty fileLink;
    private StringProperty cloudStoragePath;
    private StringProperty studentAdmissionNumber;
    private StringProperty fileType;
    private StringProperty fileName;


    public StudentDocument(File file, String cloudStoragePath, String studentAdmissionNumber, String fileName, String documentType, String fileLink) {
        this.file = new SimpleObjectProperty<>(file);
        this.fileLink = new SimpleStringProperty(fileLink);
        this.fileName = new SimpleStringProperty(fileName);
        this.cloudStoragePath = new SimpleStringProperty(cloudStoragePath);
        this.studentAdmissionNumber = new SimpleStringProperty(studentAdmissionNumber);
        this.fileType = new SimpleStringProperty(documentType);
    }

    public String getFileName() {
        return fileName.get();
    }

    public StringProperty fileNameProperty() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    public String getFileType() {
        return fileType.get();
    }

    public String getFileLink() {
        return fileLink.get();
    }

    public StringProperty fileLinkProperty() {
        return fileLink;
    }

    public void setFileLink(String fileLink) {
        this.fileLink.set(fileLink);
    }

    public StringProperty fileTypeProperty() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType.set(fileType);
    }

    public File getFile() {
        return file.get();
    }

    public ObjectProperty<File> fileProperty() {
        return file;
    }

    public void setFile(File file) {
        this.file.set(file);
    }

    public String getCloudStoragePath() {
        return cloudStoragePath.get();
    }

    public StringProperty cloudStoragePathProperty() {
        return cloudStoragePath;
    }

    public void setCloudStoragePath(String cloudStoragePath) {
        this.cloudStoragePath.set(cloudStoragePath);
    }

    public String getStudentAdmissionNumber() {
        return studentAdmissionNumber.get();
    }

    public StringProperty studentAdmissionNumberProperty() {
        return studentAdmissionNumber;
    }

    public void setStudentAdmissionNumber(String studentAdmissionNumber) {
        this.studentAdmissionNumber.set(studentAdmissionNumber);
    }
}
