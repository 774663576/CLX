package com.example.clx.modle;

import java.io.Serializable;

public class MemberInfoModle implements Serializable {
	/**
	 * 圈子成员基本信息
	 */
	private static final long serialVersionUID = -4036530540458973531L;
	private String name = "";// 姓名
	private String cellPhone = "";// 电话
	private String email = "";// 邮箱
	private String avator = "";// 头像地址
	private String gendar = "";// 性别
	private String birthday = "";// 生日
	private String employer = "";// 工作单位
	private String jobTitle = "";// 工作职位

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCellPhone() {
		return cellPhone;
	}

	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAvator() {
		return avator;
	}

	public void setAvator(String avator) {
		this.avator = avator;
	}

	public String getGendar() {
		return gendar;
	}

	public void setGendar(String gendar) {
		this.gendar = gendar;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getEmployer() {
		return employer;
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
}
