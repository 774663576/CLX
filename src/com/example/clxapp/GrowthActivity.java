package com.example.clxapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.clx.adapter.GrowthAdapter;
import com.example.clx.db.DBUtils;
import com.example.clx.modle.GrowthImgModle;
import com.example.clx.modle.GrowthModle;
import com.example.clx.modle.MemberInfoModle;
import com.example.clx.util.HttpUrlHelper;
import com.example.clx.util.Logger;
import com.example.clx.util.SharedUtils;
import com.example.clx.util.StringUtils;
import com.example.clx.util.Utils;
import com.example.clx.view.MyListView;
import com.example.clx.view.MyListView.OnRefreshListener;

/**
 * 成长记录显示界面
 * 
 * @author teeker_bin
 * 
 */
public class GrowthActivity extends Activity implements OnClickListener {
	private String cid = "";
	private List<GrowthModle> listData = new ArrayList<GrowthModle>();
	private MyListView listview;
	private ProgressDialog progressDialog;
	private GrowthAdapter adapter;
	private ImageView btnRelease;// 发布成长按钮
	private String circleName;
	private TextView txtCirName;
	private ImageView btback;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_czjl);
		progressDialog = new ProgressDialog(this);
		cid = getIntent().getStringExtra("cirID");
		circleName = getIntent().getStringExtra("cirName");
		listview = (MyListView) findViewById(R.id.listview);
		adapter = new GrowthAdapter(this, listData);
		listview.setAdapter(adapter);
		listview.setCacheColorHint(0);
		listview.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				listview.onRefreshComplete();
			}
		});
		btnRelease = (ImageView) findViewById(R.id.btnRelease);
		btnRelease.setOnClickListener(this);
		txtCirName = (TextView) findViewById(R.id.circleName);
		txtCirName.setText(circleName);
		btback = (ImageView) findViewById(R.id.back);
		btback.setOnClickListener(this);

	}

	/**
	 * 发布完记录 重新加载数据
	 */
	@Override
	protected void onStart() {
		listData.clear();
		new GetDataTask().execute();
		super.onStart();
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
			map.put("token", SharedUtils.getString("token", ""));
			map.put("timestamp", 0);
			String result = HttpUrlHelper.postData(map, "/growth/ilist");
			Logger.debug(this, "result:" + result);
			try {
				JSONObject jsonobject = new JSONObject(result);
				String cid = jsonobject.getString("cid");
				String num = jsonobject.getString("num");
				String oldts = jsonobject.getString("oldts");
				String newts = jsonobject.getString("newts");
				JSONArray jsonarray = jsonobject.getJSONArray("growths");
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject object = (JSONObject) jsonarray.opt(i);
					GrowthModle modle = new GrowthModle();
					String id = object.getString("id");
					String uid = object.getString("uid");
					String content = object.getString("content");
					String location = object.getString("location");
					String happen = object.getString("happen");
					int praise = object.getInt("praise");
					int comment = object.getInt("comment");
					String publish = object.getString("publish");
					String isPraise = object.getString("mypraise");
					Logger.debug(this, "content:" + content);
					JSONArray imgrray = object.getJSONArray("images");
					List<GrowthImgModle> imgModle = new ArrayList<GrowthImgModle>();
					for (int j = 0; j < imgrray.length(); j++) {
						GrowthImgModle im = new GrowthImgModle();
						JSONObject imgObj = (JSONObject) imgrray.opt(j);
						String imgId = imgObj.getString("imgid");
						String img = imgObj.getString("img");
						im.setId(imgId);
						im.setImg(img);
						im.setSamllImg(StringUtils.JoinString(img));
						imgModle.add(im);
					}
					MemberInfoModle md = DBUtils.selectNameAndImgByID("circle"
							+ cid, uid);
					if (md != null) {
						modle.setName(md.getName());
						modle.setPersonImg(md.getAvator());
					}
					if (isPraise.equals("1")) {
						modle.setIspraise(true);
					} else {
						modle.setIspraise(false);
					}
					modle.setImgModle(imgModle);
					modle.setCid(cid);
					modle.setNum(num);
					modle.setOldts(oldts);
					modle.setNewts(newts);
					modle.setId(id);
					modle.setUid(uid);
					modle.setContent(content);
					modle.setLocation(location);
					modle.setHappen(happen);
					modle.setPraise(praise);
					modle.setComment(comment);
					modle.setPublish(publish);
					listData.add(modle);
				}
			} catch (JSONException e) {
				Logger.error(this, e);

				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			adapter.notifyDataSetChanged();

		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			progressDialog.show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnRelease:
			Intent it = new Intent();
			it.setClass(this, ReleaseGrowthActivity.class);
			it.putExtra("cid", cid);
			it.putExtra("type", "add");
			startActivity(it);
			break;
		case R.id.back:
			finish();
			break;
		default:
			break;
		}
	}

}
