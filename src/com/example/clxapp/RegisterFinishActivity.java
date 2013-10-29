package com.example.clxapp;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.clx.util.HttpUrlHelper;
import com.example.clx.util.Logger;
import com.example.clx.util.SharedUtils;
import com.example.clx.util.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RegisterFinishActivity extends Activity implements OnClickListener {
	private Button btStartUse;
	private EditText editNC;
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register_finish);
		btStartUse = (Button) findViewById(R.id.startUse);
		btStartUse.setOnClickListener(this);
		editNC = (EditText) findViewById(R.id.editNC);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.startUse:
			new SetNickNameTask().execute();
			break;

		default:
			break;
		}
	}

	/**
	 * 设置昵称
	 * 
	 */
	class SetNickNameTask extends AsyncTask<String, Integer, String> {
		int rt;

		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> map1 = new HashMap<String, Object>();
			map1.put("uid", Utils.uid);
			map1.put("f", "nickname");
			map1.put("v", editNC.getText().toString());
			String result = HttpUrlHelper.postData(map1, "/users/isetUserInfo");
			Logger.debug(this, "SetNickNameresult:" + result);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			try {
				JSONObject object = new JSONObject(result);
				rt = object.getInt("rt");
				if (rt == 1) {
					Intent intent = new Intent();
					intent.setClass(RegisterFinishActivity.this,
							LoginActivity.class);
					startActivity(intent);
					finish();
				} else {
					Utils.showToast("昵称设置失败");
				}
			} catch (JSONException e) {
				Logger.error(this, e);

				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			progressDialog = new ProgressDialog(RegisterFinishActivity.this);
			progressDialog.show();
		}
	}
}
//