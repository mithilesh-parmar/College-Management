package time_table;

public enum DAY {

    MONDAY("1"),
    TUESDAY("2"),
    WEDNESDAY("3"),
    THURSDAY("4"),
    FRIDAY("5"),
    SATURDAY("6");

    private String value;

    DAY(String day) {
        this.value = day;
    }

    @Override
    public String toString() {
        return value;
    }
}
