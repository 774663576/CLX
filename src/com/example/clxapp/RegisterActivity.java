package com.example.clxapp;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.clx.util.HttpUrlHelper;
import com.example.clx.util.Logger;
import com.example.clx.util.SharedUtils;
import com.example.clx.util.Utils;
import com.example.clx.view.MyViewGroup;

/**
 * 注册界面包含多个注册子view
 * 
 * @author teeker_bin
 * 
 */
public class RegisterActivity extends Activity implements OnClickListener,
		OnItemSelectedListener {
	private Spinner mSpinner;// 注册界面的spinner
	private Spinner emailSpinner;// 邮箱注册界面的spinner
	private LinearLayout layLogin;
	private MyViewGroup rGroup;
	private View reg1, reg2, reg3, emailReg1, emilReg2;// 注册1、2、3界面和邮箱注意1、2界面
	private LayoutInflater flater;
	private LayoutParams params;
	private Button btnext;// 注册界面的下一步按钮
	private Button btfinish_yz;// 注册界面的完成验证按钮
	private TextView txtabout;// 注册界面的关于提示文字
	private Button btFinishRegister;// 注册界面的完成注册按钮
	private EditText ediNum;// 注册界面的输入手机号edittext
	private EditText ediPassword;// 注册界面的输入密码edittext
	private int rt;// 返回结果标志，1表示验证通过且生成新用户记录成功，其他值表示未成功TODO
	private String uid = "";// 成功后才有，代表用户ID
	private String token = "";
	private Button emailBtNext;// 邮箱注册的下一步按钮
	private String[] mItems;
	private ArrayAdapter<String> adapter;
	private EditText emailNum, emailEdit;// 邮箱注册界面的手机号码和邮箱控件
	private TextView emailTxt;// 邮箱注册界面的email显示控件
	private EditText code;// 注册界面的输入验证码edittext
	private EditText emailCode;// 邮箱注册界面的输入验证码edittext
	private Button emBtFinish;// 邮箱注册界面的完成验证按钮
	private TextView txtQh;// 注册界面显示区号的textveiw如显示+86等
	private TextView emailTxtQh;// 邮箱注册界面显示区号
	private Button btGetCode;// 注册界面重新获取验证码按钮
	private int second = 60;// 用于重新获取验证码时间倒计时
	private TextView txtShowNum;// 注册界面显示用来注册的手机号
	private ProgressDialog progressDialog;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				btGetCode.setText("重新获取验证码  " + "(" + second + "s)");
				second--;
				if (second < 0) {
					btGetCode.setText("重新获取验证码  ");
					btGetCode.setEnabled(true);
					removeMessages(0);
					btGetCode.setTextColor(Color.WHITE);

					return;
				}
				this.sendEmptyMessageDelayed(0, 1000);
				break;
			default:
				break;
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		initView();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		layLogin = (LinearLayout) findViewById(R.id.layLogin);
		layLogin.setOnClickListener(this);
		rGroup = (MyViewGroup) findViewById(R.id.regisGroup);
		params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		flater = LayoutInflater.from(this);
		reg1 = flater.inflate(R.layout.register1, null);
		reg2 = flater.inflate(R.layout.register2, null);
		reg3 = flater.inflate(R.layout.register3, null);
		emailReg1 = flater.inflate(R.layout.email_reg1, null);
		emilReg2 = flater.inflate(R.layout.email_reg2, null);
		rGroup.addView(reg1, params);
		// 建立数据源
		mItems = getResources().getStringArray(R.array.countrys);
		// 建立Adapter并且绑定数据源
		adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, mItems);
		initReg1View();
		initReg2View();
		initReg3View();
		emailTxt = (TextView) emilReg2.findViewById(R.id.emailTxt);
		emailCode = (EditText) emilReg2.findViewById(R.id.emailEdiCode);
		emBtFinish = (Button) emilReg2.findViewById(R.id.email_btfinish);
		emBtFinish.setOnClickListener(this);
		emailTxtQh = (TextView) emailReg1.findViewById(R.id.emailtxtQH);
		emailBtNext = (Button) emailReg1.findViewById(R.id.emailbtnext);
		emailBtNext.setOnClickListener(this);
		emailNum = (EditText) emailReg1.findViewById(R.id.emailnum);
		emailEdit = (EditText) emailReg1.findViewById(R.id.emailEdit);
		emailNum.setInputType(InputType.TYPE_CLASS_NUMBER);
		// emailNum.addTextChangedListener(new EditWather(2));
		emailSpinner = (Spinner) emailReg1.findViewById(R.id.email_spinner);
		emailSpinner.setAdapter(adapter);
	}

	/**
	 * 初始化注册界面1的控件
	 */
	private void initReg1View() {
		txtQh = (TextView) reg1.findViewById(R.id.txtQH);
		txtQh.setText("+86");
		btnext = (Button) reg1.findViewById(R.id.next);
		btnext.setOnClickListener(this);
		ediNum = (EditText) reg1.findViewById(R.id.num);
		ediNum.setInputType(InputType.TYPE_CLASS_NUMBER);
		ediNum.addTextChangedListener(new EditWather(1));
		mSpinner = (Spinner) reg1.findViewById(R.id.spinner1);
		// 绑定 Adapter到控件
		mSpinner.setAdapter(adapter);
		mSpinner.setOnItemSelectedListener(this);
	}

	/**
	 * 初始化注册界面2的控件
	 */
	private void initReg2View() {
		btfinish_yz = (Button) reg2.findViewById(R.id.btfinish_yz);
		btfinish_yz.setOnClickListener(this);
		code = (EditText) reg2.findViewById(R.id.editCode);
		btGetCode = (Button) reg2.findViewById(R.id.bt_get_code);
		btGetCode.setEnabled(false);
		btGetCode.setOnClickListener(this);
		btGetCode.setTextColor(Color.GRAY);
		txtShowNum = (TextView) reg2.findViewById(R.id.txt_show_num);
	}

	/**
	 * 初始化注册界面3的控件
	 */
	private void initReg3View() {
		btFinishRegister = (Button) reg3.findViewById(R.id.btfinish_register);
		btFinishRegister.setOnClickListener(this);
		txtabout = (TextView) reg3.findViewById(R.id.about);
		txtabout.setText(Html.fromHtml("<u>" + "关于注册时保护隐私的说明" + "</u>"));// 下划线
		ediPassword = (EditText) reg3.findViewById(R.id.editPassword);
	}

	/**
	 * 手机号码补空格
	 * 
	 * @author teeker_bin
	 * 
	 */
	class EditWather implements TextWatcher {
		EditText text;

		public EditWather(int type) {
			if (type == 1) {
				text = ediNum;
			} else {

				text = emailNum;
			}
		}

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
					text.setText(sb.toString());
					text.setSelection(5);
				}
				if (s.length() == 9) {
					sb.insert(8, "-");
					text.setText(sb.toString());
					text.setSelection(10);
				}

			} else if (count == 0) {
				if (s.length() == 4) {
					text.setText(s.subSequence(0, s.length() - 1));
					text.setSelection(3);
				}
				if (s.length() == 9) {
					text.setText(s.subSequence(0, s.length() - 1));
					text.setSelection(8);
				}
			}
		}

	}

	/**
	 * 是否注册成功
	 * 
	 * @param str
	 *            要解析的json字符串
	 * @return
	 */
	private boolean isSuccessRegister(String num, String email) {
		progressDialog = new ProgressDialog(this);
		progressDialog.show();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cellphone", num);
		map.put("email", email);
		String result = HttpUrlHelper.postData(map, "/users/iregister");
		progressDialog.dismiss();
		try {
			JSONObject object = new JSONObject(result);
			rt = object.getInt("rt");
			if (rt == 1) {
				uid = object.getString("uid");
				SharedUtils.setString("uid", uid);
				Logger.debug(this, "rt:" + rt + "  uid:" + uid);
				return true;
			} else {
				Utils.showToast("手机号码已被注册，请更换手机号码！");
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
			rt = object.getInt("rt");
			if (rt == 1) {
				token = object.getString("token");
				Logger.debug(this, "rt:" + rt + "  token:" + token);
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

	/**
	 * 校验验证码是否正确
	 * 
	 * @param strcode
	 * @return
	 */
	private boolean isSuccessCode(String strcode) {
		progressDialog = new ProgressDialog(this);
		progressDialog.show();
		Map<String, Object> mapYz = new HashMap<String, Object>();
		mapYz.put("uid", uid);
		mapYz.put("auth_code", strcode);
		String resultYz = HttpUrlHelper.postData(mapYz,
				"/users/iverifyAuthCode");
		progressDialog.dismiss();
		try {
			JSONObject object = new JSONObject(resultYz);
			rt = object.getInt("rt");
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
	 * 控件的点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layLogin:
			finish();
			overridePendingTransition(R.anim.slide_down_out,
					R.anim.register_down_out);
			break;
		case R.id.next:
			String txtNum = ediNum.getText().toString().replace("-", "");
			Logger.debug(this, "txt:" + txtNum);
			if (!Utils.isPhoneNum(txtNum)) {
				Utils.showToast("请输入有效的手机号码！");
				return;
			}
			new RegisterTask().execute(txtNum, "", "1");
			// if (isSuccessRegister(txtNum, "")) {
			// rGroup.setView(reg2);
			// txtShowNum.setText(replaceStr(txtNum));
			// Message msg = new Message();
			// msg.what = 0;
			// mHandler.sendEmptyMessage(0);
			// }
			break;
		case R.id.btfinish_yz:
			new CheckCodeTask().execute(code.getText().toString());
			// if (!isSuccessCode(code.getText().toString())) {
			// return;
			// }
			// rGroup.setView(reg3);
			break;
		case R.id.btfinish_register:
			new SetPasswordTask().execute();
			// Map<String, Object> map1 = new HashMap<String, Object>();
			// map1.put("uid", uid);
			// map1.put("passwd", ediPassword.getText().toString());
			// String result1 = HttpUrlHelper.postData(map1,
			// "/users/isetPasswd");
			// if (!isisSuccessSetPworl(result1)) {
			// return;
			// }
			// Intent intent = new Intent();
			// intent.setClass(this, RegisterFinishActivity.class);
			// startActivity(intent);
			// finish();
			break;
		case R.id.emailbtnext:
			String emtxt = emailEdit.getText().toString();
			if (!Utils.isEmail(emtxt)) {
				Utils.showToast("请输入正确的邮箱地址！");
				return;
			}
			new RegisterTask().execute(emailNum.getText().toString(), emtxt,
					"2");

			// if (!isSuccessRegister(emailNum.getText().toString(), emtxt)) {
			// return;
			// }
			// rGroup.setView(emilReg2);
			// emailTxt.setText(emtxt);
			break;
		case R.id.email_btfinish:
			new CheckCodeTask().execute(emailCode.getText().toString());

			// if (!isSuccessCode(emailCode.getText().toString())) {
			// return;
			// }
			// rGroup.setView(reg3);
			break;
		case R.id.bt_get_code:
			second = 60;
			btGetCode.setTextColor(Color.GRAY);
			btGetCode.setEnabled(false);
			Message msg = new Message();
			msg.what = 0;
			mHandler.sendEmptyMessage(0);
			break;
		default:
			break;
		}
	}

	/**
	 * 注册
	 * 
	 */
	class RegisterTask extends AsyncTask<String, Integer, String> {
		String txtnum = "";
		String type = "1";// 1短信注册 2 邮箱注册
		String emailtxt;

		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected String doInBackground(String... params) {
			txtnum = params[0];
			emailtxt = params[1];

			type = params[2];
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cellphone", params[0]);
			map.put("email", params[1]);
			String result = HttpUrlHelper.postData(map, "/users/iregister");
			Logger.debug(this, "result:" + result);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			try {
				JSONObject object = new JSONObject(result);
				rt = object.getInt("rt");
				if (rt == 1) {
					Message msg = new Message();
					msg.what = 0;
					mHandler.sendEmptyMessage(0);
					uid = object.getString("uid");
					if (type.equals("1")) {
						rGroup.setView(reg2);
						txtShowNum.setText(replaceStr(txtnum));
						return;
					}
					rGroup.setView(emilReg2);
					emailTxt.setText(emailtxt);

				} else {
					Utils.showToast("手机号码已被注册，请更换手机号码！");
				}
			} catch (JSONException e) {
				Logger.error(this, e);

				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			progressDialog = new ProgressDialog(RegisterActivity.this);
			progressDialog.show();
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
			mapYz.put("uid", uid);
			mapYz.put("auth_code", params[0]);
			mapYz.put("type", "register");
			Logger.debug(this, "uid:" + uid + "  code:" + params[0]);
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
				rt = object.getInt("rt");
				if (rt == 1) {
					rGroup.setView(reg3);
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
			progressDialog = new ProgressDialog(RegisterActivity.this);
			progressDialog.show();
		}
	}

	/**
	 * 设置密码
	 * 
	 */
	class SetPasswordTask extends AsyncTask<String, Integer, String> {

		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> map1 = new HashMap<String, Object>();
			map1.put("uid", uid);
			map1.put("passwd", ediPassword.getText().toString());
			String result = HttpUrlHelper.postData(map1, "/users/isetPasswd");
			Logger.debug(this, "result:" + result);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			try {
				JSONObject object = new JSONObject(result);
				rt = object.getInt("rt");
				if (rt == 1) {
					token = object.getString("token");
					Logger.debug(this, "rt:" + rt + "  token:" + token);
					Intent intent = new Intent();
					intent.setClass(RegisterActivity.this,
							RegisterFinishActivity.class);
					startActivity(intent);
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
			progressDialog = new ProgressDialog(RegisterActivity.this);
			progressDialog.show();
		}
	}

	/**
	 * 将手机号码的中间四位替换为****
	 * 
	 * @param str
	 * @return
	 */
	private String replaceStr(String str) {
		StringBuffer sb = new StringBuffer(str);
		for (int i = 3; i < 7; i++) {
			sb.deleteCharAt(i);
			sb.insert(i, "*");
		}
		return sb.toString();
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int posititon,
			long arg3) {
		if (posititon == 0) {
			return;
		}
		switch (posititon) {
		case 1:
			emailSpinner.setSelection(posititon);
			emailTxtQh.setText("+95");
			break;
		case 2:
			emailSpinner.setSelection(posititon);
			emailTxtQh.setText("+37");
			break;
		default:
			break;
		}
		rGroup.setView(emailReg1);

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}
