package com.github.alexkolpa.pagedlistview.sample;

import android.os.Bundle;

public class FooterActivity extends AbstractPagedActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_footer);

		loadListView();
	}
}
