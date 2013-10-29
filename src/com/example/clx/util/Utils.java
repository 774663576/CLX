package com.example.clx.util;

import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Photo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.clx.modle.ContactModle;
import com.example.clxapp.CLXApplication;
import com.example.clxapp.R;

/**
 * 公用工具类
 * 
 * @author teeker_bin
 * 
 */
public class Utils {
	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };
	/** 电话号码 **/
	private static final int PHONES_NUMBER_INDEX = 1;

	/** 联系人显示名称 **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;
	/** 联系人头像id **/
	private static final int PHONE_PHOTO_ID = 2;
	/** 联系人id **/
	private static final int PHONE_CONTACT_ID = 3;
	private static List<ContactModle> contactList = new ArrayList<ContactModle>();
	private static TelephonyManager mTelephonyManager;
	public static String uid = "5";

	/**
	 * 手机号码验证
	 * 
	 * @param
	 * @return
	 */
	public static boolean isPhoneNum(String strPhoneNum) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(strPhoneNum);
		return m.matches();
	}

	/**
	 * 邮箱验证
	 * 
	 * @param strEmail
	 * @return
	 */
	public static boolean isEmail(String strEmail) {
		String strPattern = "^[a-zA-Z0-9]*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);
		return m.matches();
	}

	/**
	 * 显示提示信息
	 * 
	 * @param str
	 */
	public static void showToast(String str) {
		Toast toast = Toast.makeText(CLXApplication.getInstance(), str,
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

	}

	/**
	 * 检测SIM 的相关状态
	 * 
	 * @param context
	 * @return
	 */
	private static int getSimState(Context context) {
		mTelephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return mTelephonyManager.getSimState();
	}

	/**
	 * 获取手机和SIM卡上的通讯录
	 * 
	 * @param context
	 * @return
	 */
	public static List<ContactModle> getContactList(Context context) {
		queryContactList(context);
		return contactList;
	}

	/**
	 * 查询手机和SIM卡中的联系人，不管系统里是否缓存了通讯录，先清空缓存，再重新加载联系人信息至缓存中
	 * 
	 * @param context
	 * @return
	 */
	public static void queryContactList(Context context) {
		contactList.clear();
		ContentResolver resolver = context.getContentResolver();
		// 获取手机上的联系人信息
		getContactList(resolver, Phone.CONTENT_URI, context);

		if (getSimState(context) == TelephonyManager.SIM_STATE_READY) { // 判断SIM是否存在
			// 获取SIM卡上的联系人信息
			getContactList(resolver, Uri.parse("content://icc/adn"), context);
		}

	}

	/**
	 * 获取联系人列表
	 * 
	 * @param resolver
	 * @param uri
	 * @return
	 */
	private static List<ContactModle> getContactList(ContentResolver resolver,
			Uri uri, Context mContext) {
		// 获取查询结果游标
		Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
				null);
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);
				// 得到联系人ID
				Long contactid = phoneCursor.getLong(PHONE_CONTACT_ID);
				// 得到联系人头像ID
				Long photoid = phoneCursor.getLong(PHONE_PHOTO_ID);
				// 得到联系人头像Bitamp
				Bitmap contactPhoto = null;
				// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				if (photoid > 0) {
					Uri ur = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI, contactid);
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(resolver, ur);
					contactPhoto = BitmapFactory.decodeStream(input);
				} else {
					contactPhoto = BitmapFactory.decodeResource(
							mContext.getResources(), R.drawable.root_default);
				}
				ContactModle modle = new ContactModle();
				modle.setName(contactName);
				modle.setNum(phoneNumber);
				modle.setBmp(contactPhoto);
				contactList.add(modle);
			}
			phoneCursor.close();
		}
		return contactList;
	}

	/**
	 * 获取联系人信息
	 */
	public static List<ContactModle> getPhoneContacts(Context mContext) {
		List<ContactModle> listmodle = new ArrayList<ContactModle>();
		ContentResolver resolver = mContext.getContentResolver();
		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);
				// 得到联系人ID
				Long contactid = phoneCursor.getLong(PHONE_CONTACT_ID);
				// 得到联系人头像ID
				Long photoid = phoneCursor.getLong(PHONE_PHOTO_ID);
				// 得到联系人头像Bitamp
				Bitmap contactPhoto = null;
				// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				if (photoid > 0) {
					Uri uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI, contactid);
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(resolver, uri);
					contactPhoto = BitmapFactory.decodeStream(input);
				} else {
					contactPhoto = BitmapFactory.decodeResource(
							mContext.getResources(), R.drawable.root_default);
				}
				ContactModle modle = new ContactModle();
				modle.setName(contactName);
				modle.setNum(phoneNumber);
				modle.setBmp(contactPhoto);
				listmodle.add(modle);
			}
			phoneCursor.close();
		}
		return listmodle;
	}

	/**
	 * 根据url生成缓存文件完整路径名
	 * 
	 * @param url
	 * @return
	 */
	public static String urlToFilePath(String url) {

		// 扩展名位置
		int index = url.lastIndexOf('.');
		if (index == -1) {
			return null;
		}
		StringBuilder filePath = new StringBuilder();

		// 图片存取路径
		// filePath.append(myapp.getCacheDir().toString()).append('/');
		filePath.append(
				Environment.getExternalStorageDirectory().getAbsolutePath()
						.toString()).append('/').append("ing");

		// 图片文件名
		filePath.append(MD5.Md5(url)).append(url.substring(index));

		return filePath.toString();
	}

	/**
	 * 解決scrollview中嵌套listview显示不全问题
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	/**
	 * 根据uil获取本地路径
	 * 
	 * @param key
	 * @return
	 */
	public static String createFilePath(String key) {
		try {
			return "/mnt/sdcard/thumbnails" + File.separator + "cache_"
					+ URLEncoder.encode(key.replace("*", ""), "UTF-8");
		} catch (Exception e) {
			Logger.error("Utils.createFilePath", e);
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 获取屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getSecreenWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		return screenWidth;
	}

	/**
	 * 获取屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getSecreenHeight(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		int screenHeight = dm.heightPixels;
		return screenHeight;
	}

	/**
	 * 判断sd卡是否存在
	 * 
	 * @return
	 */
	public static boolean ExistSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else
			return false;
	}

	/**
	 * 创建文件夹
	 * 
	 * @param dir
	 */
	public static void createDir(String dir) {
		String sdpath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		File destDir = new File(sdpath + dir);
		if (!destDir.exists()) {// 创建文件夹
			destDir.mkdirs();
		}
	}

	/**
	 * 返回指定格式的当前时间字符串，
	 * 
	 * @param str
	 * @return
	 */
	public static String getCurrDateStr(String str) {
		SimpleDateFormat format = new SimpleDateFormat(str);
		return format.format(new Date());
	}

	/**
	 * 得到绝对路径
	 * 
	 * @param dir
	 * @return
	 */
	public static String getgetAbsoluteDir(String dir) {
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ dir;

	}
}
