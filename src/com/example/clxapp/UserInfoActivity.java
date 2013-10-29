package com.example.clxapp;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.clx.db.DataBase;
import com.example.clx.inteface.ChangeView;
import com.example.clx.modle.Info;
import com.example.clx.modle.MemberInfoModle;
import com.example.clx.util.ImageManager;
import com.example.clx.util.Logger;
import com.example.clx.util.WigdtContorl;
import com.example.clx.util.WigdtContorl.Visible;
import com.example.clx.view.MyViewGroup;
import com.example.clx.view.UserInfoEdit;
import com.example.clx.view.UserInfoShow;

/**
 * 用户资料显示界面
 * 
 * @author teeker_bin
 * 
 */
public class UserInfoActivity extends Activity implements Visible, ChangeView,
		OnClickListener {
	private RelativeLayout drag;
	private FrameLayout imgFrame;
	private Button scrollDrag;
	private String name = "";// 姓名
	private String cellPhone = "";// 电话
	private String email = "";// 邮箱
	private String avator = "";// 头像地址
	private String gendar = "";// 性别
	private String birthday = "";// 生日
	private String employer = "";// 工作单位
	private String jobTitle = "";// 工作职位
	private MemberInfoModle infoModle;
	private TextView txtnc, txtname;
	private ImageView imgback;
	private ImageView iconImg;
	private String iconPath;
	private DataBase dbase = DataBase.getInstance();
	private SQLiteDatabase db;
	private String userlistName;// 资料存储表
	private MyViewGroup rGroup;
	private LayoutInflater flater;
	private View vInfoShow, vEditor;
	private ImageView edtBasicInfo;
	private UserInfoShow infoShow;
	private UserInfoEdit vEdit;
	private LinearLayout layCall;
	private String pid;// 用户id
	private String cid;// 圈子id
	private String username;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.user_info);
		db = dbase.getWritableDatabase();
		iconPath = getIntent().getStringExtra("iconImg");
		userlistName = getIntent().getStringExtra("userlistname");
		username = getIntent().getStringExtra("username");
		pid = getIntent().getStringExtra("pid");
		cid = getIntent().getStringExtra("cid");
		infoModle = new MemberInfoModle();
		infoModle = (MemberInfoModle) getIntent().getSerializableExtra(
				"infoModle");
		layCall = (LinearLayout) findViewById(R.id.call);
		layCall.setOnClickListener(this);
		infoShow = new UserInfoShow(this, userlistName, pid, cid);
		flater = LayoutInflater.from(this);
		vEditor = flater.inflate(R.layout.user_info_editor, null);
		vInfoShow = flater.inflate(R.layout.user_info_show, null);
		rGroup = (MyViewGroup) findViewById(R.id.infoGroup);
		rGroup.addView(infoShow.getView());
		scrollDrag = (Button) infoShow.getView().findViewById(R.id.scrolldrag);
		scrollDrag.setOnTouchListener(MyTouchListener);
		drag = (RelativeLayout) findViewById(R.id.drag);
		imgFrame = (FrameLayout) findViewById(R.id.imgframe);
		drag.setOnTouchListener(MyTouchListener);
		drag.post(new Runnable() {
			@Override
			public void run() {
				WigdtContorl.delaultY = drag.getTop();
				Logger.debug(this, "delaultY:" + WigdtContorl.delaultY);
			}
		});
		txtname = (TextView) findViewById(R.id.name);
		txtname.setText(username);
		imgback = (ImageView) findViewById(R.id.back);
		imgback.setOnClickListener(this);
		iconImg = (ImageView) findViewById(R.id.img);
		ImageManager.from(this).displayImage(iconImg, iconPath, -1, 60, 60);
		iconImg.post(new Runnable() {

			@Override
			public void run() {
				int[] location = new int[2];
				// iconImg.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
				iconImg.getLocationOnScreen(location);// 获取在整个屏幕内的绝对坐标
				WigdtContorl.moveY = location[1] - iconImg.getHeight();
				Logger.debug(this, "moveY:" + WigdtContorl.moveY);

			}
		});
		WigdtContorl.setVisible(this);
		infoShow.setChangeView(this);
	}

	private void getInfo(String str) {
		try {
			JSONObject json = new JSONObject(str);
			String id = json.getString("id");
			JSONObject jsonObject = json.getJSONObject("basic");
			name = jsonObject.getString("name");
			cellPhone = jsonObject.getString("cellphone");
			email = jsonObject.getString("email");
			gendar = jsonObject.getString("gendar");
			birthday = jsonObject.getString("birthday");
			employer = jsonObject.getString("employer");
			jobTitle = jsonObject.getString("job_title");
			avator = jsonObject.getString("avatar");
			if (infoModle == null) {
				infoModle = new MemberInfoModle();
			}
			infoModle.setAvator(avator);
			infoModle.setBirthday(birthday);
			infoModle.setCellPhone(cellPhone);
			infoModle.setEmail(email);
			infoModle.setName(name);
			infoModle.setEmployer(employer);
			infoModle.setJobTitle(jobTitle);
			infoModle.setGendar(gendar);
			insertData(id, name, cellPhone, email, birthday, gendar, employer,
					jobTitle);
			Logger.debug(this, "name:" + name + "   cellPhone:" + cellPhone);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Logger.error(this, e);

			e.printStackTrace();
		}
	}

	private OnTouchListener MyTouchListener = new OnTouchListener() {
		int y1 = 0, y2;

		@SuppressLint("NewApi")
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				y1 = (int) event.getY();
				// 按住事件发生后执行代码的区域
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				// 移动事件发生后执行代码的区域
				y2 = (int) (y1 - event.getY());
				if (y2 > 0) {
					WigdtContorl.setLayoutY_UP(drag, y2, UserInfoActivity.this,
							imgFrame);
				} else {
					WigdtContorl.setLayoutY_Down(drag, y2,
							UserInfoActivity.this, imgFrame);
				}
				Logger.debug(this, "move:" + y2 + "  y1:" + y1);
				break;
			}
			case MotionEvent.ACTION_UP: {
				// 松开事件发生后执行代码的区域
				break;
			}
			default:
				break;
			}
			return true;
		}
	};

	/**
	 * 将用户详细信息存入数据库
	 * 
	 * @param id
	 * @param name
	 * @param phone
	 * @param email
	 * @param birthday
	 * @param gendar
	 * @param employer
	 * @param jobtitle
	 */
	private void insertData(String id, String name, String phone, String email,
			String birthday, String gendar, String employer, String jobtitle) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		ContentValues values = new ContentValues();
		// 想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
		values.put("userID", id);
		values.put("userPhone", phone);
		values.put("userEmail", email);
		values.put("userBirthday", birthday);
		values.put("userGendar", gendar);
		values.put("userEmployer", employer);
		values.put("userJobTitle", jobtitle);
		values.put("userName", name);
		db.insert(userlistName, null, values);
		db.close();
	}

	public void setVisible(boolean visible) {
		Animation ani1 = null;
		if (visible) {
			ani1 = AnimationUtils.loadAnimation(this,
					R.anim.alpha_animation_show);
			scrollDrag.setVisibility(View.GONE);

		} else {
			ani1 = AnimationUtils.loadAnimation(this,
					R.anim.alpha_animation_hidden);
			scrollDrag.setVisibility(View.VISIBLE);

		}
		imgFrame.setAnimation(ani1);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		// case R.id.edt_basic_info:
		// rGroup.setView(vEditor);
		// break;
		case R.id.call:
			infoShow.moveToCall();
			break;
		default:
			break;
		}

	}

	@Override
	public void setViewData(List<Info> data, int type, String cid, String pid,
			String tableName) {
		vEdit = new UserInfoEdit(this, data, type, cid, pid, tableName);
		vEdit.setChangeView(this);
		rGroup.setInfoEditView(vEdit.getView());
	}

	@Override
	public void delView() {
		rGroup.delView();
	}

	@Override
	public void NotifyData(List<Info> data, int infoType) {
		infoShow.refushData(data, infoType);
	}
}
