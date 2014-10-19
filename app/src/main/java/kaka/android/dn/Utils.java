package kaka.android.dn;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    public static Object deserializeFromString(String str) throws IOException, ClassNotFoundException {
	byte[] data = Base64.decode(str, 0);
	ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
	Object o = ois.readObject();
	ois.close();
	return o;
    }

    public static String serializeToString(Object obj) throws IOException {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	ObjectOutputStream oos = new ObjectOutputStream(baos);
	oos.writeObject(obj);
	oos.close();
	return Base64.encodeToString(baos.toByteArray(), 0);
    }
}
