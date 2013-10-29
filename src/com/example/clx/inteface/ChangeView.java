package com.example.clx.inteface;

import java.util.List;

import com.example.clx.modle.Info;

public interface ChangeView {
	/* 从资料显示view切换到编辑view所需要的数据 */

	public void setViewData(List<Info> data, int type, String cid, String pid,
			String tableName);

	/* 删除资料编辑view */
	public void delView();

	/**
	 * 在编辑界面修改以后 将修改的数据传给显示界面进行更新
	 * 
	 * @param data
	 *            修改以后的数据
	 * @param infoType
	 *            修改的资料类型 1:基本信息、2：联系信息、3账号信息、4地址信息、5其他信息
	 */
	public void NotifyData(List<Info> data, int infoType);
}
