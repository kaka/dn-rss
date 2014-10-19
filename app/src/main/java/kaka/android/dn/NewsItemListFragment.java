package kaka.android.dn;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


/**
 * A list fragment representing a list of NewsItems. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link NewsItemDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class NewsItemListFragment extends ListFragment implements NewsManager.EventListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };

    private BaseAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NewsItemListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

	setListAdapter(adapter = new BaseAdapter() {
	    @Override
	    public int getCount() {
		return App.news.getItems() == null ? 0 : App.news.getItems().size();
	    }

	    @Override
	    public Object getItem(int position) {
		return App.news.getItems().get(position);
	    }

	    @Override
	    public long getItemId(int position) {
		return 0;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if (convertView == null) {
		    convertView = LayoutInflater.from(App.context())
			    .inflate(R.layout.list_item, parent, false);
		    convertView.setTag(holder = new Holder());
		    holder.title = App.findViewById(convertView, R.id.title);
		    holder.time = App.findViewById(convertView, R.id.time);
		    holder.is_new = App.findViewById(convertView, R.id.is_new);
		    if (titleFont == null)
			titleFont = holder.title.getTypeface();
		} else {
		    holder = (Holder)convertView.getTag();
		}

		NewsItem item = (NewsItem)getItem(position);
		holder.title.setTypeface(titleFont, App.news.isItemRead(item) ? Typeface.NORMAL : Typeface.BOLD);
		holder.title.setText(item.getTitle());
		holder.time.setText(Utils.timeAgo(item.getDate()));
		holder.is_new.setVisibility(App.news.isItemNew(item) ? View.VISIBLE : View.GONE);

		return convertView;
	    }

	    Typeface titleFont;
	    class Holder {
		TextView title, time, is_new;
	    }
	});
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;

	App.news.addEventListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;

	App.news.removeEventListener(this);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
	super.onListItemClick(listView, view, position, id);

	// Notify the active callbacks interface (the activity, if the
	// fragment is attached to one) that an item has been selected.
	NewsItem item = (NewsItem)adapter.getItem(position);
	App.news.readItem(item);
	adapter.notifyDataSetChanged();
	mCallbacks.onItemSelected(item.getId());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    @Override
    public void onEvent(NewsManager.Event e) {
	if (e == NewsManager.Event.REFRESHED_NEWS || e == NewsManager.Event.LOADED_CACHE) {
	    adapter.notifyDataSetChanged();
	}
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }
}
