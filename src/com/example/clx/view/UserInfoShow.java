package com.example.clx.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.clx.db.DBUtils;
import com.example.clx.db.DataBase;
import com.example.clx.inteface.ChangeView;
import com.example.clx.modle.Info;
import com.example.clx.util.HttpUrlHelper;
import com.example.clx.util.Logger;
import com.example.clx.util.SharedUtils;
import com.example.clx.util.UserInfoUtils;
import com.example.clx.util.Utils;
import com.example.clxapp.R;

/**
 * 用户资料显示View 主要对用户资料的分类显示
 * 
 * @author teeker_bin
 * 
 */

public class UserInfoShow {
	private Context mContext;
	private View infoShow;
	private List<Info> list = new ArrayList<Info>();;
	private List<String> groupkey = new ArrayList<String>();
	private List<Info> basicList = new ArrayList<Info>();// 存放基本信息数据
	private List<Info> socialList = new ArrayList<Info>();// 存放社交账号数据
	private List<Info> contactList = new ArrayList<Info>();// 存放联系方式数据
	private List<Info> addressList = new ArrayList<Info>();// 存放地址数据
	private List<Info> otherList = new ArrayList<Info>();// 存放其他数据
	private List<Info> eduList = new ArrayList<Info>();// 存放教育经历
	private List<Info> workList = new ArrayList<Info>();// 存放工作经历
	private ListView listview;
	private ChangeView cv;
	private MyAdapter adapter;
	private String tableName;
	private DataBase dbase = DataBase.getInstance();
	private SQLiteDatabase db;
	private String pid;
	private String cid;

	/**
	 * 
	 * @param context
	 * @param str
	 *            要解析的资料Json串
	 * @param tableName存储的表名
	 * @param pid
	 *            用户id
	 */
	public UserInfoShow(Context context, String tableName, String pid,
			String cid) {
		this.mContext = context;
		this.tableName = tableName;
		this.pid = pid;
		this.cid = cid;
		db = dbase.getWritableDatabase();
		infoShow = LayoutInflater.from(context).inflate(
				R.layout.user_info_show, null);
		listview = (ListView) infoShow.findViewById(R.id.listView_list);
		if (DBUtils.isExistOfPersonId(tableName, pid)) {
			getUserInfo(tableName, pid);
			Logger.debug(this, "本地资料");
		} else {
			new GetDataTask().execute();
			Logger.debug(this, "服务器资料");
		}
		initData();
		adapter = new MyAdapter();
		setMyAdapter();
	}

	public View getView() {
		return infoShow;
	}

	public void initData() {
		for (int i = 0; i < UserInfoUtils.infoTitleKey.length; i++) {
			groupkey.add(UserInfoUtils.infoTitleKey[i]);
		}
		Info basicKey = new Info();
		basicKey.setKey(UserInfoUtils.infoTitleKey[0]);
		list.add(basicKey);
		list.addAll(basicList);
		Info contactKey = new Info();
		contactKey.setKey(UserInfoUtils.infoTitleKey[1]);
		list.add(contactKey);
		list.addAll(contactList);
		Info socialKey = new Info();
		socialKey.setKey(UserInfoUtils.infoTitleKey[2]);
		list.add(socialKey);
		list.addAll(socialList);
		Info addressKey = new Info();
		addressKey.setKey(UserInfoUtils.infoTitleKey[3]);
		list.add(addressKey);
		list.addAll(addressList);
		Info otherKey = new Info();
		otherKey.setKey(UserInfoUtils.infoTitleKey[4]);
		list.add(otherKey);
		list.addAll(otherList);
		Info eduKey = new Info();
		eduKey.setKey(UserInfoUtils.infoTitleKey[5]);
		list.add(eduKey);
		list.addAll(eduList);
		Info wordKey = new Info();
		wordKey.setKey(UserInfoUtils.infoTitleKey[6]);
		list.add(wordKey);
		list.addAll(workList);
	}

