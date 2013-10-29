package com.example.clxapp;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.clx.util.HttpUrlHelper;
import com.example.clx.util.Logger;
import com.example.clx.util.SharedUtils;
import com.example.clx.util.Utils;
import com.example.clx.view.MyViewGroup;

/**
 * 找回密码界面包含多个子view
 * 
 * @author teeker_bin
 * 
 */
public class FindPasswordActivity extends Activity implements OnClickListener {
	private MyViewGroup group;
	private LayoutInflater flater;
	private View find1, find2, find3;// 找回密码的三个界面
	private Button btnext;// 界面1的下一步按钮
	private Button btfinishYz;// 界面2的完成验证按钮
	private Button btfinish;// 界面3的完成按钮
	private LayoutParams params;
	private ImageView btback;
	private EditText ediNum;// 手机号输入框
	private EditText ediCode;// 验证码输入框
	private EditText ediPasswd;// 密码输入框
	private ProgressDialog progressDialog;
	private String uid;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_find_password);
		group = (MyViewGroup) findViewById(R.id.myGroup);
		flater = LayoutInflater.from(this);
		params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		find1 = flater.inflate(R.layout.find_word1, null);
		find2 = flater.inflate(R.layout.find_word2, null);
		find3 = flater.inflate(R.layout.find_word3, null);
		group.addView(find1, params);
		btback = (ImageView) findViewById(R.id.btback);
		btback.setOnClickListener(this);
		initFind1View();
		initFind2View();
		initFind3View();
	}

	/**
	 * 初始化找回密码界面1的控件
	 */
	private void initFind1View() {
		btnext = (Button) find1.findViewById(R.id.btnext);
		btnext.setOnClickListener(this);
		ediNum = (EditText) find1.findViewById(R.id.editnum);
		ediNum.addTextChangedListener(new EditWather());
		ediNum.setInputType(InputType.TYPE_CLASS_NUMBER);
	}

	/**
	 * 初始化找回密码界面2的控件
	 */
	private void initFind2View() {
		btfinishYz = (Button) find2.findViewById(R.id.btfinish_yz);
		btfinishYz.setOnClickListener(this);
		ediCode = (EditText) find2.findViewById(R.id.editCode);
	}

	/**
	 * 初始化找回密码界面3的控件
	 */
	private void initFind3View() {
		btfinish = (Button) find3.findViewById(R.id.btfinish);
		btfinish.setOnClickListener(this);
		ediPasswd = (EditText) find3.findViewById(R.id.editPassword);
	}

	class EditWather implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			StringBuffer sb = new StringBuffer(s);
			if (count == 1) {
				if (s.length() == 4) {
					sb.insert(3, "-");
					ediNum.setText(sb.toString());
					ediNum.setSelection(5);
				}
				if (s.length() == 9) {
					sb.insert(8, "-");
					ediNum.setText(sb.toString());
					ediNum.setSelection(10);
				}
				// if (s.length() == 11) {
				// ediNum.setText(s + " ");
				// ediNum.setSelection(11);
				// }

			} else if (count == 0) {
				if (s.length() == 4) {
					ediNum.setText(s.subSequence(0, s.length() - 1));
					ediNum.setSelection(3);
				}
				if (s.length() == 9) {
					ediNum.setText(s.subSequence(0, s.length() - 1));
					ediNum.setSelection(8);
				}
				// if (s.length() == 11) {
				// ediNum.setText(s.subSequence(0, s.length() - 1));
				// ediNum.setSelection(10);
				// }
			}
		}
	}

	/***
	 * 验证用户是否存在
	 * 
	 * @param str
	 * @return
	 */
	private boolean isUserExist(String str) {
		try {
			JSONObject object = new JSONObject(str);
			int rt = object.getInt("rt");
			if (rt == 1) {
				return true;
			} else {
				Utils.showToast("手机号码不存在");
				return false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * 校验验证码是否有效
	 * 
	 * @param str
	 * @return
	 */
	private boolean isSuccessCode(String str) {
		try {
			JSONObject object = new JSONObject(str);
			int rt = object.getInt("rt");
			if (rt == 1) {
				return true;
			} else {
				Utils.showToast("验证失败");
				return false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 密码是否设置成功
	 * 
	 * @param str
	 * @return
	 */
	private boolean isisSuccessSetPworl(String str) {
		try {
			JSONObject object = new JSONObject(str);
			int rt = object.getInt("rt");
			if (rt == 1) {
				return true;
			} else {
				Utils.showToast("密码设置失败");
				return false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnext:
			new FindPasswordTask().execute();
			// Map<String, Object> map = new HashMap<String, Object>();
			// map.put("cellphone", ediNum.getText().toString().replace("-",
			// ""));
			// String result = HttpUrlHelper.postData(map,
			// "/users/iretrievePassword");
			// if (isUserExist(result)) {
			// group.setView(find2);
			// }
			break;
		case R.id.btfinish_yz:
			new CheckCodeTask().execute();
			// Map<String, Object> mapYz = new HashMap<String, Object>();
			// mapYz.put("uid", SharedUtils.getString("uid", ""));
			// mapYz.put("auth_code", ediCode.getText().toString());
			// mapYz.put("type", "retrievePasswd");
			// String resultYz = HttpUrlHelper.postData(mapYz,
			// "/users/iverifyAuthCode");
			// if (!isSuccessCode(resultYz)) {
			// return;
			// }
			// group.setView(find3);
			break;
		case R.id.btback:
			finish();
			break;
		case R.id.btfinish:
			new SetPasswordTask().execute();
			// Map<String, Object> mapPwd = new HashMap<String, Object>();
			// mapPwd.put("uid", SharedUtils.getString("uid", ""));
			// mapPwd.put("passwd", ediPasswd.getText().toString());
			// String result1 = HttpUrlHelper
			// .postData(mapPwd, "/users/isetPasswd");
			// if (!isisSuccessSetPworl(result1)) {
			// return;
			// }
			// finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 校验验证码是否正确
	 * 
	 */
	class CheckCodeTask extends AsyncTask<String, Integer, String> {

		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> mapYz = new HashMap<String, Object>();
			mapYz.put("uid", Utils.uid);
			mapYz.put("auth_code", ediCode.getText().toString());
			mapYz.put("type", "retrievePasswd");
			String result = HttpUrlHelper.postData(mapYz,
					"/users/iverifyAuthCode");
			Logger.debug(this, "result:" + result);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			try {
				JSONObject object = new JSONObject(result);
				int rt = object.getInt("rt");
				if (rt == 1) {
					group.setView(find3);
				} else {
					Utils.showToast("验证失败");
				}
			} catch (JSONException e) {
				Logger.error(this, e);

				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			progressDialog = new ProgressDialog(FindPasswordActivity.this);
			progressDialog.show();
		}
	}

	/**
	 * 找回密码接口
	 * 
	 */
	class FindPasswordTask extends AsyncTask<String, Integer, String> {

		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cellphone", ediNum.getText().toString().replace("-", ""));
			Logger.debug(this, "cellphone:"
					+ ediNum.getText().toString().replace("-", ""));
			String result = HttpUrlHelper.postData(map,
					"/users/iretrievePassword");
			Logger.debug(this, "result:" + result);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			try {
				JSONObject object = new JSONObject(result);
				int rt = object.getInt("rt");
				if (rt == 1) {
					uid = object.getString("uid");
					SharedUtils.setString("uid", uid);
					Utils.uid = uid;
					group.setView(find2);

				} else {
					Utils.showToast("手机号码不存在");
				}
			} catch (JSONException e) {
				Logger.error(this, e);

				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			progressDialog = new ProgressDialog(FindPasswordActivity.this);
			progressDialog.show();
		}
	}

	/**
	 * 设置密码接口
	 * 
	 */
	class SetPasswordTask extends AsyncTask<String, Integer, String> {

		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> mapPwd = new HashMap<String, Object>();
			mapPwd.put("uid", Utils.uid);
			mapPwd.put("passwd", ediPasswd.getText().toString());
			String result = HttpUrlHelper.postData(mapPwd, "/users/isetPasswd");
			Logger.debug(this, "result:" + result);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			try {
				JSONObject object = new JSONObject(result);
				int rt = object.getInt("rt");
				if (rt == 1) {
					finish();
				} else {
					Utils.showToast("密码设置失败");
				}
			} catch (JSONException e) {
				Logger.error(this, e);

				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			progressDialog = new ProgressDialog(FindPasswordActivity.this);
			progressDialog.show();
		}
	}
}
