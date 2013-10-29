package com.example.clx.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.clx.modle.GrowthImgModle;
import com.example.clx.modle.GrowthModle;
import com.example.clx.popwindow.GrowthCommentsPopwindow;
import com.example.clx.popwindow.ShowBigImgPopwindow;
import com.example.clx.util.HttpUrlHelper;
import com.example.clx.util.ImageManager;
import com.example.clx.util.Logger;
import com.example.clx.util.SharedUtils;
import com.example.clx.util.Utils;
import com.example.clxapp.R;

public class GrowthAdapter extends BaseAdapter {
	private List<GrowthModle> listData;
	private Context mContext;

	public GrowthAdapter(Context context, List<GrowthModle> modle) {
		this.mContext = context;
		this.listData = modle;
	}

	@Override
	public int getCount() {
		return listData.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.growth_item, null);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.location = (TextView) convertView
					.findViewById(R.id.txtLocate);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.praise = (TextView) convertView.findViewById(R.id.praise);
			holder.comment = (TextView) convertView.findViewById(R.id.comment);
			holder.layComment = (LinearLayout) convertView
					.findViewById(R.id.layComment);
			holder.layParise = (LinearLayout) convertView
					.findViewById(R.id.layParise);
			holder.gridView = (GridView) convertView
					.findViewById(R.id.imgGridview);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.gridView.setAdapter(new GrowthImgAdapter(mContext, listData.get(
				position).getImgModle()));
		holder.gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ShowBigImgPopwindow open = new ShowBigImgPopwindow(mContext,
						arg0, listData.get(position).getImgModle());
				open.show();
			}
		});
		holder.layParise.setOnClickListener(new BtnClick(holder, position));
		holder.layComment.setOnClickListener(new BtnClick(position));
		String path = listData.get(position).getPersonImg();
		ImageManager.from(mContext).displayImage(holder.img, path,
				R.drawable.root_default, 60, 60);
		holder.name.setText(listData.get(position).getName());
		holder.time.setText(listData.get(position).getPublish());
		holder.location.setText(listData.get(position).getLocation());
		holder.content.setText(listData.get(position).getContent());
		holder.praise.setText(listData.get(position).getPraise() + "");
		holder.comment.setText(listData.get(position).getComment() + "");
		return convertView;
	}

	class ViewHolder {
		LinearLayout layParise;
		LinearLayout layComment;
		TextView name;
		TextView time;
		TextView location;
		TextView content;
		TextView praise;
		TextView comment;
		LinearLayout layImg;
		GridView gridView;
		ImageView img;
	}

	class BtnClick implements OnClickListener {
		int position;
		ViewHolder holder;
		String url;

		public BtnClick(ViewHolder holder, int position) {
			this.position = position;
			this.holder = holder;
		}

		public BtnClick(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layParise:
				if (!listData.get(position).isIspraise()) {
					url = "/growth/imyPraise";
					new GetDataTask(holder, position).execute(
							listData.get(position).getCid(),
							listData.get(position).getId(), url);
					return;
				}
				Utils.showToast("您已经赞过！");
				break;
			case R.id.layComment:
				GrowthCommentsPopwindow pop = new GrowthCommentsPopwindow(
						mContext, v, listData.get(position));
				pop.show();
				Logger.debug(this, "name:" + listData.get(position).getName());
				break;
			default:
				break;
			}

		}
	}

	// private void addImgView(ViewHolder holder, List<GrowthImgModle> modle) {
	// LinearLayout lay = new LinearLayout(mContext);
	// for (int i = 0; i < modle.size(); i++) {
	// // if (i % 3 == 0) {
	// // lay = new LinearLayout(mContext);
	// // holder.layImg.addView(lay);
	// // }
	// ImageView img = new ImageView(mContext);
	// LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
	// LinearLayout.LayoutParams.WRAP_CONTENT,
	// LinearLayout.LayoutParams.WRAP_CONTENT);
	// lp.setMargins(6, 6, 6, 6);
	// img.setLayoutParams(lp);
	// img.setTag(i);
	// img.setOnClickListener(new ImgClick(modle));
	// String path;
	// path = modle.get(i).getSamllImg();
	// ImageManager.from(mContext).displayImage(img, path, 0);
	// lay.addView(img);
	// }
	// holder.layImg.addView(lay);
	// }

	class ImgClick implements OnClickListener {
		List<GrowthImgModle> modle;

		public ImgClick(List<GrowthImgModle> modle) {
			this.modle = modle;
		}

		@Override
		public void onClick(View v) {
			// Utils.showToast(v.getTag() + "");
			ShowBigImgPopwindow open = new ShowBigImgPopwindow(mContext, v,
					modle);
			open.show();
		}
	}

	/**
	 * 才从服务器获取数据
	 * 
	 */
	class GetDataTask extends AsyncTask<String, Integer, String> {
		TextView txt;
		int position;
		String count;

		public GetDataTask(ViewHolder holder, int position) {
			txt = (TextView) holder.praise;
		}

		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected String doInBackground(String... params) {
			String rt = "";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cid", params[0]);
			map.put("uid", Utils.uid);
			map.put("gid", params[1]);
			map.put("token", SharedUtils.getString("token", ""));
			map.put("timestamp", 0);
			String result = HttpUrlHelper.postData(map, params[2]);
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
				txt.setText(count);
				listData.get(position).setIspraise(true);
			} else {
				Utils.showToast("点赞失败!");
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
		}
	}
}
