package com.example.clx.popwindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.clx.adapter.GrowthImgAdapter;
import com.example.clx.db.DBUtils;
import com.example.clx.modle.CommentsModle;
import com.example.clx.modle.GrowthModle;
import com.example.clx.modle.MemberInfoModle;
import com.example.clx.util.HttpUrlHelper;
import com.example.clx.util.ImageManager;
import com.example.clx.util.Logger;
import com.example.clx.util.SharedUtils;
import com.example.clx.util.Utils;
import com.example.clxapp.R;
import com.example.clxapp.ReleaseGrowthActivity;

/**
 * 成长记录详情界面
 */
public class GrowthCommentsPopwindow implements OnClickListener {
	private Context mContext;// 当前对象
	private View parent;
	private GrowthModle modle;
	private LayoutInflater flater;
	private View view;// 要加载的布局文件
	private PopupWindow popupWindow;// popWindow弹出框
	private ListView listview;
	private String cid;// 圈子id
	private String uid;// 用户id
	private String gid = "";// 成长记录id
	private List<CommentsModle> cModle = new ArrayList<CommentsModle>();
	private MyAdapter adapter;// 自定义adapter
	private TextView name;// 显示發佈人姓名
	private TextView content;// 显示记录内容
	private TextView time;// 显示记录发布时间
	private ImageView img;// 发布人头像
	private TextView praise;// 点赞的数量
	private TextView comment;// 评论数量
	private GridView gridView;// 用来展示记录中的图片
	private ImageView back;
	private Button btnPublis;// 回复按钮
	private EditText edtContent;// 评论内容
	private Button edit;// 编辑按钮
	private Button del;// 删除按钮
	private ArrayList<String> urlPath = new ArrayList<String>();// 存放图片的地址的本地图片路径编辑时使用
	private ArrayList<String> imgID = new ArrayList<String>();// 存放图片的地址的ID编辑时使用

	/**
	 * 构造函数
	 * 
	 * @param contxt
	 * @param parent
	 * @param modle
	 */
	public GrowthCommentsPopwindow(Context contxt, View parent,
			GrowthModle modle) {
		this.mContext = contxt;
		this.parent = parent;
		this.modle = modle;
		flater = LayoutInflater.from(contxt);
		view = flater.inflate(R.layout.growth_comments, null);
		initView();
		initPopwindow();
		cid = modle.getCid();
		gid = modle.getId();
		uid = modle.getUid();
		MemberInfoModle md = DBUtils.selectNameAndImgByID("circle" + cid, uid);
		if (md != null) {
			name.setText(md.getName());
			String path = md.getAvator();
			ImageManager.from(mContext).displayImage(img, path,
					R.drawable.hand_pic, 60, 60);
		}
		adapter = new MyAdapter();
		listview.setAdapter(adapter);
		new GetDataTask().execute();
	}

	/**
	 * . 初始化控件
	 */
	private void initView() {
		edit = (Button) view.findViewById(R.id.edit);
		edit.setOnClickListener(this);
		del = (Button) view.findViewById(R.id.del);
		del.setOnClickListener(this);
		btnPublis = (Button) view.findViewById(R.id.btPublish);
		btnPublis.setOnClickListener(this);
		edtContent = (EditText) view.findViewById(R.id.editContent);
		back = (ImageView) view.findViewById(R.id.back);
		back.setOnClickListener(this);
		gridView = (GridView) view.findViewById(R.id.gridView1);
		gridView.setAdapter(new GrowthImgAdapter(mContext, modle.getImgModle()));
		praise = (TextView) view.findViewById(R.id.praise);
		comment = (TextView) view.findViewById(R.id.comments);
		name = (TextView) view.findViewById(R.id.name);
		time = (TextView) view.findViewById(R.id.time);
		content = (TextView) view.findViewById(R.id.content);
		img = (ImageView) view.findViewById(R.id.img);
		time.setText(modle.getPublish());
		content.setText(modle.getContent());
		comment.setText("评论(" + modle.getComment() + ")");
		praise.setText("赞(" + modle.getPraise() + ")");
		listview = (ListView) view.findViewById(R.id.listView);
	}

	/**
	 * 初始化popwindow
	 */
	@SuppressWarnings("deprecation")
	private void initPopwindow() {

		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setAnimationStyle(R.style.LeftAndRight);
	}

	/**
	 * popwindow的显示
	 */
	public void show() {
		popupWindow.showAtLocation(parent, Gravity.BOTTOM
				| Gravity.CENTER_HORIZONTAL, 0, 0);
		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		// 刷新状态
		popupWindow.update();
	}

	/**
	 * popwindow的隐藏
	 */
	public void dismiss() {
		popupWindow.dismiss();
	}

	/**
	 * 判断是否有权限进行编辑
	 * 
	 * @param uid
	 * @return
	 */
	private boolean isPermission(String uid) {
		if (uid.equals(Utils.uid)) {
			return true;
		}
		return false;
	}

