package com.github.alexkolpa.pagedlistview.sample;

import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.github.alexkolpa.pagedlistview.PagedListView;

public class AbstractPagedActivity extends ActionBarActivity {

	protected MyPageable mPageable;
	protected PagedListView mListView;
	protected MultiTypeAdapter mAdapter;

	protected void loadListView() {
		mAdapter = new MultiTypeAdapter();

		mPageable = new MyPageable(mAdapter);

		mListView = (PagedListView) findViewById(R.id.paged_listview);
		mListView.setAdapter(mAdapter);
		mListView.setPageable(mPageable);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.refresh:
				mAdapter.clear();
				mPageable.resetPage();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
