package teacher_leaves;


public enum Filter {

    ALL("All"),
    APPROVED("Approved"),
    DECLINED("Declined"),
    PENDING("Pending");

    private String title;

    Filter(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }

    public static long getStatusCode(Filter value) {
        switch (value) {
            case DECLINED:
                return 0L;
            case APPROVED:
                return 1L;
            case PENDING:
                return 2L;
        }
        return -1L;
    }

    public static Filter getStatusFilter(long value) {
        if (value == 0L) return DECLINED;
        else if (value == 1L) return APPROVED;
        else if (value == 2L) return PENDING;
        return null;
    }
}
