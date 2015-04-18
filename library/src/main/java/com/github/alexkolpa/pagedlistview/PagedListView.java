package com.github.alexkolpa.pagedlistview;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

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

	private boolean mFooterContained = false;
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
		typedArray.recycle();

		setLoadingView(loadingViewId);
	}

	/**
	 * Set Pageable behind this PagedListView
	 * @param pageable the Pageable that handles the loading of new data
	 */
	public void setPageable(Pageable pageable) {
		mPageable = pageable;
	}

	public boolean isLoading() {
		return mIsLoading;
	}

	/**
	 * Set the loading state of the View
	 * @param loading whether the view is currently loading
	 */
	public void setLoading(boolean loading) {
		mIsLoading = loading;

		checkAndToggleLoadingVisibility();
	}

	/**
	 * Inflates and adds a new loading view into the list's footer
	 * @param loadingViewId resource id of view to be inflated
	 */
	public void setLoadingView(int loadingViewId) {
		View loadingView = LayoutInflater.from(getContext()).inflate(loadingViewId, mFooterView, false);
		setLoadingView(loadingView, true);
	}

	/**
	 * Set a new view as the loading view
	 * @param loadingView the view to be used to indicate a loading list
	 * @param addToFooter Whether the view should be added to the footer
	 */
	public void setLoadingView(View loadingView, boolean addToFooter) {
		if(mFooterView.indexOfChild(mLoadingView) != -1) {
			mFooterView.removeView(mLoadingView);
		}

		if(addToFooter && loadingView != null) {
			mFooterView.addView(loadingView);
		}

		mFooterContained = addToFooter;

		mLoadingView = loadingView;

		checkAndToggleLoadingVisibility();
	}

	private void checkAndToggleLoadingVisibility() {
		if(mLoadingView != null) {
			if(mFooterContained) {
				//Due to a layout issue with views inside a ListView, setting visibility to GONE behaves the same as
				// INVISIBLE, which is not what we want. So we actively remove the view from the footer view.
				if(mIsLoading && mFooterView.indexOfChild(mLoadingView) == -1) {
					mFooterView.addView(mLoadingView);
				}
				else {
					mFooterView.removeView(mLoadingView);
				}
			}
			else {
				mLoadingView.setVisibility(mIsLoading? VISIBLE : GONE);
			}
		}
	}

	/**
	 * Sets the new loading view in the footer
	 * @param loadingView view to be shown on loading
	 */
	public void setLoadingView(View loadingView) {
		setLoadingView(loadingView, true);
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

				checkAndToggleLoadingVisibility();
			}

			if(mPageable != null && mPageable.hasMoreItems() && !mIsLoading &&
					((firstVisibleItem + visibleItemCount) > totalItemCount - 1)) {
				mIsLoading = true;

				checkAndToggleLoadingVisibility();

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
