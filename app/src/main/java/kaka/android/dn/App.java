package kaka.android.dn;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class App extends Application
{
    public static App INSTANCE;
    public static final boolean DEBUG = true;
    public static final int SDK = android.os.Build.VERSION.SDK_INT;
    public static NewsManager news;

    private static Thread uiThread;
    private static Handler uiHandler;
    private static ConnectivityManager connectivityManager;
    private static ConcurrentLinkedQueue<WeakReference<OnSignal>> signalCallbacks;
    private static ActivityLifecycleHandler lifecycleHandler;

    @Override
    public void onCreate() {
	super.onCreate();
	App.INSTANCE = this;

	news = new NewsManager();

	uiThread = context().getMainLooper().getThread();
	uiHandler = new Handler(context().getMainLooper());
	connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
	signalCallbacks = new ConcurrentLinkedQueue<WeakReference<OnSignal>>();
	lifecycleHandler = new ActivityLifecycleHandler();
	registerActivityLifecycleCallbacks(lifecycleHandler);
    }

    public static Context context() {
	return INSTANCE.getApplicationContext();
    }

    public static SharedPreferences sharedPreferences() {
	return PreferenceManager.getDefaultSharedPreferences(context());
    }

    public static Resources resources() {
	return INSTANCE.getResources();
    }

    public static String string(int resId) {
	return INSTANCE.getString(resId);
    }
    public static String string(int resId, Object... args) {
	return INSTANCE.getString(resId, args);
    }

    public static String quantityString(int resId, int quantity) {
	return INSTANCE.getResources().getQuantityString(resId, quantity);
    }
    public static String quantityString(int resId, int quantity, Object... args) {
	return INSTANCE.getResources().getQuantityString(resId, quantity, args);
    }

    public static boolean stringp(String s) {
	return s != null && !s.isEmpty();
    }

    public static String versionName() {
	try {
	    return INSTANCE.getPackageManager().getPackageInfo(INSTANCE.getPackageName(), 0).versionName;
	} catch(android.content.pm.PackageManager.NameNotFoundException e) {
	    App.log.e("App", "Version name error", e);
	}
	return "";
    }
    public static int versionCode() {
	try {
	    return INSTANCE.getPackageManager().getPackageInfo(INSTANCE.getPackageName(), 0).versionCode;
	} catch(android.content.pm.PackageManager.NameNotFoundException e) {
	    App.log.e("App", "Version code error", e);
	}
	return 0;
    }

    @SuppressWarnings({ "unchecked", "UnusedDeclaration" }) // Checked by runtime cast. Public API.
    public static <T extends View> T findViewById(View view, int id) {
	return (T)view.findViewById(id);
    }
    @SuppressWarnings({ "unchecked", "UnusedDeclaration" }) // Checked by runtime cast. Public API.
    public static <T extends View> T findViewById(Activity activity, int id) {
	return (T)activity.findViewById(id);
    }

    public static <T extends View> ArrayList<T> findViewsByTag(View root, String tag, Class<T> type) {
	ArrayList<T> views = new ArrayList<T>();
	final Object rootTag = root.getTag();
	if(rootTag != null && rootTag.equals(tag))
	    views.add((T)root);

	if(root instanceof ViewGroup) {
	    ViewGroup group = (ViewGroup)root;
	    final int childCount = group.getChildCount();
	    for(int i = 0; i < childCount; i++) {
		final View child = group.getChildAt(i);
		if(child instanceof ViewGroup)
		    views.addAll(findViewsByTag(child, tag, type));

		final Object tagObj = child.getTag();
		if(tagObj != null && tagObj.equals(tag))
		    views.add((T) child);
	    }
	}
	return views;
    }

    public static void runOnUiThread(Runnable runnable) {
	if(Thread.currentThread() == uiThread) {
	    runnable.run();
	} else {
	    uiHandler.post(runnable);
	}
    }

    public static boolean hasConnectivity() {
	NetworkInfo info = connectivityManager.getActiveNetworkInfo();
	return info != null && info.isConnectedOrConnecting();
    }

    public static void toast(int resid) {
	toast(App.string(resid));
    }
    public static void toast(int resid, Object... args) {
	toast(App.string(resid, args));
    }
    public static void toast(String msg, Object... args) {
	toast(String.format(msg, args));
    }
    public static void toast(String msg) {
	Toast.makeText(context(), msg, Toast.LENGTH_SHORT).show();
    }

    public static class log {
	public static void d(Object obj, String msg)		     { if(DEBUG) Log.d(className(obj), msg); }
	public static void d(Object obj, String msg, Object... args) { if(DEBUG) Log.d(className(obj), String.format(msg, args)); }
	public static void d(Object obj, String msg, Throwable tr)   { if(DEBUG) Log.w(className(obj), msg, tr); }
	public static void d(String tag, String msg)		     { if(DEBUG) Log.d(tag, msg); }
	public static void d(String tag, String msg, Object... args) { if(DEBUG) Log.d(tag, String.format(msg, args)); }
	public static void d(String tag, String msg, Throwable tr)   { if(DEBUG) Log.d(tag, msg, tr); }

	public static void v(Object obj, String msg)		     { if(DEBUG) Log.v(className(obj), msg); }
	public static void v(Object obj, String msg, Object... args) { if(DEBUG) Log.v(className(obj), String.format(msg, args)); }
	public static void v(Object obj, String msg, Throwable tr)   { if(DEBUG) Log.w(className(obj), msg, tr); }
	public static void v(String tag, String msg)		     { if(DEBUG) Log.v(tag, msg); }
	public static void v(String tag, String msg, Object... args) { if(DEBUG) Log.v(tag, String.format(msg, args)); }
	public static void v(String tag, String msg, Throwable tr)   { if(DEBUG) Log.v(tag, msg, tr); }

	public static void i(Object obj, String msg)		     { if(DEBUG) Log.i(className(obj), msg); }
	public static void i(Object obj, String msg, Object... args) { if(DEBUG) Log.i(className(obj), String.format(msg, args)); }
	public static void i(Object obj, String msg, Throwable tr)   { if(DEBUG) Log.w(className(obj), msg, tr); }
	public static void i(String tag, String msg)		     { if(DEBUG) Log.i(tag, msg); }
	public static void i(String tag, String msg, Object... args) { if(DEBUG) Log.i(tag, String.format(msg, args)); }
	public static void i(String tag, String msg, Throwable tr)   { if(DEBUG) Log.i(tag, msg, tr); }

	public static void w(Object obj, String msg)		     { if(DEBUG) Log.w(className(obj), msg); }
	public static void w(Object obj, String msg, Object... args) { if(DEBUG) Log.w(className(obj), String.format(msg, args)); }
	public static void w(Object obj, String msg, Throwable tr)   { if(DEBUG) Log.w(className(obj), msg, tr); }
	public static void w(String tag, String msg)		     { if(DEBUG) Log.w(tag, msg); }
	public static void w(String tag, String msg, Object... args) { if(DEBUG) Log.w(tag, String.format(msg, args)); }
	public static void w(String tag, String msg, Throwable tr)   { if(DEBUG) Log.w(tag, msg, tr); }

	public static void e(Object obj, String msg)		     { Log.e(className(obj), msg); }
	public static void e(Object obj, String msg, Object... args) { Log.e(className(obj), String.format(msg, args)); }
	public static void e(Object obj, String msg, Throwable tr)   { Log.e(className(obj), msg, tr); }
	public static void e(String tag, String msg)		     { Log.e(tag, msg); }
	public static void e(String tag, String msg, Object... args) { Log.e(tag, String.format(msg, args)); }
	public static void e(String tag, String msg, Throwable tr)   { Log.e(tag, msg, tr); }

	private static String className(Object obj) {
	    Class cls = obj.getClass();
	    return cls.getName()
		.replace(cls.getPackage().getName() + ".", "")
		.split("\\$", 2)[0];
	}
    }

    public static class signal {
	public static void register(OnSignal cb) {
	    synchronized(signalCallbacks) {
		signalCallbacks.add(new WeakReference<OnSignal>(cb));
	    }
	}
	public static void unregister(OnSignal cb) {
	    synchronized(signalCallbacks) {
		for(Iterator<WeakReference<OnSignal>> iter = signalCallbacks.iterator(); iter.hasNext(); ) {
		    WeakReference<OnSignal> ref = iter.next();
		    if(cb.equals(ref.get()))
			iter.remove();
		}
	    }
	}
	public static void send(final String signal) {
	    App.log.i("App", "Sending signal: " + signal);
	    runOnUiThread(new Runnable() {
		public void run() {
		    synchronized(signalCallbacks) {
			for(Iterator<WeakReference<OnSignal>> iter = signalCallbacks.iterator(); iter.hasNext(); ) {
			    WeakReference<OnSignal> ref = iter.next();
			    OnSignal cb = ref.get();
			    if(cb != null) {
				cb.onSignal(signal);
			    } else {
				iter.remove();
			    }
			}
		    }
		}
	    });
	}
    }
    public interface OnSignal {
	public void onSignal(String signal);
    }

    public static AlertDialog.Builder alertDialog(Context context) {
	return new AlertDialog.Builder(context);
    }

    public static float dpToPixels(int dp) {
	return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources().getDisplayMetrics());
    }

    public static boolean isRunningInForeground() {
	return lifecycleHandler.isApplicationInForeground();
    }
    public static boolean isVisible() {
	return lifecycleHandler.isApplicationVisible();
    }
    private class ActivityLifecycleHandler implements ActivityLifecycleCallbacks {
	private int resumed, paused;
	private int started, stopped;

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

	@Override
	public void onActivityDestroyed(Activity activity) {}

	@Override
	public void onActivityResumed(Activity activity) { ++resumed; }

	@Override
	public void onActivityPaused(Activity activity) { ++paused; }

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

	@Override
	public void onActivityStarted(Activity activity) { ++started; }

	@Override
	public void onActivityStopped(Activity activity) { ++stopped; }

	public boolean isApplicationVisible() { return started > stopped; }
	public boolean isApplicationInForeground() { return resumed > paused; }
    }
}
