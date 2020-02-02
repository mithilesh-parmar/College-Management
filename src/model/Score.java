package model;

import javafx.beans.property.*;

import java.util.HashMap;
import java.util.Map;

public class Score {
    private BooleanProperty pass;
    private LongProperty theoryMarks, practicalMarks;

    public Score(boolean pass, long theoryMarks, long practicalMarks) {
        this.pass = new SimpleBooleanProperty(pass);
        this.theoryMarks = new SimpleLongProperty(theoryMarks);
        this.practicalMarks = new SimpleLongProperty(practicalMarks);
    }

    public static Score fromJSON(Map<String, Object> json) {
        return new Score(
                (Boolean) json.get("pass"),
                (long) json.get("th_mark"),
                (long) json.get("pr_mark")
        );
    }

    public Map<String, Object> toJSON() {
        Map<String, Object> json = new HashMap<>();
        json.put("pass", pass.get());
        json.put("th_mark", theoryMarks.get());
        json.put("pr_mark", practicalMarks.get());
        return json;
    }

    @Override
    public String toString() {
        return "\nPass: " + pass.get() +
                "\nTheory: " + theoryMarks.get() +
                "\nPractical: " + practicalMarks.get();
    }

    public boolean isPass() {
        return pass.get();
    }

    public BooleanProperty passProperty() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass.set(pass);
    }

    public long getTheoryMarks() {
        return theoryMarks.get();
    }

    public LongProperty theoryMarksProperty() {
        return theoryMarks;
    }

    public void setTheoryMarks(long theoryMarks) {
        this.theoryMarks.set(theoryMarks);
    }

    public long getPracticalMarks() {
        return practicalMarks.get();
    }

    public LongProperty practicalMarksProperty() {
        return practicalMarks;
    }

    public void setPracticalMarks(long practicalMarks) {
        this.practicalMarks.set(practicalMarks);
    }
}
