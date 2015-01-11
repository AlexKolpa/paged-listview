package com.alexkolpa.pagedlistview;

import static android.view.ViewGroup.LayoutParams.*;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

public class PagedListView extends ListView {

	public interface Pageable {
		void onLoadMoreItems();
		boolean hasMoreItems();
	}

	private boolean mIsLoading;
	private Pageable mPageable;
	private View mLoadingView;
	private ViewGroup mFooterView;
	private OnScrollListener mOnScrollListener;
	private ListAdapter mAdapter;
	private DataSetObserver mDataObserver;

	private int mPreviousTotal;

	public PagedListView(Context context) {
		super(context);
		init();
	}

	public PagedListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		loadAttributes(attrs);
	}

	public PagedListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
		loadAttributes(attrs);
	}

	private void init() {
		mIsLoading = false;

		mFooterView = new LinearLayout(getContext());
		mFooterView.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));

		addFooterView(mFooterView, null, false);

		super.setOnScrollListener(new PagedScrollListener());
	}

	private void loadAttributes(AttributeSet attributeSet) {
		TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.PagedListView);
		int loadingViewId = typedArray.getResourceId(R.styleable.PagedListView_plv_loadingView, R.layout.default_loading_view);
		mLoadingView = LayoutInflater.from(getContext()).inflate(loadingViewId, mFooterView, false);
	}

	public void setPageable(Pageable pageable) {
		mPageable = pageable;
	}

	public boolean isLoading() {
		return mIsLoading;
	}

	public void setLoading(boolean loading) {
		mIsLoading = loading;

		if(mIsLoading && mFooterView.indexOfChild(mLoadingView) == -1) {
			mFooterView.addView(mLoadingView);
		}
		else if(!mIsLoading) {
			mFooterView.removeView(mLoadingView);
		}
	}

	public void setLoadingView(int loadingViewId) {
		View loadingView = LayoutInflater.from(getContext()).inflate(loadingViewId, mFooterView, false);
		setLoadingView(loadingView);
	}

	public void setLoadingView(View loadingView) {
		if(mFooterView.indexOfChild(mLoadingView) != -1) {
			mFooterView.removeView(mLoadingView);
			mFooterView.addView(loadingView);
		}

		mLoadingView = loadingView;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);

		if(mAdapter != null && mDataObserver != null) {
			mAdapter.unregisterDataSetObserver(mDataObserver);
		}

		mAdapter = adapter;

		if(mAdapter != null) {
			mDataObserver = new PagedDataSetObserver();
			mAdapter.registerDataSetObserver(mDataObserver);
			mDataObserver.onChanged();
		}
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mOnScrollListener = l;
	}

	private class PagedScrollListener implements OnScrollListener {

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if(mOnScrollListener != null) {
				mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}

			if(mIsLoading && totalItemCount != mPreviousTotal) {
				mIsLoading = false;

				mPreviousTotal = totalItemCount;

				mFooterView.removeView(mLoadingView);
			}

			if(mPageable != null && mPageable.hasMoreItems() && !mIsLoading &&
					((firstVisibleItem + visibleItemCount) > totalItemCount - 1)) {
				mIsLoading = true;

				mFooterView.addView(mLoadingView);

				mPageable.onLoadMoreItems();
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if(mOnScrollListener != null) {
				mOnScrollListener.onScrollStateChanged(view, scrollState);
			}
		}
	}

	private class PagedDataSetObserver extends DataSetObserver {
		@Override
		public void onChanged() {
			super.onChanged();
			checkAndUpdatePreviousTotal();
		}

		@Override
		public void onInvalidated() {
			super.onInvalidated();
			checkAndUpdatePreviousTotal();
		}

		private void checkAndUpdatePreviousTotal() {
			int empty = getAdapter().getCount();
			if(empty == getHeaderViewsCount() + getFooterViewsCount()) {
				mPreviousTotal = empty;
			}
		}
	}
}
