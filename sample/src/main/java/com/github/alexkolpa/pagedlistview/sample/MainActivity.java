package com.github.alexkolpa.pagedlistview.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		String[] names = getResources().getStringArray(R.array.activity_names);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);

		ListView listView = (ListView)findViewById(R.id.activity_list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Class intentClazz;
		switch (position) {
			case 0:
				intentClazz = FooterActivity.class;
				break;
			case 1:
				intentClazz = CustomActivity.class;
				break;
			default:
				throw new IllegalArgumentException("Incorrect position!");
		}

		Intent intent = new Intent(this, intentClazz);
		startActivity(intent);
	}
}
