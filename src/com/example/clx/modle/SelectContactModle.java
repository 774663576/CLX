package com.example.clx.modle;

import android.graphics.Bitmap;

public class SelectContactModle {
	/**
	 * . 存放当前选则信息
	 */
	int position;
	String num = "";
	String name = "";
	Bitmap bmp;

	public int getPosition() {
		return position;
	}

	public Bitmap getBmp() {
		return bmp;
	}

	public void setBmp(Bitmap bmp) {
		this.bmp = bmp;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
