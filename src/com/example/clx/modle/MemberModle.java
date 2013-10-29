package com.example.clx.modle;

/**
 * . 圈子成员列表显示信息
 * 
 * @author teeker_bin
 * 
 */
public class MemberModle {
	private String name = "";// 姓名
	private String img = "";// 头像地址
	private String employer = "";// 工作单位
	private String job = "";// 工作
	private String sort_key;// 用来排序的关键字
	private String id = "";// id

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSort_key() {
		return sort_key;
	}

	public void setSort_key(String sort_key) {
		this.sort_key = sort_key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getEmployer() {
		return employer;
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

}
