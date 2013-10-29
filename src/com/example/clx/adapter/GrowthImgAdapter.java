package com.example.clx.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.clx.modle.GrowthImgModle;
import com.example.clx.util.ImageManager;
import com.example.clx.util.Utils;
import com.example.clxapp.R;

public class GrowthImgAdapter extends BaseAdapter {
	private Context mContext;
	private List<GrowthImgModle> listData;

	public GrowthImgAdapter(Context context, List<GrowthImgModle> data) {
		this.mContext = context;
		this.listData = data;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.grow_img_gridview_item, null);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.fream = (FrameLayout) convertView.findViewById(R.id.fream);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		int width = Utils.getSecreenWidth(mContext) - 30;
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				width / 4, width / 4);
		holder.img.setLayoutParams(params);
		String path;
		if (listData != null && position < listData.size()) {
			path = listData.get(position).getSamllImg();
		} else {
			path = "camera_default";
		}
		if (path.contains("default")) {
			holder.img.setImageResource(R.drawable.root_default);
		} else {
			ImageManager.from(mContext).displayImage(holder.img, path, 0);
		}
		return convertView;
	}

	class ViewHolder {
		ImageView img;
		FrameLayout fream;
	}
}
