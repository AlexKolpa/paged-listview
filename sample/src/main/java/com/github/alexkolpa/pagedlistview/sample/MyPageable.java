package com.github.alexkolpa.pagedlistview.sample;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.util.Log;
import com.github.alexkolpa.pagedlistview.PagedListView;

class MyPageable implements PagedListView.Pageable {

	private static final long DELAY = 500L; //ms
	private static final int MAX_PAGE_COUNT = 3;
	private static final int BATCH_SIZE = 20;

	private final Handler mHandler = new Handler();

	private MultiTypeAdapter mAdapter;
	private int mPage = 0;

	public MyPageable(MultiTypeAdapter adapter) {
		this.mAdapter = adapter;
	}

	@Override
	public void onLoadMoreItems() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				List<String> newItems = new ArrayList<>(BATCH_SIZE);
				for(int i = 0; i < BATCH_SIZE; i++) {
					newItems.add("Item " + (mPage * BATCH_SIZE + i + 1));
				}

				mAdapter.addAll(newItems);

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
