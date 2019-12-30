package utility;

public enum DocumentType {
    IMAGE("image"), TEXT("text");

    String type;

    DocumentType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
