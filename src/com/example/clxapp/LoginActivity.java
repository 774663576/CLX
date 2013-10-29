package com.example.clxapp;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.clx.util.HttpUrlHelper;
import com.example.clx.util.Logger;
import com.example.clx.util.SharedUtils;
import com.example.clx.util.Utils;

/**
 * 登录界面
 * 
 * @author teeker_bin
 * 
 */
public class LoginActivity extends Activity implements OnClickListener {
	private LinearLayout btReg;// 去往注册界面按钮
	private Button btLogin;// 登录按钮
	private EditText ediNum;// 手机号码输入框
	private EditText ediPassword;// 密码输入框
	private String uid = "";// 成功后才有，代表用户ID
	private String token = "";
	private TextView btFindWd;// 找回密码按钮
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		initView();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		btFindWd = (TextView) findViewById(R.id.findpd);
		btFindWd.setOnClickListener(this);
		btReg = (LinearLayout) findViewById(R.id.btregister);
		btReg.setOnClickListener(this);
		btLogin = (Button) findViewById(R.id.btlogin);
		btLogin.setOnClickListener(this);
		ediNum = (EditText) findViewById(R.id.edtNum);
		ediNum.setInputType(InputType.TYPE_CLASS_NUMBER);
		ediNum.addTextChangedListener(new EditWather());
		ediPassword = (EditText) findViewById(R.id.edtPassword);
	}

	/**
	 * 手机号码分段显示操作
	 * 
	 * @author teeker_bin
	 * 
	 */
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

	/**
	 * 登录时判断用户名是否存在
	 * 
	 * @param str
	 * @return
	 */
	private boolean isUserExist(String str) {
		try {
			JSONObject object = new JSONObject(str);
			int rt = object.getInt("rt");
			if (rt == 1) {
				token = object.getString("token");
				uid = object.getString("uid");
				Logger.debug(this, "rt:" + rt + "  uid:" + uid + "  token:"
						+ token);
				return true;
			} else {
				Utils.showToast("用户名不存在");
				return false;
			}
		} catch (JSONException e) {
			Logger.error(this, e);
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * 控件的点击事件
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btregister:
			Intent intent = new Intent();
			intent.setClass(this, RegisterActivity.class);
			startActivity(intent);
			// activity切换动画显示 一定要放到startactivity 或finish()之后
			overridePendingTransition(R.anim.register_up_in,
					R.anim.slide_down_out);
			break;
		case R.id.btlogin:
			String num = ediNum.getText().toString().replace("-", "");
			if (!Utils.isPhoneNum(num)) {
				Utils.showToast("请输入有效的手机号码！");
				return;
			}
			if (ediPassword.getText().toString().length() == 0) {
				Utils.showToast("请输入密码！");
				return;
			}
			new LoginTask().execute(num);
			break;
		case R.id.findpd:
			Intent find = new Intent();
			find.setClass(this, FindPasswordActivity.class);
			startActivity(find);
			break;
		default:
			break;
		}

	}

	/**
	 * 异步提交修改数据到服务器
	 * 
	 */
	class LoginTask extends AsyncTask<String, Integer, String> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cellphone", params[0]);
			map.put("passwd", ediPassword.getText().toString());
			String result = HttpUrlHelper.postData(map, "/users/ilogin");
			Logger.debug(this, "LoginResult:" + result);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			if (!isUserExist(result)) {
				return;
			}
			SharedUtils.setString("uid", uid);
			SharedUtils.setString("token", token);
			Utils.uid = uid;
			Intent it = new Intent();
			it.setClass(LoginActivity.this, MainActivity.class);
			startActivity(it);
			finish();
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			progressDialog = new ProgressDialog(LoginActivity.this);
			progressDialog.show();
		}
	}
}
