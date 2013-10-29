package com.example.clx.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.clx.modle.ContactModle;
import com.example.clx.util.BitmapUtils;
import com.example.clx.util.Utils;
import com.example.clxapp.R;

public class CheckboxAdapter extends BaseAdapter {

	private Context context;
	private List<ContactModle> listData;
	// 记录checkbox的状态
	public HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();

	// 构造函数
	public CheckboxAdapter(Context context, List<ContactModle> listData) {
		this.context = context;
		this.listData = listData;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	// 重写View
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater mInflater = LayoutInflater.from(context);
			convertView = mInflater.inflate(R.layout.contact_list_item, null);
			holder.laybg = (LinearLayout) convertView.findViewById(R.id.laybg);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.check = (CheckBox) convertView.findViewById(R.id.checkBox1);
			holder.num = (TextView) convertView.findViewById(R.id.num);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		setViewWidth(holder.img);
		if (listData.get(position).getBmp() == null) {
			holder.img.setImageResource(R.drawable.root_default);
		} else {
			holder.img.setImageBitmap(BitmapUtils.toRoundBitmap(listData.get(
					position).getBmp()));
		}
		holder.name.setText(listData.get(position).getName());
		holder.num.setText(listData.get(position).getNum());
		holder.check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					state.put(position, isChecked);
				} else {
					state.remove(position);
				}
			}
		});
		holder.check.setChecked((state.get(position) == null ? false : true));
		return convertView;
	}

	private void setViewWidth(ImageView img) {
		int width = Utils.getSecreenWidth(context);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width / 7,
				width / 7);
		lp.setMargins(5, 5, 5, 5);
		img.setLayoutParams(lp);
	}

	class ViewHolder {
		LinearLayout laybg;
		TextView name;
		CheckBox check;
		TextView num;
		ImageView img;
	}
}