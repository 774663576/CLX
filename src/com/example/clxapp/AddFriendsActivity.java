package com.example.clxapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AddFriendsActivity extends Activity {
	private Button add;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friends);
		add = (Button) findViewById(R.id.addContacts);
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(AddFriendsActivity.this,
						SelectContactsActivity.class);
				startActivity(intent);
			}
		});
	}

}
