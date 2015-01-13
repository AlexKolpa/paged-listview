package com.github.alexkolpa.pagedlistview.sample;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import com.github.alexkolpa.pagedlistview.PagedListView;


public class MainActivity extends ActionBarActivity {

	private ArrayAdapter<String> mAdapter;
	private MyPageable mPageable;

	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		mPageable = new MyPageable();

		mHandler = new Handler();

		PagedListView listView = (PagedListView)findViewById(R.id.paged_listview);
		listView.setPageable(mPageable);
		listView.setAdapter(mAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.refresh) {
			mAdapter.clear();
			mPageable.resetPage();
		}

		return super.onOptionsItemSelected(item);
	}

	private class MyPageable implements PagedListView.Pageable {

		private static final long DELAY = 500L; //ms
		private static final int MAX_PAGE_COUNT = 3;
		private static final int BATCH_SIZE = 20;
		private int mPage = 0;

		@Override
		public void onLoadMoreItems() {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					for(int i = 0; i < BATCH_SIZE; i++) {
						mAdapter.add("Item " + (mPage * BATCH_SIZE + i + 1));
					}

					mPage++;

					Log.d("MyPageable", "Batch for page " + mPage + " added");
				}
			}, DELAY);
		}

		@Override
		public boolean hasMoreItems() {
			return mPage < MAX_PAGE_COUNT;
		}

		public void resetPage() {
			mPage = 0;
		}
	}
}
