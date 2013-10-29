package com.example.clx.db;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.clx.modle.CircleModle;
import com.example.clx.modle.Info;
import com.example.clx.modle.MemberInfoModle;
import com.example.clx.modle.MemberModle;
import com.example.clx.util.Logger;

/**
 * 数据库操作类
 * 
 * @author teeker_bin
 * 
 */
public class DBUtils {
	public static DataBase dbase = DataBase.getInstance();
	public static SQLiteDatabase db = dbase.getWritableDatabase();

	/**
	 * 从数据库获取存储的本地圈
	 * 
	 * @return
	 */
	public static List<CircleModle> getCircleList() {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		List<CircleModle> data = new ArrayList<CircleModle>();
		Cursor cursor = db.query("circlelist", null, null, null, null, null,
				null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				String id = cursor.getString(cursor.getColumnIndex("cirID"));
				String name = cursor
						.getString(cursor.getColumnIndex("cirName"));
				String imgAdd = cursor.getString(cursor
						.getColumnIndex("cirImg"));
				CircleModle modle = new CircleModle();
				modle.setCirID(id);
				modle.setCirName(name);
				modle.setCirIcon(imgAdd);
				data.add(modle);
				Logger.debug(DBUtils.class, "circleName:" + name + "  id:" + id);
				cursor.moveToNext();
			}
		}
		cursor.close();
		db.close();
		return data;
	}

	/**
	 * 从数据库获取存储用户列表
	 * 
	 * @return
	 */
	public static List<MemberModle> getUserList(String cirname) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		List<MemberModle> data = new ArrayList<MemberModle>();
		Cursor cursor = db.query(cirname, null, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				String id = cursor.getString(cursor.getColumnIndex("userID"));
				String name = cursor.getString(cursor
						.getColumnIndex("userName"));
				String imgAdd = cursor.getString(cursor
						.getColumnIndex("userImg"));
				String employer = cursor.getString(cursor
						.getColumnIndex("employer"));
				String sortkey = cursor.getString(cursor
						.getColumnIndex("sortkey"));
				MemberModle modle = new MemberModle();
				modle.setId(id);
				modle.setEmployer(employer);
				modle.setName(name);
				modle.setSort_key(sortkey);
				modle.setImg(imgAdd);
				data.add(modle);
				cursor.moveToNext();
			}
		}
		cursor.close();
		db.close();
		return data;
	}

	/**
	 * 查找圈子成员信息
	 * 
	 * @param userlistNmae圈子成员表名
	 * @param pid成员ID
	 * @return
	 */
	public static List<Info> getUserInfo(String userlistNmae, String pid) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		List<Info> listInfo = new ArrayList<Info>();
		Cursor cursor = db.query(userlistNmae, null, "personID='" + pid + "'",
				null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				String key = cursor.getString(cursor.getColumnIndex("key"));
				String value = cursor.getString(cursor.getColumnIndex("value"));
				Info info = new Info();
				info.setKey(key);
				info.setValue(value);
				listInfo.add(info);
				cursor.moveToNext();
			}
		}
		cursor.close();
		db.close();
		return listInfo;
	}

	/**
	 * 在用户资料表中查找指定用户是否存在
	 * 
	 * @param userlistNmae
	 * @param pid
	 * @return
	 */
	public static boolean isExistOfPersonId(String userlistNmae, String pid) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		Cursor cursor = db.query(userlistNmae, null, "personID='" + pid + "'",
				null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.close();
			db.close();
			return true;
		}
		cursor.close();
		db.close();
		return false;

	}

	/**
	 * 根据id查找姓名和头像地址
	 * 
	 * @param tabName
	 * @param id
	 * @return
	 */
	public static MemberInfoModle selectNameAndImgByID(String tabName, String id) {
		if (!db.isOpen()) {
			db = dbase.getWritableDatabase();
		}
		MemberInfoModle modle = new MemberInfoModle();
		Cursor cursor = db.query(tabName, null, "userID='" + id + "'", null,
				null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String name = cursor.getString(cursor.getColumnIndex("userName"));
			String img = cursor.getString(cursor.getColumnIndex("userImg"));
			modle.setName(name);
			modle.setAvator(img);

		} else {
			cursor.close();
			db.close();
			return null;
		}
		cursor.close();
		db.close();
		return modle;
	}
}
