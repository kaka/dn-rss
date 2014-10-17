package kaka.android.dn;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils
{
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String timeAgo(Date date) {
	long seconds = (System.currentTimeMillis() - date.getTime()) / 1000;
	if (seconds > 60*60*24)
	    return (seconds / (60*60*24)) + " d";
	if (seconds > 60*60)
	    return (seconds / (60*60)) + " h";
	if (seconds > 60)
	    return (seconds / 60) + " m";
	return seconds + " s";
    }

    public static boolean isToday(Date date) {
	return dateFormat.format(date).equals(dateFormat.format(new Date()));
    }

    public static boolean isYesterday(Date date) {
	Date yesterday = new Date();
	yesterday.setTime(yesterday.getTime() - 1000*60*60*24);
	return dateFormat.format(date).equals(dateFormat.format(yesterday));
    }
}
