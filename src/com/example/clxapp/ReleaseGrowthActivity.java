package com.example.clxapp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clx.util.BitmapUtils;
import com.example.clx.util.FileUtils;
import com.example.clx.util.HttpUrlHelper;
import com.example.clx.util.InfoHelper;
import com.example.clx.util.Logger;
import com.example.clx.util.MediaUtils;
import com.example.clx.util.SharedUtils;
import com.example.clx.util.StringUtils;
import com.example.clx.util.Utils;
import com.example.clx.view.GrowthImgGridView;

public class ReleaseGrowthActivity extends Activity implements OnClickListener {
	private ImageView addPic;
	private Button btpick;
	private Button btcamear;
	private Button btcancle;
	private static final int REQUEST_CODE_GETIMAGE_BYSDCARD = 0;// 选择图片
	private static final int REQUEST_CODE_GETIMAGE_BYCAMERA = 1;// 拍照
	private String thisLarge = null, theSmall = null;
	private PopupWindow mPopupWindow;
	private TextView time;
	private EditText location;// 地点输入框
	private EditText content;// 内容输入框
	private String cid;
	private ProgressDialog progressDialog;
	private Button btnUpload;
	private ImageView btnback;
	private String gid = "";// 成长记录id
	private GrowthImgGridView gridView;
	private List<PicModle> listBmp = new ArrayList<PicModle>();
	private List<String> newAdd = new ArrayList<String>();// 保存新增图片地址 编辑成長记录时使用
	private List<String> delID = new ArrayList<String>();// 保存新增刪除的图片记录id
															// 编辑成長记录时使用
	private MyAdapter adapter;
	private String type;// 标记是增加记录还是编辑记录
	private ArrayList<String> urlPath = new ArrayList<String>();// 存放图片的地址的本地图片路径
	private String editcontent;
	private List<String> imgID = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_release_growth_main);
		addPic = (ImageView) findViewById(R.id.addPicture);
		addPic.setOnClickListener(this);
		time = (TextView) findViewById(R.id.time);
		location = (EditText) findViewById(R.id.location);
		content = (EditText) findViewById(R.id.content);
		time.setText(getDate());
		type = getIntent().getStringExtra("type");
		if (type.equals("edit")) {
			edit();
		}
		cid = getIntent().getStringExtra("cid");
		btnUpload = (Button) findViewById(R.id.btnUpload);
		btnUpload.setOnClickListener(this);
		btnback = (ImageView) findViewById(R.id.back);
		btnback.setOnClickListener(this);
		gridView = (GrowthImgGridView) findViewById(R.id.imgGridview);
		adapter = new MyAdapter();
		gridView.setAdapter(adapter);
	}

	/**
	 * 编辑时初始化
	 */
	private void edit() {
		Bundle bundle = this.getIntent().getExtras();
		gid = getIntent().getStringExtra("gid");
		urlPath = bundle.getStringArrayList("path");
		imgID = bundle.getStringArrayList("imgID");
		editcontent = getIntent().getStringExtra("content");
		content.setText(editcontent);
		content.setSelection(editcontent.length());
		for (int i = 0; i < urlPath.size(); i++) {
			PicModle modle = new PicModle();
			modle.setPath(urlPath.get(i));
			modle.setFilePath(Utils.createFilePath(urlPath.get(i)));
			Bitmap bm = BitmapFactory.decodeFile(Utils.createFilePath(urlPath
					.get(i)));
			modle.setBmp(bm);
			modle.setImgID(imgID.get(i));
			listBmp.add(modle);
		}
	}

	/**
	 * 弹出popwindow选择框
	 * 
	 * @param v
	 */
	private void showPopWindow(View v) {
		View view = LayoutInflater.from(this).inflate(R.layout.alert_dialog,
				null);
		btpick = (Button) view.findViewById(R.id.btn_pick_photo);
		btcamear = (Button) view.findViewById(R.id.btn_take_photo);
		btcancle = (Button) view.findViewById(R.id.btn_cancel);
		btcancle.setOnClickListener(this);
		btcamear.setOnClickListener(this);
		btpick.setOnClickListener(this);
		mPopupWindow = new PopupWindow(view, 300, 300);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.showAtLocation(v, Gravity.CENTER,
				LayoutParams.MATCH_PARENT, 0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addPicture:
			showPopWindow(v);
			break;
		case R.id.btn_pick_photo:
			Intent it = new Intent(Intent.ACTION_GET_CONTENT);
			it.setType("image/*");
			startActivityForResult(it, REQUEST_CODE_GETIMAGE_BYSDCARD);
			mPopupWindow.dismiss();
			break;
		case R.id.btn_take_photo:
			Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
			String camerName = InfoHelper.getFileName();
			String fileName = "Share" + camerName + ".tmp";
			File camerFile = new File(InfoHelper.getCamerPath(), fileName);
			theSmall = InfoHelper.getCamerPath() + fileName;
			thisLarge = getLatestImage();
			Uri originalUri = Uri.fromFile(camerFile);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, originalUri);
			startActivityForResult(intent, REQUEST_CODE_GETIMAGE_BYCAMERA);
			mPopupWindow.dismiss();
			break;
		case R.id.btn_cancel:
			mPopupWindow.dismiss();
			break;
		case R.id.btnUpload:
			new UpDataTask().execute(content.getText().toString(), time
					.getText().toString(), location.getText().toString());
			break;
		case R.id.back:
			finish();
			break;
		default:
			break;
		}
	}

	private String getDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		return df.format(new Date());
	}

	/**
	 * 上传数据到服务器
	 * 
	 */
	class UpDataTask extends AsyncTask<String, Integer, String> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected String doInBackground(String... params) {
			String rt = "";
			String time = String.valueOf(new Date().getTime());
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cid", cid);
			map.put("uid", Utils.uid);
			map.put("gid", gid);
			map.put("content", params[0]);
			map.put("location", params[2]);
			map.put("time", time.substring(0, time.length() - 3));
			map.put("token", SharedUtils.getString("token", ""));
			String result = HttpUrlHelper.postData(map, "/growth/igrowth");
			Logger.debug(this, "cid" + cid + "  uid:" + Utils.uid + "    gid:"
					+ gid + " content:" + params[0] + "  location:" + params[2]
					+ "  time:" + params[1]);
			Logger.debug(this, "Growthresult:" + result);
			try {
				JSONObject jsonobject = new JSONObject(result);
				rt = jsonobject.getString("rt");
				if (rt.equals("1")) {
					gid = jsonobject.getString("gid");
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
				Logger.debug(this, "lsitBmap.size:" + listBmp.size());
				if (listBmp.size() == 0 && delID.size() == 0) {// 当没有图片要上传并且也没有要删除的图片则返回
					Utils.showToast("成长记录已发布!");
					progressDialog.dismiss();
					finish();
					return;
				}
				if (type.equals("add")) {
					new UpLoadPicTask().execute();
					return;
				}
				new UpLoadNewAddPicTask().execute();
			} else {
				Utils.showToast("发布失败!");
				progressDialog.dismiss();
				finish();

			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			progressDialog = new ProgressDialog(ReleaseGrowthActivity.this);
			progressDialog.show();
		}
	}

	/**
	 * 上传图片到服务器
	 * 
	 */
	class UpLoadPicTask extends AsyncTask<Object, Integer, Object> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
		String rt = "1";

		@Override
		protected Object doInBackground(Object... params) {
			String result = "";
			for (int i = 0; i < listBmp.size(); i++) {
				File file = new File(listBmp.get(i).getPath());
				if (file != null) {
					result = HttpUrlHelper.postDataFile(HttpUrlHelper.strUrl
							+ "/growth/iuploadImage", file, cid, Utils.uid,
							gid, SharedUtils.getString("token", ""));
					Logger.debug(this, "Picresult:" + result);
				}
				try {
					JSONObject jsonobject = new JSONObject(result);
					rt = jsonobject.getString("rt");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Logger.error(this, e);

					e.printStackTrace();
				}
				// if (!rt.equals("1")) {
				// Utils.showToast("第" + i + "张图片上传失败!");
				// }
			}

			return rt;
		}

		@Override
		protected void onPostExecute(Object result) {
			if (result.equals("1")) {
				Utils.showToast("成长记录已发布!");
			} else {
				Utils.showToast("发布失败!");
			}
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			finish();

		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
		}
	}

	/**
	 * 上传修改时新增图片图片到服务器
	 * 
	 */
	class UpLoadNewAddPicTask extends AsyncTask<Object, Integer, Object> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
		String rt = "1";

		@Override
		protected Object doInBackground(Object... params) {
			String result = "";
			for (int i = 0; i < newAdd.size(); i++) {
				File file = new File(newAdd.get(i));
				if (file != null) {
					result = HttpUrlHelper.postDataFile(HttpUrlHelper.strUrl
							+ "/growth/iuploadImage", file, cid, Utils.uid,
							gid, SharedUtils.getString("token", ""));
					Logger.debug(this, "EditPicresult:" + result);
				}
				try {
					JSONObject jsonobject = new JSONObject(result);
					rt = jsonobject.getString("rt");
				} catch (JSONException e) {
					Logger.error(this, e);
					e.printStackTrace();
				}

			}

			return rt;
		}

		@Override
		protected void onPostExecute(Object result) {
			Logger.debug(this, "UpLoadNewAddPicTask完成");
			new DelTask().execute();
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
		}
	}

	/**
	 * 删除图片 编辑时可能使用
	 * 
	 */
	class DelTask extends AsyncTask<String, Integer, String> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected String doInBackground(String... params) {

			String rt = "1";
			for (int i = 0; i < delID.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("cid", cid);
				map.put("uid", Utils.uid);
				map.put("gid", gid);
				map.put("imgid", delID.get(i));
				map.put("token", SharedUtils.getString("token", ""));
				if (delID.get(i) == null) {

					continue;
				}
				Logger.debug(this, "cid:" + cid + " gid:" + gid + "  imgid:"
						+ delID.get(i));
				String result = HttpUrlHelper.postData(map,
						"/growth/iremoveImage");
				Logger.debug(this, "resultdel:" + result);
				try {
					JSONObject jsonobject = new JSONObject(result);
					rt = jsonobject.getString("rt");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Logger.error(this, e);
					e.printStackTrace();
				}
			}
			return rt;
		}

		@Override
		protected void onPostExecute(String result) {
			Logger.debug(this, "DelTask完成");
			if (result.equals("1")) {
				Utils.showToast("成长记录已发布!");
			} else {
				Utils.showToast("发布失败!");
			}
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			finish();
		}

		@Override
		protected void onPreExecute() {

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bitmap bitmap;
		PicModle modle = new PicModle();

		if (requestCode == REQUEST_CODE_GETIMAGE_BYSDCARD) {
			if (resultCode != RESULT_OK) {
				return;
			}
			if (data == null)
				return;
			Uri thisUri = data.getData();// 获得图片的uri
			// 这里开始的第二部分，获取图片的路径：
			String[] proj = { MediaStore.Images.Media.DATA };
			@SuppressWarnings("deprecation")
			Cursor cursor = managedQuery(thisUri, proj, null, null, null);
			// 按我个人理解 这个是获得用户选择的图片的索引值
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			// 最后根据索引值获取图片路径
			String path = cursor.getString(column_index);
			modle.setPath(path);
			if (type.equals("edit")) {
				newAdd.add(path);
			}
			String thePath = InfoHelper
					.getAbsolutePathFromNoStandardUri(thisUri);
			// 如果是标准Uri
			if (StringUtils.isBlank(thePath)) {
				thisLarge = getAbsoluteImagePath(thisUri);
			} else {
				thisLarge = thePath;
			}
			String attFormat = FileUtils.getFileFormat(thisLarge);
			if (!"photo".equals(MediaUtils.getContentType(attFormat))) {
				Toast.makeText(this, "请选择图片文件！", Toast.LENGTH_SHORT).show();
				return;
			}
			String imgName = FileUtils.getFileName(thisLarge);
			bitmap = loadImgThumbnail(imgName,
					MediaStore.Images.Thumbnails.MICRO_KIND);
			if (bitmap != null) {
				modle.setBmp(bitmap);
			}
		}
		// 拍摄图片
		else if (requestCode == REQUEST_CODE_GETIMAGE_BYCAMERA) {
			if (resultCode != RESULT_OK) {
				return;
			}
			super.onActivityResult(requestCode, resultCode, data);
			bitmap = InfoHelper.getScaleBitmap(this, theSmall);
			if (bitmap != null) {
				String dir = "/clx/camera/";
				Utils.createDir(dir);
				String name = InfoHelper.getFileName() + ".jpg";
				String fileName = Utils.getgetAbsoluteDir(dir) + name;
				BitmapUtils.createImgToFile(bitmap, fileName);
				modle.setPath(fileName);
				modle.setBmp(bitmap);
			}
		}
		listBmp.add(modle);
		adapter.notifyDataSetChanged();
	}

	/**
	 * 通过uri获取文件的绝对路�?
	 * 
	 * @param uri
	 * @return
	 */
	protected String getAbsoluteImagePath(Uri uri) {
		String imagePath = "";
		String[] proj = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = managedQuery(uri, proj, null, null, null);

		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				imagePath = cursor.getString(column_index);
			}
		}

		return imagePath;
	}

	/**
	 * 获取SD卡中�?��图片路径
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	protected String getLatestImage() {
		String latestImage = null;
		String[] items = { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, items, null,
				null, MediaStore.Images.Media._ID + " desc");

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				latestImage = cursor.getString(1);
				break;
			}
		}

		return latestImage;
	}

	/**
	 * 获取图片缩略�? 只有Android2.1以上版本支持
	 * 
	 * @param imgName
	 * @param kind
	 *            MediaStore.Images.Thumbnails.MICRO_KIND
	 * @return
	 */
	protected Bitmap loadImgThumbnail(String imgName, int kind) {
		Bitmap bitmap = null;

		String[] proj = { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DISPLAY_NAME };

		@SuppressWarnings("deprecation")
		Cursor cursor = managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj,
				MediaStore.Images.Media.DISPLAY_NAME + "='" + imgName + "'",
				null, null);

		if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
			ContentResolver crThumb = getContentResolver();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			bitmap = MediaStore.Images.Thumbnails.getThumbnail(crThumb,
					cursor.getInt(0), kind, options);
		}
		return bitmap;
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listBmp.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@SuppressWarnings("deprecation")
		@SuppressLint("NewApi")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(ReleaseGrowthActivity.this)
						.inflate(R.layout.grow_img_gridview_item, null);
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.del = (ImageView) convertView.findViewById(R.id.del);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.del.setVisibility(View.VISIBLE);
			holder.del.setOnClickListener(new BtnDelClick(position));
			int width = Utils.getSecreenWidth(ReleaseGrowthActivity.this) - 30;
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
					width / 4, width / 4);
			holder.img.setLayoutParams(params);
			Bitmap bmp = listBmp.get(position).getBmp();
			if (bmp != null) {
				BitmapDrawable bd = new BitmapDrawable(bmp);
				holder.img.setBackgroundDrawable(bd);
			}
			return convertView;
		}
	}

	class BtnDelClick implements OnClickListener {
		int position;

		public BtnDelClick(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			if (type.equals("edit")) {
				delID.add(listBmp.get(position).getImgID());
			}
			for (int i = 0; i < newAdd.size(); i++) {// 删除新增图片
				if (listBmp.get(position).getPath().equals(newAdd.get(i))) {
					newAdd.remove(newAdd.get(i));
					break;
				}
			}
			listBmp.remove(position);
			adapter.notifyDataSetChanged();
		}
	}

	class PicModle {
		String path;
		Bitmap bmp;
		String filePath;// 修改时用
		String imgID;// 编辑删除时使用

		public String getImgID() {
			return imgID;
		}

		public void setImgID(String imgID) {
			this.imgID = imgID;
		}

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public Bitmap getBmp() {
			return bmp;
		}

		public void setBmp(Bitmap bmp) {
			this.bmp = bmp;
		}

	}

	class ViewHolder {
		ImageView img;
		ImageView del;
	}
}