	/**
	 * 从服务器获取评论内容
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
			map.put("gid", gid);
			String result = HttpUrlHelper.postData(map, "/growth/icomments");
			Logger.debug(this, "result:" + result);
			try {
				JSONObject jsonobject = new JSONObject(result);
				String cid = jsonobject.getString("cid");
				String gid = jsonobject.getString("gid");
				String num = jsonobject.getString("num");
				JSONArray jsonarray = jsonobject.getJSONArray("comments");
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject object = (JSONObject) jsonarray.opt(i);
					CommentsModle modle = new CommentsModle();
					String id = object.getString("id");
					String uid = object.getString("uid");
					String content = object.getString("content");
					String replyid = object.getString("replyid");
					String time = object.getString("time");
					modle.setCid(cid);
					modle.setContent(content);
					modle.setGid(gid);
					modle.setId(id);
					modle.setNum(num);
					modle.setReplyid(replyid);
					modle.setUid(uid);
					modle.setTime(time);
					cModle.add(modle);
				}
			} catch (JSONException e) {
				Logger.error(this, e);
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			adapter.notifyDataSetChanged();
			Utils.setListViewHeightBasedOnChildren(listview);
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理

		}
	}

	/**
	 * 提交回复评论
	 * 
	 */
	class PublishCommentsTask extends AsyncTask<String, Integer, String> {
		String count;
		String rt = "";

		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cid", cid);
			map.put("uid", Utils.uid);
			map.put("gid", gid);
			map.put("token", SharedUtils.getString("token", ""));
			map.put("content", params[0]);
			String result = HttpUrlHelper.postData(map, "/growth/imyComment");
			Logger.debug(this, "result:" + result);
			try {
				JSONObject jsonobject = new JSONObject(result);
				rt = jsonobject.getString("rt");
				if (rt.equals("1")) {
					count = jsonobject.getString("count");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Logger.error(this, e);
				e.printStackTrace();
			}
			return rt;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("1")) {
				comment.setText("评论(" + count + ")");
				Utils.showToast("评论成功!");

			} else {
				Utils.showToast("评论失败!");
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
		}
	}

	/**
	 * 删除记录
	 * 
	 */
	class DelCommentsTask extends AsyncTask<String, Integer, String> {
		String rt = "";

		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected String doInBackground(String... params) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cid", cid);
			map.put("uid", Utils.uid);
			map.put("gid", gid);
			map.put("token", SharedUtils.getString("token", ""));
			String result = HttpUrlHelper
					.postData(map, "/growth/iremoveGrowth");
			Logger.debug(this, "result:" + result);
			try {
				JSONObject jsonobject = new JSONObject(result);
				rt = jsonobject.getString("rt");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Logger.error(this, e);
				e.printStackTrace();
			}
			return rt;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("1")) {
				Utils.showToast("删除成功！");
				dismiss();
			} else {
				Utils.showToast("删除失败！");
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
		}
	}

	/**
	 * 自定义adapter用来显示评论内容
	 * 
	 * @author teeker_bin
	 * 
	 */
	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return cModle.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = flater.inflate(R.layout.growth_comments_item,
						null);
				holder.content = (TextView) convertView
						.findViewById(R.id.content);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.time = (TextView) convertView.findViewById(R.id.time);
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			setNameAndImg("circle" + cModle.get(position).getCid(),
					cModle.get(position).getUid(), holder);
			holder.content.setText(cModle.get(position).getContent());
			holder.time.setText(cModle.get(position).getTime());
			return convertView;
		}
	}

	/**
	 * 根据id设置姓名和头像
	 * 
	 * @param tableName
	 *            //表名称
	 * @param id
	 * @param holder
	 */
	private void setNameAndImg(String tableName, String id, ViewHolder holder) {
		MemberInfoModle modle = new MemberInfoModle();
		modle = DBUtils.selectNameAndImgByID(tableName, id);
		String name = modle.getName();
		String path = modle.getAvator();
		holder.name.setText(name);
		ImageManager.from(mContext).displayImage(holder.img, path,
				R.drawable.hand_pic, 60, 60);

	}

	class ViewHolder {
		ImageView img;
		TextView name;
		TextView time;
		TextView content;
	}

	/**
	 * 点击事件的处理
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			dismiss();
			break;
		case R.id.btPublish:
			String str = edtContent.getText().toString();
			if (str.length() == 0) {
				Utils.showToast("请输入内容！");
				return;
			}
			new PublishCommentsTask().execute(str);
			break;
		case R.id.edit:
			if (isPermission(modle.getUid())) {
				for (int i = 0; i < modle.getImgModle().size(); i++) {
					urlPath.add(modle.getImgModle().get(i).getSamllImg());
					imgID.add(modle.getImgModle().get(i).getId());
				}
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("path", urlPath);
				bundle.putStringArrayList("imgID", imgID);
				intent.putExtras(bundle);
				intent.setClass(mContext, ReleaseGrowthActivity.class);
				intent.putExtra("type", "edit");
				intent.putExtra("cid", cid);
				intent.putExtra("gid", gid);
				intent.putExtra("content", content.getText());
				mContext.startActivity(intent);
				dismiss();
				return;
			}
			Utils.showToast("您没有编辑权限！");
			break;
		case R.id.del:
			if (isPermission(modle.getUid())) {
				new DelCommentsTask().execute();
				return;
			}
			Utils.showToast("您没有删除权限！");
			break;
		default:
			break;
		}
	}
}
