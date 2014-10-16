package kaka.android.dn;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NewsManager
{
    private ArrayList<NewsItem> items;
    private ArrayList<EventListener> eventListeners = new ArrayList<EventListener>();

    public void addEventListener(EventListener listener) {
	eventListeners.add(listener);
    }

    public boolean removeEventListener(EventListener listener) {
	return eventListeners.remove(listener);
    }

    private void notifyListeners(final Event e) {
	App.runOnUiThread(new Runnable() {
	    public void run() {
		for (EventListener el : eventListeners) {
		    el.onEvent(e);
		}
	    }
	});
    }

    public static interface EventListener {
	public void onEvent(Event e);
    }

    public static enum Event {
	REFRESHED_NEWS
    }

    public void refresh() {
	new Thread() {
	    public void run() {
		InputStream stream = fetch("http://www.dn.se/nyheter/m/rss/");
		XmlPullParser parser = buildParser(stream);
		items = parseXML(parser);

		try {
		    stream.close();
		} catch (Exception e) {
		    App.log.e(this, "Error while closing stream", e);
		}

		notifyListeners(Event.REFRESHED_NEWS);
	    }
	}.start();
    }

    private InputStream fetch(String urlString) {
	try {
	    URL url = new URL(urlString);
	    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	    conn.setReadTimeout(10000);
	    conn.setConnectTimeout(15000);
	    conn.setRequestMethod("GET");
	    conn.setDoInput(true);
	    conn.connect();
	    return conn.getInputStream();
	} catch (Exception e) {
	    App.log.e(this, "Error while fetching data", e);
	}
	return null;
    }

    private XmlPullParser buildParser(InputStream stream) {
	try {
	    XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
	    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	    parser.setInput(stream, null);
	    return parser;
	} catch (Exception e) {
	    App.log.e(this, "Error while setting up parser", e);
	}
	return null;
    }

    private ArrayList<NewsItem> parseXML(XmlPullParser parser) {
	ArrayList<NewsItem> items = new ArrayList<NewsItem>();
	int event;
	String text = null;
	NewsItem item = null;

	try {
	    event = parser.getEventType();
	    while (event != XmlPullParser.END_DOCUMENT) {
		String name = parser.getName();
		switch (event) {
		    case XmlPullParser.TEXT:
			text = parser.getText();
			break;
		    case XmlPullParser.START_TAG:
			if (name.equals("item")) {
			    item = new NewsItem();
			}
			break;
		    case XmlPullParser.END_TAG:
			if (item != null) {
			    if (name.equals("item")) {
				items.add(item);
				item = null;
			    } else if (name.equals("title")) {
				item.setTitle(text);
			    } else if (name.equals("link")) {
				item.setLink(text);
			    } else if (name.equals("description")) {
				item.setDescription(text);
			    } else if (name.equals("dc:date")) {
				item.setPubDate(text);
			    }
			}
			break;
		}
		event = parser.next();
	    }
	} catch (Exception e) {
	    App.log.e(this, "Error while parsing XML", e);
	}

	return items;
    }
}
