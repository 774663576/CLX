package com.example.clx.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.clx.util.Logger;
import com.example.clxapp.R;

/**
 * 菜单界面
 * 
 */
public class SetMenu implements OnClickListener {
	/**
	 * 当前界面的View
	 */
	private View mDesktop;

	private List<String> menulist = new ArrayList<String>();
	private Context mcontext;
	private ListView listview;
	private MyAdapter adapter;
	private Button btn;
	private Activity mactivity;

	public SetMenu(Context context, Activity activity) {
		// 绑定布局到当前View
		this.mcontext = context;
		this.mactivity = activity;
		mDesktop = LayoutInflater.from(context).inflate(R.layout.desktop, null);
		menulist.add("我的圈子");
		menulist.add("我的名片");
		menulist.add("私信");
		menulist.add("设置");
		listview = (ListView) mDesktop.findViewById(R.id.menulist);
		adapter = new MyAdapter();
		listview.setAdapter(adapter);
		btn = (Button) mDesktop.findViewById(R.id.menuback);
		btn.setOnClickListener(this);
	}

	public View getView() {
		return mDesktop;
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return menulist.size();
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
			convertView = LayoutInflater.from(mcontext).inflate(
					R.layout.menu_item, null);
			TextView txt = (TextView) convertView.findViewById(R.id.menutxt);
			txt.setText(menulist.get(position).toString());
			return convertView;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.menuback:
			mactivity.finish();
			Logger.debug(this, "退出");
			break;
		default:
			break;
		}

	}
}
