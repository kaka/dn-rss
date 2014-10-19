package kaka.android.dn;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;


/**
 * An activity representing a list of NewsItems. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link NewsItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link NewsItemListFragment} and the item details
 * (if present) is a {@link NewsItemDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link NewsItemListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class NewsItemListActivity extends Activity
        implements NewsItemListFragment.Callbacks,
	NewsItemSlideshowFragment.OnFragmentInteractionListener,
	NewsManager.EventListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private MenuItem refreshItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_newsitem_list);

        if (findViewById(R.id.newsitem_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((NewsItemListFragment) getFragmentManager()
                    .findFragmentById(R.id.newsitem_list))
                    .setActivateOnItemClick(true);
        }

        App.news.addEventListener(this);
	App.news.refresh();
	setProgressBarIndeterminateVisibility(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	refreshItem = menu.add(getString(R.string.refresh));
	refreshItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	refreshItem.setIcon(R.drawable.ic_action_navigation_refresh);
	refreshItem.setVisible(false); // since a refresh is always issued at startup
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	if (item == refreshItem) {
	    App.news.refresh();
	    refreshItem.setVisible(false);
	    setProgressBarIndeterminateVisibility(true);
	    return true;
	}
	return false;
    }

    @Override
    public void onEvent(NewsManager.Event e) {
	if (e == NewsManager.Event.REFRESHED_NEWS || e == NewsManager.Event.REFRESH_FAILED) {
	    if (refreshItem != null)
		refreshItem.setVisible(true);
	    setProgressBarIndeterminateVisibility(false);
	}
    }

    /**
     * Callback method from {@link NewsItemListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
	showNewsItem(id);
    }

    @Override
    public void onSlideClick(String id) {
	showNewsItem(id);
    }

    private void showNewsItem(String id) {
	if (mTwoPane) {
	    // In two-pane mode, show the detail view in this activity by
	    // adding or replacing the detail fragment using a
	    // fragment transaction.
	    Bundle arguments = new Bundle();
	    arguments.putString(NewsItemDetailFragment.ARG_ITEM_ID, id);
	    NewsItemDetailFragment fragment = new NewsItemDetailFragment();
	    fragment.setArguments(arguments);
	    getFragmentManager().beginTransaction()
		    .replace(R.id.newsitem_detail_container, fragment)
		    .commit();

	} else {
	    // In single-pane mode, simply start the detail activity
	    // for the selected item ID.
	    Intent detailIntent = new Intent(this, NewsItemDetailActivity.class);
	    detailIntent.putExtra(NewsItemDetailFragment.ARG_ITEM_ID, id);
	    startActivity(detailIntent);
	    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
    }
}
