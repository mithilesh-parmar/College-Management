package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Menu {
    private StringProperty name = new SimpleStringProperty();
    private StringProperty screenPath = new SimpleStringProperty();
    private StringProperty iconPath = new SimpleStringProperty();

    public Menu(String name, String screenPath, String iconPath) {
        setName(name);
        setIconPath(iconPath);
        setScreenPath(screenPath);
    }

    @Override
    public String toString() {
        return name.get();
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

    public String getScreenPath() {
        return screenPath.get();
    }

    public StringProperty screenPathProperty() {
        return screenPath;
    }

    public void setScreenPath(String screenPath) {
        this.screenPath.set(screenPath);
    }

    public String getIconPath() {
        return iconPath.get();
    }

    public StringProperty iconPathProperty() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath.set(iconPath);
    }
}
