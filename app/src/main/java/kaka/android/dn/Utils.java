package kaka.android.dn;

import java.util.Date;

public class Utils
{
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
}
