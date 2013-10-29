package com.example.clxapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AddCircleMemberActivity extends Activity implements
		OnClickListener {
	private Button add;
	private Button input;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_circle_member);
		add = (Button) findViewById(R.id.addFromAddBook);
		input = (Button) findViewById(R.id.inputContact);
		add.setOnClickListener(this);
		input.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addFromAddBook:
			Intent intent = new Intent();
			intent.setClass(this, SelectContactsActivity.class);
			startActivity(intent);
			break;
		case R.id.inputContact:
			break;
		default:
			break;
		}

	}

}
