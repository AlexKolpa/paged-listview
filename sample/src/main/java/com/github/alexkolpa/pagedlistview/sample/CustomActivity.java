package com.github.alexkolpa.pagedlistview.sample;

import android.os.Bundle;

public class CustomActivity extends AbstractPagedActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custom);

		loadListView();

		mListView.setLoadingView(findViewById(R.id.loading_view), false);
	}
}
