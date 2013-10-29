package com.example.clx.modle;

import java.util.List;

/**
 * 成长记录详细信息
 * 
 * @author teeker_bin
 * 
 */
public class GrowthModle {
	private String personImg = "";// 发布成长记录人的头像地址
	private String name = "";// 发布成长记录人的姓名
	private String cid = "";// 圈子id
	private String num = "";// 圈子数量
	private String oldts = "";// 请求参数中的时间戳
	private String newts = "";// 本次访问服务器端的时间戳
	private String id = "";// 成长id
	private String uid = "";// 发布者id
	private String content = "";// 发布内容
	private String location = "";// 发布地点
	private String happen = "";// 发布时间
	private int praise;// 赞的数量
	private int comment;// 评论的数量
	private String imgid = "";// 图片id
	private String imgurl = "";// 图片地址
	private String publish = "";// 发布时间
	private boolean ispraise;// 用来标记本地是否点赞
	private List<GrowthImgModle> imgModle;

	public String getPersonImg() {
		return personImg;
	}

	public void setPersonImg(String personImg) {
		this.personImg = personImg;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isIspraise() {
		return ispraise;
	}

	public List<GrowthImgModle> getImgModle() {
		return imgModle;
	}

	public void setImgModle(List<GrowthImgModle> imgModle) {
		this.imgModle = imgModle;
	}

	public void setIspraise(boolean ispraise) {
		this.ispraise = ispraise;
	}

	public String getPublish() {
		return publish;
	}

	public void setPublish(String publish) {
		this.publish = publish;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getOldts() {
		return oldts;
	}

	public void setOldts(String oldts) {
		this.oldts = oldts;
	}

	public String getNewts() {
		return newts;
	}

	public void setNewts(String newts) {
		this.newts = newts;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getHappen() {
		return happen;
	}

	public void setHappen(String happen) {
		this.happen = happen;
	}

	public int getPraise() {
		return praise;
	}

	public void setPraise(int praise) {
		this.praise = praise;
	}

	public int getComment() {
		return comment;
	}

	public void setComment(int comment) {
		this.comment = comment;
	}

	public String getImgid() {
		return imgid;
	}

	public void setImgid(String imgid) {
		this.imgid = imgid;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

}
