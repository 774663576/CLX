package com.example.clx.inteface;

import java.util.List;

import com.example.clx.modle.Info;

public interface ChangeView {
	/* ��������ʾview�л����༭view����Ҫ������ */

	public void setViewData(List<Info> data, int type, String cid, String pid,
			String tableName);

	/* ɾ�����ϱ༭view */
	public void delView();

	/**
	 * �ڱ༭�����޸��Ժ� ���޸ĵ����ݴ�����ʾ������и���
	 * 
	 * @param data
	 *            �޸��Ժ������
	 * @param infoType
	 *            �޸ĵ��������� 1:������Ϣ��2����ϵ��Ϣ��3�˺���Ϣ��4��ַ��Ϣ��5������Ϣ
	 */
	public void NotifyData(List<Info> data, int infoType);
}
