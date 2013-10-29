package com.example.clx.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 媒体类型工具
 * 
 */
public class MediaUtils {
	private static Map<String, String> FORMAT_TO_CONTENTTYPE = new HashMap<String, String>();

	static {

		// 图片
		FORMAT_TO_CONTENTTYPE.put("jpg", "photo");
		FORMAT_TO_CONTENTTYPE.put("jpeg", "photo");
		FORMAT_TO_CONTENTTYPE.put("png", "photo");
		FORMAT_TO_CONTENTTYPE.put("bmp", "photo");
		FORMAT_TO_CONTENTTYPE.put("gif", "photo");
	}

	/**
	 * 根据扩展名获取类�?
	 * 
	 * @param attFormat
	 * @return
	 */
	public static String getContentType(String attFormat) {
		String contentType = FORMAT_TO_CONTENTTYPE.get("null");

		if (attFormat != null) {
			contentType = (String) FORMAT_TO_CONTENTTYPE.get(attFormat
					.toLowerCase());
		}
		return contentType;
	}
}