package kaka.android.dn;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsItemSlideshowFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 *
 */
public class NewsItemSlideshowFragment extends Fragment implements NewsManager.EventListener,
	View.OnClickListener, ViewPager.OnPageChangeListener
{
    private ViewPager viewPager;
    private PagerAdapter adapter;
    private boolean isDraggingViewPager;

    private OnFragmentInteractionListener mListener;

    public NewsItemSlideshowFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_newsitem_slideshow, container, false);
	viewPager = App.findViewById(root, R.id.view_pager);
	viewPager.setAdapter(adapter = new PagerAdapter() {
	    @Override
	    public int getCount() {
		return App.news.getItems() == null ? 0 : App.news.getItems().size();
	    }

	    @Override
	    public Object instantiateItem(ViewGroup container, int position) {
		View view = LayoutInflater.from(App.context())
			.inflate(R.layout.slideshow_item, container, false);

		NewsItem item = App.news.getItems().get(position);
		((TextView)(view.findViewById(R.id.title))).setText(item.getTitle());
		((TextView)(view.findViewById(R.id.description))).setText(item.getDescription());

		((ViewPager)container).addView(view, 0);
		view.setOnClickListener(NewsItemSlideshowFragment.this);

		return view;
	    }

	    @Override
	    public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager)container).removeView((View)object);
	    }

	    @Override
	    public boolean isViewFromObject(View view, Object o) {
		return view == o;
	    }
	});
	viewPager.setOnPageChangeListener(NewsItemSlideshowFragment.this);

	setupTimerTask();

	return root;
    }

    private void setupTimerTask() {
	long interval = 4000;
	Timer timer = new Timer(true);
	timer.scheduleAtFixedRate(new TimerTask() {
	    @Override
	    public void run() {
		if (!isDraggingViewPager) {
		    App.runOnUiThread(new Runnable() {
			public void run() {
			    int current = viewPager.getCurrentItem();
			    int count = adapter.getCount();
			    int next = (current + 1) % count;
			    if (count > 0) {
				viewPager.setCurrentItem(next, true);
			    }
			}
		    });
		}
	    }
	}, interval, interval);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

	App.news.addEventListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

	App.news.removeEventListener(this);
    }

    @Override
    public void onEvent(NewsManager.Event e) {
	adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
	int position = viewPager.getCurrentItem();
	NewsItem item = App.news.getItems().get(position);
	if (mListener != null) {
	    mListener.onSlideClick(item.getId());
	}
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }
    @Override
    public void onPageSelected(int i) {
    }
    @Override
    public void onPageScrollStateChanged(int i) {
	switch (i) {
	    case ViewPager.SCROLL_STATE_DRAGGING:
		isDraggingViewPager = true;
		break;
	    case ViewPager.SCROLL_STATE_IDLE:
		isDraggingViewPager = false;
		break;
	}
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onSlideClick(String id);
    }
}
