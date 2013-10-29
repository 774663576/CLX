package com.example.clx.util;

import android.text.TextUtils;

public class StringUtils {
	/**
	 * 判断给定字符串是否空白串�?br> 空白串是指由空格、制表符、回车符、换行符组成的字符串<br>
	 * 若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isBlank(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 字符串拼接
	 * 
	 * @param str
	 * @return
	 */
	public static String JoinString(String str) {
		return str.substring(0, str.length() - 4) + "_100x100"
				+ str.substring(str.length() - 4);

	}

	/**
	 * 返回str中最后一个separator子串后面的字符串 当str == null || str == "" || separator == ""
	 * 时返回str； 当separator==null || 在str中不存在子串separator 时返回 ""
	 * 
	 * @param str
	 *            源串
	 * @param separator
	 *            子串
	 * @return
	 */
	public static String substringAfterLast(String str, String separator) {
		if (TextUtils.isEmpty(str) || "".equals(separator)) {
			return str;
		}

		if (separator == null) {
			return "";
		}
		int idx = str.lastIndexOf(separator);
		if (idx < 0) {
			return str;
		}

		return str.substring(idx + separator.length());
	}
}