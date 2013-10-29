package com.example.clx.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinUtils {
	/**
	 * 汉字转换为汉语拼音首字母，英文字符不�?
	 * 
	 * @author gzs
	 * @param chines
	 *            汉字
	 * @return 拼音
	 */
	public static String getPinyinFrt(String chines) {
		String pinyinName = "";
		// 转化为字�?
		char[] nameChar = chines.toCharArray();
		// 汉语拼音格式输出�?
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		// 输出设置,大小�?音标方式�?
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			// 如果是中�?
			if (nameChar[i] > 128) {
				try {
					String[] temp = PinyinHelper.toHanyuPinyinStringArray(
							nameChar[i], defaultFormat);
					if (temp != null && temp.length > 0 && temp[0].length() > 0) {
						pinyinName += temp[0].charAt(0);
					}
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					Logger.error("PinyinUtils.getPinyinFrt", e);

					e.printStackTrace();
				}
			} else {// 为英文字�?
				pinyinName += nameChar[i];
			}
		}
		return pinyinName;
	}

	/**
	 * 汉字转换位汉语拼音，英文字符不变
	 * 
	 * @param chines
	 *            汉字
	 * @return 拼音
	 */
	@SuppressWarnings("deprecation")
	public static String getPinyin(String chines) {
		String pinyinName = "";
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

		try {
			pinyinName = PinyinHelper.toHanyuPinyinString(chines,
					defaultFormat, "");
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			Logger.error("PinyinUtils.getPinyin", e);

		}
		return pinyinName;
	}
}
