package com.example.clx.modle;

import android.graphics.Bitmap;

/**
 * 联系人对象信息
 * 
 * @author teeker_bin
 * 
 */
public class ContactModle {
	private String name = "";// 联系人姓名
	private String num = "";// 联系人号码
	private Bitmap bmp;// 联系人头像
	private boolean selected = false;

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public Bitmap getBmp() {
		return bmp;
	}

	public void setBmp(Bitmap bmp) {
		this.bmp = bmp;
	}

}
