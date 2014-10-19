package kaka.android.dn;

public class Cache<T>
{
    private String key;

    public Cache(String key) {
	this.key = key;
    }

    /**
     * @return true on success, false on failure
     */
    public boolean save(T object) {
	try {
	    App.sharedPreferences().edit().putString(key, Utils.serializeToString(object)).commit();
	    return true;
	} catch (Exception e) {
	    App.log.e(this, String.format("Failed to save to cache '%s'", key), e);
	}
	return false;
    }

    @SuppressWarnings("unchecked")
    public T load() {
	String s = App.sharedPreferences().getString(key, null);
	if (s != null) {
	    try {
		return (T)Utils.deserializeFromString(s);
	    } catch (Exception e) {
		App.log.e(this, String.format("Failed to load from cache '%s'", key), e);
	    }
	}
	return null;
    }
}
