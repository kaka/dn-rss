package kaka.android.dn;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public class NewsManager
{
    private static final String CACHE_KEY = "cache";

    private ArrayList<NewsItem> items = new ArrayList<NewsItem>();
    private HashSet<NewsItem> itemSet = new HashSet<NewsItem>();
    private ArrayList<EventListener> eventListeners = new ArrayList<EventListener>();

    public NewsManager() {
	new Thread() {
	    public void run() {
		loadItems();
	    }
	}.start();
    }

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
	REFRESHED_NEWS,
	REFRESH_FAILED,
	LOADED_CACHE
    }

    public void refresh() {
	new Thread() {
	    public void run() {
		InputStream stream = fetch("http://www.dn.se/nyheter/m/rss/");
		XmlPullParser parser = buildParser(stream);
		if (stream != null && parser != null) {
		    addItems(parseXML(parser));

		    try {
			stream.close();
		    } catch (Exception e) {
			App.log.e(this, "Error while closing stream", e);
		    }

		    notifyListeners(Event.REFRESHED_NEWS);

		    saveItems();
		} else {
		    notifyListeners(Event.REFRESH_FAILED);
		}
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
	ArrayList<NewsItem> list = new ArrayList<NewsItem>();
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
				list.add(item);
				item = null;
			    } else if (name.equals("title")) {
				item.setTitle(text);
			    } else if (name.equals("link")) {
				item.setLink(text);
			    } else if (name.equals("description")) {
				item.setDescription(text);
			    } else if (name.equals("pubDate")) {
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

	return list;
    }

    public ArrayList<NewsItem> getItems() {
	return items;
    }

    public NewsItem getItemById(String guid) {
	for (NewsItem item : items)
	    if (item.getId().equals(guid))
		return item;
	return null;
    }

    private void addItems(Collection<NewsItem> collection) {
	if (collection == null)
	    return;

	itemSet.addAll(collection);
	items.clear();
	for (NewsItem i : itemSet)
	    items.add(i);
	Collections.sort(items, new Comparator<NewsItem>() {
	    @Override
	    public int compare(NewsItem lhs, NewsItem rhs) {
		return rhs.getDate().compareTo(lhs.getDate());
	    }
	});
    }

    private void loadItems() {
	addItems(loadFromCache());
	notifyListeners(Event.LOADED_CACHE);
    }

    private void saveItems() {
	// Filter out outdated news
	HashSet<NewsItem> set = new HashSet<NewsItem>(itemSet.size());
	long timeLimit = System.currentTimeMillis() - 1000*60*60*24*2;
	for (NewsItem i : itemSet)
	    if (i.getDate().getTime() >= timeLimit)
		set.add(i);
	saveToCache(set);
    }

    private void saveToCache(HashSet<NewsItem> items) {
	try {
	    App.sharedPreferences().edit().putString(CACHE_KEY, Utils.serializeToString(items)).commit();
	} catch (Exception e) {
	    App.log.e(this, "Failed to save to cache", e);
	}
    }

    @SuppressWarnings("unchecked")
    private HashSet<NewsItem> loadFromCache() {
	String s = App.sharedPreferences().getString(CACHE_KEY, null);
	if (s != null) {
	    try {
		return (HashSet<NewsItem>)Utils.deserializeFromString(s);
	    } catch (Exception e) {
		App.log.e(this, "Failed to load from cache", e);
	    }
	}
	return null;
    }
}