	/**
	 * 查找圈子成员信息
	 * 
	 * @param userlistNmae圈子成员表名
	 * @param pid成员ID
	 * @return
	 */
	private void getUserInfo(String userlistNmae, String pid) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		Cursor cursor = db.query(userlistNmae, null, "personID='" + pid + "'",
				null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				String tid = cursor.getString(cursor.getColumnIndex("tID"));
				String key = cursor.getString(cursor.getColumnIndex("key"));
				String value = cursor.getString(cursor.getColumnIndex("value"));
				String start = cursor.getString(cursor
						.getColumnIndex("startDate"));
				String end = cursor.getString(cursor.getColumnIndex("endDate"));
				Logger.debug(this, "key:" + key + "   value:" + value);
				dataClassification(tid, key, value, start, end);
				cursor.moveToNext();
			}
		}
		cursor.close();
		db.close();
	}

	private void setMyAdapter() {
		listview.setAdapter(adapter);
	}

	/**
	 * 点击打电话时 listview移动到联系方式
	 */
	public void moveToCall() {
		for (int i = 0; i < list.size(); i++) {
			if ("联系方式".equals(list.get(i).getKey())) {
				listview.setSelection(i);
				break;

			}
		}

	}

	/**
	 * 才从服务器获取数据
	 * 
	 */
	class GetDataTask extends AsyncTask<String, Integer, String> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cid", cid);
			map.put("uid", Utils.uid);
			map.put("pid", pid);
			map.put("token", SharedUtils.getString("token", ""));
			map.put("timestamp", 0);
			String json = HttpUrlHelper.postData(map, "/people/idetail");
			try {
				JSONObject jsonobject = new JSONObject(json);
				JSONArray jsonarray = jsonobject.getJSONArray("person");
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject object = (JSONObject) jsonarray.opt(i);
					String start = "";
					String end = "";
					String id = object.getString("id");// 属性id
					String key = object.getString("type");// 属性名称
					String value = object.getString("value");// 属性值
					if (Arrays.toString(UserInfoUtils.eduStr).contains(key)) {
						start = object.getString("start");//
						end = object.getString("end");//
					}
					Logger.debug(this, "id:" + id + "  key:" + key + "  value:"
							+ value + "  start:" + start + "  end" + end);
					dataClassification(id, key, value, start, end);
					insertData(id, pid, key, value, start, end);
				}
			} catch (JSONException e) {
				Logger.error(this, e);
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			list.clear();
			initData();
			adapter.notifyDataSetChanged();
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
		}
	}

	/**
	 * 数据分类
	 * 
	 * @param id
	 * @param key
	 * @param value
	 */
	private void dataClassification(String id, String key, String value,
			String start, String end) {
		Info info = new Info();
		info.setValue(value);
		info.setId(id);
		info.setType(key);
		String typekey = "";
		if (Arrays.toString(UserInfoUtils.basicStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			basicList.add(info);
		} else if (Arrays.toString(UserInfoUtils.socialStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			socialList.add(info);
		} else if (Arrays.toString(UserInfoUtils.contactStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			contactList.add(info);
		} else if (Arrays.toString(UserInfoUtils.addressStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			addressList.add(info);
		} else if (Arrays.toString(UserInfoUtils.otherStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			otherList.add(info);
		} else if (Arrays.toString(UserInfoUtils.eduStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			info.setStartDate(start);
			info.setEndDate(end);
			eduList.add(info);
		} else if (Arrays.toString(UserInfoUtils.workStr).contains(key)) {
			typekey = UserInfoUtils.convertToChines(key);
			info.setKey(typekey);
			workList.add(info);
		}
	}

	/**
	 * 从编辑界面回到显示界面之后进行数据更新
	 * 
	 * @param data
	 *            修改之後的数据
	 * @param infoType
	 *            修改的资料类型
	 */
	public void refushData(List<Info> data, int infoType) {
		switch (infoType) {
		case 0:
			basicList = data;
			break;
		case 1:
			contactList = data;
			break;
		case 2:
			socialList = data;
			break;
		case 3:
			addressList = data;
			break;
		case 4:
			otherList = data;
			break;
		case 5:
			eduList = data;
			break;
		case 6:
			workList = data;
			break;
		default:
			break;
		}
		list.clear();
		initData();
		adapter.notifyDataSetChanged();

	}

	private void insertData(String tid, String personId, String key,
			String value, String start, String end) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		ContentValues values = new ContentValues();
		// 想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
		values.put("tID", tid);
		values.put("personID", personId);
		values.put("key", key);
		values.put("value", value);
		values.put("startDate", start);
		values.put("endDate", end);
		System.out
				.println("insert:     " + "key:" + key + "    value:" + value);
		db.insert(tableName, null, values);
		db.close();
	}

	/**
	 * 自定义的Adapter
	 * 
	 * @author teeker_bin
	 * 
	 */
	private class MyAdapter extends BaseAdapter {
		ViewHolder holder = null;

		public MyAdapter() {
			holder = new ViewHolder();
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public boolean isEnabled(int position) {
			if (groupkey.contains(getItem(position))) {
				return false;
			}
			return super.isEnabled(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (groupkey.contains(list.get(position).getKey())) {

				view = LayoutInflater.from(mContext).inflate(
						R.layout.info_show_list_item_tag, null);
				holder.titleKey = (TextView) view.findViewById(R.id.key);
				holder.titleKey.setText(list.get(position).getKey());
				holder.edit = (ImageView) view.findViewById(R.id.imgEdit);
				holder.edit.setOnClickListener(new BtnClick(holder.titleKey
						.getText().toString()));
				if (position == 0) {
					holder.line = (ImageView) view
							.findViewById(R.id.imageView1);
					holder.line.setVisibility(View.GONE);
				}
				return view;
			} else {
				if (Arrays.toString(UserInfoUtils.eduStr).contains(
						list.get(position).getType())) {
					view = LayoutInflater.from(mContext).inflate(
							R.layout.info_edu_work_show_item, null);
					holder.startTimeAndEndTime = (TextView) view
							.findViewById(R.id.startTimeAndEndTime);
					holder.eduAndWordContent = (TextView) view
							.findViewById(R.id.eduAndWordContent);
					holder.startTimeAndEndTime.setText(list.get(position)
							.getStartDate()
							+ "-"
							+ list.get(position).getEndDate());
					holder.eduAndWordContent.setText(list.get(position)
							.getValue());
					return view;
				}
				view = LayoutInflater.from(mContext).inflate(
						R.layout.info_show_list_item, null);
				holder.key = (TextView) view.findViewById(R.id.key);
				holder.value = (TextView) view.findViewById(R.id.value);
				holder.sms = (ImageView) view.findViewById(R.id.imgSms);
				holder.tel = (ImageView) view.findViewById(R.id.imgTel);
				holder.email = (ImageView) view.findViewById(R.id.imgEmail);
				if (list.get(position).getKey().contains("手机")) {
					holder.tel.setVisibility(View.VISIBLE);
					holder.sms.setVisibility(View.VISIBLE);
				} else if (list.get(position).getKey().contains("电话")) {
					holder.tel.setVisibility(View.VISIBLE);
					holder.sms.setVisibility(View.GONE);
				} else if (list.get(position).getKey().contains("E-Mail")) {
					holder.tel.setVisibility(View.GONE);
					holder.sms.setVisibility(View.GONE);
					holder.email.setVisibility(View.VISIBLE);

				} else {
					holder.tel.setVisibility(View.GONE);
					holder.sms.setVisibility(View.GONE);
					holder.email.setVisibility(View.GONE);
				}

				holder.email.setOnClickListener(new BtnClick(list.get(position)
						.getValue()));
				holder.tel.setOnClickListener(new BtnClick(list.get(position)
						.getValue()));
				holder.sms.setOnClickListener(new BtnClick(list.get(position)
						.getValue()));
				holder.key.setText(list.get(position).getKey() + ":");
				holder.value.setText(list.get(position).getValue());
				return view;
			}
		}
	}

	class BtnClick implements OnClickListener {
		String str;

		public BtnClick() {
		}

		public BtnClick(String str) {
			this.str = str;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.imgSms:
				sendMessage(str);
				break;
			case R.id.imgTel:
				callPhone(str);
				break;
			case R.id.imgEmail:
				sendEmail(str);
				break;
			case R.id.imgEdit:
				if (str.equals(UserInfoUtils.infoTitleKey[0])) {
					cv.setViewData(basicList, 0, cid, pid, tableName);
				} else if (str.equals(UserInfoUtils.infoTitleKey[1])) {
					cv.setViewData(contactList, 1, cid, pid, tableName);
				} else if (str.equals(UserInfoUtils.infoTitleKey[2])) {
					cv.setViewData(socialList, 2, cid, pid, tableName);
				} else if (str.equals(UserInfoUtils.infoTitleKey[3])) {
					cv.setViewData(addressList, 3, cid, pid, tableName);
				} else if (str.equals(UserInfoUtils.infoTitleKey[4])) {
					cv.setViewData(otherList, 4, cid, pid, tableName);
				} else if (str.equals(UserInfoUtils.infoTitleKey[5])) {
					cv.setViewData(eduList, 5, cid, pid, tableName);
				} else if (str.equals(UserInfoUtils.infoTitleKey[6])) {
					cv.setViewData(workList, 6, cid, pid, tableName);
				}
				break;
			default:
				break;
			}

		}

	}

	/**
	 * 拨打电话
	 * 
	 * @param num
	 */
	private void callPhone(String num) {
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + num));
		mContext.startActivity(intent);

	}

	/**
	 * @param num
	 */
	private void sendMessage(String num) {
		Uri uri = Uri.parse("smsto:" + num);
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		it.putExtra("sms_body", "");
		mContext.startActivity(it);
	}

	/**
	 * 发送email
	 * 
	 * @param num
	 */
	private void sendEmail(String str) {
		// 系统邮件系统的动作为android.content.Intent.ACTION_SEND
		Uri uri = Uri.parse("mailto:" + str);
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		mContext.startActivity(it);
	}

	class ViewHolder {
		ImageView line;
		TextView key;
		TextView value;
		ImageView sms;
		ImageView tel;
		ImageView email;
		ImageView edit;
		TextView titleKey;
		TextView startTimeAndEndTime;
		TextView eduAndWordContent;
	}

	public void setChangeView(ChangeView cv) {
		this.cv = cv;
	}

}
