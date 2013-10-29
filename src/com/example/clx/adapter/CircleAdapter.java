package com.example.clx.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.clx.modle.CircleModle;
import com.example.clx.util.AsyncImageLoader;
import com.example.clx.util.AsyncImageLoader.ImageCallback;
import com.example.clx.util.Utils;
import com.example.clxapp.R;

/**
 * 圈子显示的自定义adapter
 * 
 * @author teeker_bin
 * 
 */
public class CircleAdapter extends BaseAdapter {
	private List<CircleModle> listmodle;
	private Context mcontext;
	private GridView listView;
	private AsyncImageLoader ImageLoader;

	public CircleAdapter(Context context, List<CircleModle> listModle,
			GridView listView) {
		this.listmodle = listModle;
		this.mcontext = context;
		this.listView = listView;
		ImageLoader = new AsyncImageLoader();
	}

	public CircleAdapter(Context context) {
		this.mcontext = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listmodle.size();
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mcontext).inflate(
					R.layout.circle_item, null);
			holder.circleImg = (ImageView) convertView
					.findViewById(R.id.circleImg);
			holder.circleName = (TextView) convertView
					.findViewById(R.id.circleName);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		int width = Utils.getSecreenWidth(mcontext) - 60;
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				width / 4, width / 4);
		params.setMargins(20, 20, 20, 20);
		holder.circleImg.setLayoutParams(params);
		String imgUrl = listmodle.get(position).getCirIcon();
		ImageView imageView = holder.circleImg;
		imageView.setTag(imgUrl);
		// 异步下载图片
		Bitmap cachedImage = ImageLoader.loaDrawable(imgUrl,
				new ImageCallback() {
					@Override
					public void imageLoaded(Bitmap imageDrawable,
							String imageUrl) {
						ImageView imageViewByTag = (ImageView) listView
								.findViewWithTag(imageUrl);
						if (imageViewByTag != null) {
							imageViewByTag.setImageBitmap(imageDrawable);
						}
					}
				});
		if (cachedImage != null) {
			holder.circleImg.setImageBitmap(cachedImage);

		} else {
			holder.circleImg.setImageResource(R.drawable.root_add);
		}
		holder.circleName.setText(listmodle.get(position).getCirName());
		return convertView;
	}

	class ViewHolder {
		ImageView circleImg;
		TextView circleName;
	}

}
