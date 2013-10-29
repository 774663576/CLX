package com.example.clx.util;

/**
 * 用户详细资料工具类
 * 
 * @author teeker_bin
 * 
 */
public class UserInfoUtils {
	/* 分类标题 */
	public static String infoTitleKey[] = { "基本信息", "联系方式", "社交账号", "所在地/通讯地址",
			"其他资料", "教育经历", "工作经历" };
	/* 基本数据对包含的属性key */
	public static String basicStr[] = { "D_NAME", "D_JOBTITLE", "D_EMPLOYER" };
	/* 基本数据key所对应的中文名称 */
	public static String basicChineseStr[] = { "姓名", "头衔", "工作单位" };
	/* 联系方式数据对包含的属性key */
	public static String contactStr[] = { "D_MOBILE", "D_WORK_PHONE",
			"D_HOME_PHONE", "D_HOME_FAX", "D_WORK_EMAIL", "D_WORK_FAX" };
	/* 联系方式数据key所对应的中文名称 */
	public static String contacChinesetStr[] = { "手机", "工作电话", "家庭电话", "家庭传真",
			"工作E-Mail", "工作传真" };
	/* 账号数据对包含的属性key */
	public static String socialStr[] = { "D_QQ", "D_QQ_WEIBO", "D_SINA_WEIBO",
			"D_WEIXIN", "D_RENREN" };
	/* 账号数据key所对应的中文名称 */
	public static String socialChineseStr[] = { "QQ", "腾讯微博", "新浪微博", "微信",
			"人人网" };
	/* 地址数据对包含的属性key */
	public static String addressStr[] = { "D_BIRTH_PLACE", "D_CURRENT_ADDRESS",
			"D_HOME_ADDRESS", "D_WORK_ADDRESS" };
	/* 地址数据key所对应的中文名称 */
	public static String addressChineseStr[] = { "籍贯", "当前居住地", "家庭住址", "工作地址" };
	/* 其他数据对包含的属性key */
	public static String otherStr[] = { "D_GENDAR", "D_BIRTHDAY" };
	/* 其他数据key所对应的中文名称 */
	public static String otherChineseStr[] = { "性別", "生日" };
	/* 教育经历包含的属性key */
	public static String eduStr[] = { "D_COLLEGE", "D_GRADE_SCHOOL",
			"D_JUNIOR_COLLEGE", "D_JUNIOR_SCHOOL", "D_KINDER_GARTEN",
			"D_MASTER_COLLEGE", "D_PHD_COLLEGE", "D_SENIOR_SCHOOL" };
	/* 教育key所对应的中文名称 */
	public static String eduChinesStr[] = { "大学", "小学", "大专", "初中", "幼儿园",
			"硕士", "博士", "高中" };
	/* 工作经历所包含的属性key */
	public static String workStr[] = { "D_JOB" };
	/* 工作key所对应的中文名称 */
	public static String workChineseStr[] = { "工作" };
	/* 资料属性type */
	public static String infoKey[] = { "D_NAME", "D_JOBTITLE", "D_EMPLOYER",
			"D_MOBILE", "D_WORK_PHONE", "D_HOME_PHONE", "D_HOME_FAX",
			"D_WORK_EMAIL", "D_WORK_FAX,", "D_QQ", "D_QQ_WEIBO",
			"D_SINA_WEIBO", "D_WEIXIN", "D_RENREN", "D_BIRTH_PLACE",
			"D_CURRENT_ADDRESS", "D_HOME_ADDRESS", "D_WORK_ADDRESS",
			"D_GENDAR", "D_BIRTHDAY", "D_COLLEGE", "D_GRADE_SCHOOL",
			"D_JUNIOR_COLLEGE", "D_JUNIOR_SCHOOL", "D_KINDER_GARTEN",
			"D_MASTER_COLLEGE", "D_PHD_COLLEGE", "D_SENIOR_SCHOOL", "D_JOB" };
	public static String infoKeyChinese[] = { "姓名", "头衔", "工作单位", "手机", "工作电话",
			"家庭电话", "家庭传真", "工作E-Mail", "工作传真", "QQ", "腾讯微博", "新浪微博", "微信",
			"人人网", "籍贯", "当前居住地", "家庭住址", "工作地址", "性別", "生日", "大学", "小学", "大专",
			"初中", "幼儿园", "硕士", "博士", "高中", "工作" };

	/**
	 * 将英文key装换为中文key
	 * 
	 * @param key
	 *            要转换的key
	 */
	public static String convertToChines(String key) {
		for (int i = 0; i < infoKey.length; i++) {
			if (infoKey[i].equals(key)) {
				return infoKeyChinese[i];
			}
		}
		return key;

	}

	/**
	 * 将中文key装换为英文key
	 * 
	 * @param key
	 *            要转换的key
	 */
	public static String convertToEnglish(String key) {
		for (int i = 0; i < infoKeyChinese.length; i++) {
			if (infoKeyChinese[i].equals(key)) {
				return infoKey[i];
			}
		}
		return key;

	}
}
