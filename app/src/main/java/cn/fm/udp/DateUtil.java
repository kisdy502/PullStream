package cn.fm.udp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public final static String FORMAT_MMDD = "MMDD";
    public final static String FORMAT_MMDDHH = "MMDDHH";
    public final static String FORMAT_YYYYMMDD = "yyyy-MM-dd";
    public final static String FORMAT_YYYYMMDD2 = "yyyyMMdd";
    public final static String YYYYMMDD_HH = "yyyyMMdd_HH";
    public final static String FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public final static String FORMAT_STAND = "yyyy-MM-dd HH:mm:ss";


    /**
     * 将时间转换为时间戳
     */
    public static long dateToTimestamp(String dateStr) throws ParseException {
        return dateToTimestamp(dateStr, FORMAT_STAND);
    }

    /**
     * 将时间转换为时间戳
     */
    public static long dateToTimestamp(String dateStr, String format) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = simpleDateFormat.parse(dateStr);
        long ts = date.getTime();
        return ts;
    }

    /**
     * 将时间戳转换为时间
     */
    public static String timestampToDateString(String s, String format) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 将时间戳转换为时间
     */
    public static String timestampToDateString(String s) {
        return timestampToDateString(s, "yyyy-MM-dd HH:mm:ss");
    }

    public static String timestampToDateString(long stamp, String format) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = new Date(stamp);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 将时间戳转换为时间
     */
    public static String timestampToDateString(long stamp) {
        return timestampToDateString(stamp, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date timestampToDate(long stamp) {
        Date date = new Date(stamp);
        return date;
    }

}
