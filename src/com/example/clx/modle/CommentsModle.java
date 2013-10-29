package com.example.clx.modle;

/**
 * 评论接口返回值对象
 * 
 * @author teeker_bin
 * 
 */
public class CommentsModle {
	String uid = "";// 评论发布者ID
	String cid = "";// 请求的圈子ID
	String gid = "";// 请求的成长记录ID
	String id = "";// 评论ID
	String replyid = "";// 评论回复的用户ID
	String num = "";// 评论数量
	String content = "";// 评论内容
	String time = "";// 评论时间

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReplyid() {
		return replyid;
	}

	public void setReplyid(String replyid) {
		this.replyid = replyid;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
