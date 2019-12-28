package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Map;

public class Teacher {
    private StringProperty name = new SimpleStringProperty(),
            profilePictureUrl = new SimpleStringProperty(),
            token = new SimpleStringProperty(),
            verificationCode = new SimpleStringProperty();
    private BooleanProperty verified = new SimpleBooleanProperty(false),
            profileCompleted = new SimpleBooleanProperty(false);


    public Teacher() {
    }

    public Teacher(String name, String profilePictureUrl, String token, String verificationCode, boolean verified, boolean profileCompleted) {
        setName(name);
        setProfilePictureUrl(profilePictureUrl);
        setToken(token);
        setVerificationCode(verificationCode);
        setVerified(verified);
        setProfileCompleted(profileCompleted);
    }

    public static Teacher fromJSON(Map<String, Object> json) {
        return new Teacher(
                (String) json.get("name"),
                (String) json.get("pp_url"),
                (String) json.get("token"),
                (String) json.get("verification_code"),
                (boolean) json.get("verified"),
                (boolean) json.get("profile_completed")
        );
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

    public String getProfilePictureUrl() {
        return profilePictureUrl.get();
    }

    public StringProperty profilePictureUrlProperty() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl.set(profilePictureUrl);
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

    public String getVerificationCode() {
        return verificationCode.get();
    }

    public StringProperty verificationCodeProperty() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode.set(verificationCode);
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
        return name.get();
    }
}
