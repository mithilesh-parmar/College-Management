package utility;


import com.google.cloud.Timestamp;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public abstract class DateUtility {


    public static String timeStampToReadable(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
        return sdf.format(timestamp.toDate());
    }

    public static Date localDateToDate(LocalDate localDate) {

        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate dateToLocalDate(Timestamp timestamp) {
        return LocalDate.ofInstant(timestamp.toDate().toInstant(), ZoneId.systemDefault());
    }

    public static Timestamp localDateToTimestamp(LocalDate localDate) {
        return Timestamp.of(localDateToDate(localDate));
    }

    public static LocalDate dateToLocalDate(Date date) {
        return LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

}
