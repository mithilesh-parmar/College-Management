package model;

import javafx.beans.property.*;

import java.util.HashMap;
import java.util.Map;

//Add fee logic
public class Student {
    private StringProperty address = new SimpleStringProperty(),
            ID = new SimpleStringProperty(),
            admissionID = new SimpleStringProperty(),
            profilePictureURL = new SimpleStringProperty(),
            section = new SimpleStringProperty(),
            token = new SimpleStringProperty(),
            parentNumber = new SimpleStringProperty(),
            requestedRemark = new SimpleStringProperty(),
            name = new SimpleStringProperty(),
            className = new SimpleStringProperty(),
            email = new SimpleStringProperty(),
            sectionID = new SimpleStringProperty(),
            batch = new SimpleStringProperty();

    private BooleanProperty
            requested = new SimpleBooleanProperty(),
            verified = new SimpleBooleanProperty(),
            profileCompleted = new SimpleBooleanProperty();

    private LongProperty scholarship = new SimpleLongProperty(), amountDue = new SimpleLongProperty(), totalAmount = new SimpleLongProperty();

    public Student(String ID,
                   String admissionID,
                   String batch,
                   String address,
                   String profilePictureURL,
                   String section,
                   String token,
                   String parentNumber,
                   String requestedRemark,
                   String name,
                   String className,
                   String email,
                   String sectionID,
                   long scholarship,
                   boolean requested,
                   boolean verified,
                   boolean profileCompleted
    ) {


        setAdmissionID(admissionID);
        setID(ID);
        setBatch(batch);
        setAddress(address);
        setProfilePictureURL(profilePictureURL);
        setSection(section);
        setToken(token);
        setParentNumber(parentNumber);
        setRequestedRemark(requestedRemark);
        setName(name);
        setClassName(className);
        setEmail(email);
        setScholarship(scholarship);
        setRequested(requested);
        setVerified(verified);
        setSectionID(sectionID);
//        setAmountDue(amountDue);
        setProfileCompleted(profileCompleted);
    }


    public static Student fromJSON(Map<String, Object> json) {
        return new Student(
                (String) json.get("id"),
                (String) json.get("admission_id"),
                json.containsKey("batch") ? (String) json.get("batch") : "Undefined",
                (String) json.get("address"),
                (String) json.get("pp_url"),
                (String) json.get("section"),
                (String) json.get("token"),
                (String) json.get("parent_number"),
                (String) json.get("requested_remark"),
                (String) json.get("name"),
                (String) json.get("class_name"),
                (String) json.get("email"),
                (String) json.get("section_id"),
                json.containsKey("scholarship") ? (long) json.get("scholarship") : 0L,
//                (long) json.get("amount_due"),
                (boolean) json.get("requested"),
                (boolean) json.get("verified"),
                (boolean) json.get("profile_completed")
        );
    }

    public Map<String, Object> toJSON() {
        Map<String, Object> json = new HashMap<>();
        json.put("id", ID.get());
        json.put("address", address.get());
        json.put("pp_url", profilePictureURL.get());
        json.put("section", section.get());
        json.put("token", token.get());
        json.put("parent_number", parentNumber.get());
        json.put("requested_remark", requestedRemark.get());
        json.put("name", name.get());
        json.put("class_name", className.get());
        json.put("email", email.get());
        json.put("requested", requested.get());
        json.put("verified", verified.get());
        json.put("profile_completed", profileCompleted.get());
        json.put("scholarship", scholarship.get());
        json.put("admission_id", admissionID.get());
        json.put("section_id", sectionID.get());
        json.put("batch", batch.get());
//        json.put("amount_due", amountDue.get());
        return json;
    }

    public String getSectionID() {
        return sectionID.get();
    }

    public StringProperty sectionIDProperty() {
        return sectionID;
    }

    public void setSectionID(String sectionID) {
        this.sectionID.set(sectionID);
    }

    public String getAdmissionID() {
        return admissionID.get();
    }

    public StringProperty admissionIDProperty() {
        return admissionID;
    }

    public void setAdmissionID(String admissionID) {
        this.admissionID.set(admissionID);
    }

    public long getAmountDue() {
        return amountDue.get();
    }

    public LongProperty amountDueProperty() {
        return amountDue;
    }

    public void setAmountDue(long amountDue) {
        this.amountDue.set(amountDue);
    }

    public long getScholarship() {
        return scholarship.get();
    }

    public LongProperty scholarshipProperty() {
        return scholarship;
    }

    public void setScholarship(long scholarship) {
        this.scholarship.set(scholarship);
    }

    public String getBatch() {
        return batch.get();
    }

    public StringProperty batchProperty() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch.set(batch);
    }

    public String getID() {
        return ID.get();
    }

    public StringProperty IDProperty() {
        return ID;
    }

    public void setID(String ID) {
        this.ID.set(ID);
    }

    public String getAddress() {
        return address.get();
    }

    public StringProperty addressProperty() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public String getProfilePictureURL() {
        return profilePictureURL.get();
    }

    public StringProperty profilePictureURLProperty() {
        return profilePictureURL;
    }

    public void setProfilePictureURL(String profilePictureURL) {
        this.profilePictureURL.set(profilePictureURL);
    }

    public String getSection() {
        return section.get();
    }

    public StringProperty sectionProperty() {
        return section;
    }

    public void setSection(String section) {
        this.section.set(section);
    }

    public String getToken() {
        return token.get();
    }

    public StringProperty tokenProperty() {
        return token;
    }

    public void setToken(String token) {
        this.token.set(token);
    }

    public String getParentNumber() {
        return parentNumber.get();
    }

    public StringProperty parentNumberProperty() {
        return parentNumber;
    }

    public void setParentNumber(String parentNumber) {
        this.parentNumber.set(parentNumber);
    }

    public String getRequestedRemark() {
        return requestedRemark == null ? " " : requestedRemark.get();
    }

    public StringProperty requestedRemarkProperty() {
        return requestedRemark;
    }

    public void setRequestedRemark(String requestedRemark) {
        this.requestedRemark.set(requestedRemark);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getClassName() {
        return className.get();
    }

    public StringProperty classNameProperty() {
        return className;
    }

    public void setClassName(String className) {
        this.className.set(className);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public boolean isRequested() {
        return requested.get();
    }

    public BooleanProperty requestedProperty() {
        return requested;
    }

    public void setRequested(boolean requested) {
        this.requested.set(requested);
    }

    public boolean isVerified() {
        return verified.get();
    }

    public BooleanProperty verifiedProperty() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified.set(verified);
    }

    public boolean isProfileCompleted() {
        return profileCompleted.get();
    }

    public BooleanProperty profileCompletedProperty() {
        return profileCompleted;
    }

    public void setProfileCompleted(boolean profileCompleted) {
        this.profileCompleted.set(profileCompleted);
    }

    @Override
    public String toString() {
        return name.getValue() + "\t" + section.getValue();
    }

    public String getNameWithoutSpaces() {
        String studentName = name.get();

        studentName = studentName.replace(" ", "_");

        return studentName;
    }
}
