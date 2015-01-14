package com.github.alexkolpa.pagedlistview.sample;

import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import com.github.alexkolpa.pagedlistview.PagedListView;

class MyPageable implements PagedListView.Pageable {

	private static final long DELAY = 500L; //ms
	private static final int MAX_PAGE_COUNT = 3;
	private static final int BATCH_SIZE = 20;

	private final Handler mHandler = new Handler();

	private ArrayAdapter<String> mAdapter;
	private int mPage = 0;

	public MyPageable(ArrayAdapter<String> adapter) {
		this.mAdapter = adapter;
	}

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
