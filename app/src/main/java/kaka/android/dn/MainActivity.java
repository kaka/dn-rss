package kaka.android.dn;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class MainActivity extends Activity implements NewsManager.EventListener
{
    private NewsListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

	App.news.addEventListener(this);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment = new NewsListFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEvent(NewsManager.Event e) {
	if (e == NewsManager.Event.REFRESHED_NEWS) {
	   fragment.refreshItems();
	}
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class NewsListFragment extends ListFragment {
	private BaseAdapter adapter;

        public NewsListFragment() {
        }

	public void refreshItems() {
	    adapter.notifyDataSetChanged();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
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
		    } else {
			holder = (Holder)convertView.getTag();
		    }

		    NewsItem item = (NewsItem)getItem(position);
		    holder.title.setText(item.getTitle());

		    return convertView;
		}

		class Holder {
		    TextView title;
		}
	    });
	}
    }
}
