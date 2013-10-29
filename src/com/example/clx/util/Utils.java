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
 * ���ù�����
 * 
 * @author teeker_bin
 * 
 */
public class Utils {
	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };
	/** �绰���� **/
	private static final int PHONES_NUMBER_INDEX = 1;

	/** ��ϵ����ʾ���� **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;
	/** ��ϵ��ͷ��id **/
	private static final int PHONE_PHOTO_ID = 2;
	/** ��ϵ��id **/
	private static final int PHONE_CONTACT_ID = 3;
	private static List<ContactModle> contactList = new ArrayList<ContactModle>();
	private static TelephonyManager mTelephonyManager;
	public static String uid = "5";

	/**
	 * �ֻ�������֤
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
	 * ������֤
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
	 * ��ʾ��ʾ��Ϣ
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
	 * ���SIM �����״̬
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
	 * ��ȡ�ֻ���SIM���ϵ�ͨѶ¼
	 * 
	 * @param context
	 * @return
	 */
	public static List<ContactModle> getContactList(Context context) {
		queryContactList(context);
		return contactList;
	}

	/**
	 * ��ѯ�ֻ���SIM���е���ϵ�ˣ�����ϵͳ���Ƿ񻺴���ͨѶ¼������ջ��棬�����¼�����ϵ����Ϣ��������
	 * 
	 * @param context
	 * @return
	 */
	public static void queryContactList(Context context) {
		contactList.clear();
		ContentResolver resolver = context.getContentResolver();
		// ��ȡ�ֻ��ϵ���ϵ����Ϣ
		getContactList(resolver, Phone.CONTENT_URI, context);

		if (getSimState(context) == TelephonyManager.SIM_STATE_READY) { // �ж�SIM�Ƿ����
			// ��ȡSIM���ϵ���ϵ����Ϣ
			getContactList(resolver, Uri.parse("content://icc/adn"), context);
		}

	}

	/**
	 * ��ȡ��ϵ���б�
	 * 
	 * @param resolver
	 * @param uri
	 * @return
	 */
	private static List<ContactModle> getContactList(ContentResolver resolver,
			Uri uri, Context mContext) {
		// ��ȡ��ѯ����α�
		Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
				null);
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				// �õ��ֻ�����
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// ���ֻ�����Ϊ�յĻ���Ϊ���ֶ� ������ǰѭ��
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				// �õ���ϵ������
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);
				// �õ���ϵ��ID
				Long contactid = phoneCursor.getLong(PHONE_CONTACT_ID);
				// �õ���ϵ��ͷ��ID
				Long photoid = phoneCursor.getLong(PHONE_PHOTO_ID);
				// �õ���ϵ��ͷ��Bitamp
				Bitmap contactPhoto = null;
				// photoid ����0 ��ʾ��ϵ����ͷ�� ���û�и���������ͷ�������һ��Ĭ�ϵ�
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
	 * ��ȡ��ϵ����Ϣ
	 */
	public static List<ContactModle> getPhoneContacts(Context mContext) {
		List<ContactModle> listmodle = new ArrayList<ContactModle>();
		ContentResolver resolver = mContext.getContentResolver();
		// ��ȡ�ֻ���ϵ��
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				// �õ��ֻ�����
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// ���ֻ�����Ϊ�յĻ���Ϊ���ֶ� ������ǰѭ��
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				// �õ���ϵ������
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);
				// �õ���ϵ��ID
				Long contactid = phoneCursor.getLong(PHONE_CONTACT_ID);
				// �õ���ϵ��ͷ��ID
				Long photoid = phoneCursor.getLong(PHONE_PHOTO_ID);
				// �õ���ϵ��ͷ��Bitamp
				Bitmap contactPhoto = null;
				// photoid ����0 ��ʾ��ϵ����ͷ�� ���û�и���������ͷ�������һ��Ĭ�ϵ�
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
	 * ����url���ɻ����ļ�����·����
	 * 
	 * @param url
	 * @return
	 */
	public static String urlToFilePath(String url) {

		// ��չ��λ��
		int index = url.lastIndexOf('.');
		if (index == -1) {
			return null;
		}
		StringBuilder filePath = new StringBuilder();

		// ͼƬ��ȡ·��
		// filePath.append(myapp.getCacheDir().toString()).append('/');
		filePath.append(
				Environment.getExternalStorageDirectory().getAbsolutePath()
						.toString()).append('/').append("ing");

		// ͼƬ�ļ���
		filePath.append(MD5.Md5(url)).append(url.substring(index));

		return filePath.toString();
	}

	/**
	 * ��Qscrollview��Ƕ��listview��ʾ��ȫ����
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		// ��ȡListView��Ӧ��Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()�������������Ŀ
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // ��������View �Ŀ��
			totalHeight += listItem.getMeasuredHeight(); // ͳ������������ܸ߶�
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()��ȡ�����ָ���ռ�õĸ߶�
		// params.height���õ�����ListView������ʾ��Ҫ�ĸ߶�
		listView.setLayoutParams(params);
	}

	/**
	 * ����uil��ȡ����·��
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
	 * ��ȡ��Ļ���
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
	 * ��ȡ��Ļ�߶�
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
	 * �ж�sd���Ƿ����
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
	 * �����ļ���
	 * 
	 * @param dir
	 */
	public static void createDir(String dir) {
		String sdpath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		File destDir = new File(sdpath + dir);
		if (!destDir.exists()) {// �����ļ���
			destDir.mkdirs();
		}
	}

	/**
	 * ����ָ����ʽ�ĵ�ǰʱ���ַ�����
	 * 
	 * @param str
	 * @return
	 */
	public static String getCurrDateStr(String str) {
		SimpleDateFormat format = new SimpleDateFormat(str);
		return format.format(new Date());
	}

	/**
	 * �õ�����·��
	 * 
	 * @param dir
	 * @return
	 */
	public static String getgetAbsoluteDir(String dir) {
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ dir;

	}
}
