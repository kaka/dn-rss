package kaka.android.dn;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A fragment representing a single NewsItem detail screen.
 * This fragment is either contained in a {@link NewsItemListActivity}
 * in two-pane mode (on tablets) or a {@link NewsItemDetailActivity}
 * on handsets.
 */
public class NewsItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The content this fragment is presenting.
     */
    private NewsItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NewsItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
	    mItem = App.news.getItemById(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_newsitem_detail, container, false);

        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.newsitem_detail)).setText(mItem.getDescription());
        }

        return rootView;
    }
}
