package model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Map;

public class Student {
    private StringProperty address = new SimpleStringProperty(),
            profilePictureURL = new SimpleStringProperty(),
            section = new SimpleStringProperty(),
            token = new SimpleStringProperty(),
            parentNumber = new SimpleStringProperty(),
            requestedRemark = new SimpleStringProperty(),
            name = new SimpleStringProperty(),
            className = new SimpleStringProperty(),
            email = new SimpleStringProperty();
    private SimpleBooleanProperty
            requested = new SimpleBooleanProperty(),
            verified = new SimpleBooleanProperty(),
            profileCompleted = new SimpleBooleanProperty();

    public Student(String address,
                   String profilePictureURL,
                   String section,
                   String token,
                   String parentNumber,
                   String requestedRemark,
                   String name,
                   String className,
                   String email,
                   boolean requested,
                   boolean verified,
                   boolean profileCompleted
    ) {


        setAddress(address);
        setProfilePictureURL(profilePictureURL);
        setSection(section);
        setToken(token);
        setParentNumber(parentNumber);
        setRequestedRemark(requestedRemark);
        setName(name);
        setClassName(className);
        setEmail(email);
        setRequested(requested);
        setVerified(verified);
        setProfileCompleted(profileCompleted);
    }

    public static Student fromJSON(Map<String, Object> json) {
        return new Student(
                (String) json.get("address"),
                (String) json.get("pp_url"),
                (String) json.get("section"),
                (String) json.get("token"),
                (String) json.get("parent_number"),
                (String) json.get("requested_remark"),
                (String) json.get("name"),
                (String) json.get("class_name"),
                (String) json.get("email"),
                (boolean) json.get("requested"),
                (boolean) json.get("verified"),
                (boolean) json.get("profile_completed")
        );
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
        return requestedRemark.get();
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

    public SimpleBooleanProperty requestedProperty() {
        return requested;
    }

    public void setRequested(boolean requested) {
        this.requested.set(requested);
    }

    public boolean isVerified() {
        return verified.get();
    }

    public SimpleBooleanProperty verifiedProperty() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified.set(verified);
    }

    public boolean isProfileCompleted() {
        return profileCompleted.get();
    }

    public SimpleBooleanProperty profileCompletedProperty() {
        return profileCompleted;
    }

    public void setProfileCompleted(boolean profileCompleted) {
        this.profileCompleted.set(profileCompleted);
    }

    @Override
    public String toString() {
        return name.getValue() + "\t" + section.getValue();
    }
}
