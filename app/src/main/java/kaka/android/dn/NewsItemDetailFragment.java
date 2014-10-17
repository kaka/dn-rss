package kaka.android.dn;

import android.content.Intent;
import android.net.Uri;
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
	    if (mItem == null) {
		getActivity().getFragmentManager().popBackStack();
	    }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_newsitem_detail, container, false);

        if (mItem != null) {
	    ((TextView)rootView.findViewById(R.id.title)).setText(mItem.getTitle());
	    ((TextView)rootView.findViewById(R.id.description)).setText(mItem.getDescription());
	    String date = mItem.getDateWithFormat("yyyy-MM-dd HH:mm:ss");
	    ((TextView)rootView.findViewById(R.id.date)).setText(getString(R.string.item_published, date));
	    rootView.findViewById(R.id.read_more).setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
		    Uri uri = Uri.parse(mItem.getLink());
		    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		    startActivity(intent);
		}
	    });
        }

        return rootView;
    }
}
