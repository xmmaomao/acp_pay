package com.acp.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author buhao
 */
public class StringUtil {
	/**
	 * utf8字符串，"UTF-8"
	 */
	public static final String UTF8 = "UTF-8";
	/**
	 * 空字符串，""
	 */
	public static final String EMPTY = "";

	/**
	 * 0-9的字符串
	 */
	private static final String NUMERIC = "0123456789";

	/**
	 * email正则表达式
	 */
	private static final Pattern EMAIL_CHECKER = Pattern.compile(
			"^([a-z0-9A-Z]+[-|\\._]?)+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");

	/**
	 * 手机号码正则表达式
	 */
	private static final Pattern MOBILE_CHECKER = Pattern.compile("^1[3,4,5,7,8]\\d{9}$");

	/**
	 * 用指定的字符串替换某字符串的匹配子串，只替换匹配到的第一个
	 *
	 * @param text         待替换的字符串
	 * @param searchString 需替换的字符串
	 * @param replacement  用于替换的字符串
	 * @return 替换后的字符串
	 */
	public static String replace(String text, String searchString, String replacement) {
		return replace(text, searchString, replacement, 1);
	}

	/**
	 * 用指定的字符串替换某字符串的所有匹配子串
	 *
	 * @param text         待替换的字符串
	 * @param searchString 需替换的字符串
	 * @param replacement  用于替换的字符串
	 * @return 替换后的字符串
	 */
	public static String replaceAll(String text, String searchString, String replacement) {
		return replace(text, searchString, replacement, -1);
	}

	/**
	 * 替换字符串，若max为负数，则全文搜索，匹配到的全部替换
	 *
	 * @param text         待替换的字符串
	 * @param searchString 需替换的字符串
	 * @param replacement  用于替换的字符串
	 * @param max          需替换的个数
	 * @return 替换后的字符串
	 */
	public static String replace(String text, String searchString, String replacement, int max) {
		if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0) {
			return text;
		}
		int start = 0;
		int end = text.indexOf(searchString, start);
		if (end == -1) {
			return text;
		}
		int replLength = searchString.length();
		int increase = replacement.length() - replLength;
		increase = (increase < 0 ? 0 : increase);
		increase *= (max < 0 ? 16 : (max > 64 ? 64 : max));
		StringBuffer buf = new StringBuffer(text.length() + increase);
		while (end != -1) {
			buf.append(text.substring(start, end)).append(replacement);
			start = end + replLength;
			if (--max == 0) {
				break;
			}
			end = text.indexOf(searchString, start);
		}
		buf.append(text.substring(start));
		return buf.toString();
	}

	/**
	 * 产生指定长度的随机字符串
	 *
	 * @param salt   源字符集合，如1-9，a-z等
	 * @param length 随机字符串的长度
	 * @return 随机字符串
	 */
	public static String randomString(String salt, int length) {
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		int len = salt.length();
		for (int i = 0; i < length; i++) {
			sb.append(salt.charAt(random.nextInt(len)));
		}
		return sb.toString();
	}

	/**
	 * 判断字符是否存在于字符串中
	 * <p>
	 * 如{@linkplain #isTargetChar(String, char) isTargetChar}{@code ("abc",'a')}
	 * ，a存在于abc中，所以返回true
	 *
	 * @param salt 目标字符串
	 * @param c    待判断字符
	 * @return 存在，则返回true；否则，返回false
	 */
	public static boolean isTargetChar(String salt, char c) {
		for (int i = 0; i < salt.length(); i++) {
			if (salt.charAt(i) == c) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断字符串是否存在于目标字符串中
	 * <p>
	 * 如{@linkplain #isTargetString(String, String) isTargetString}
	 * {@code ("abc","a")} ，a存在于abc中，所以返回true
	 *
	 * @param salt 目标字符串
	 * @param s    待判断字符串
	 * @return 存在，则返回true；否则，返回false
	 */
	public static boolean isTargetString(String salt, String s) {
		if (s == null) {
			return false;
		}
		int len = s.length();
		if (len == 0) {
			return false;
		}
		for (int i = 0; i < len; i++) {
			if (!isTargetChar(salt, s.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断字符是否为数字
	 *
	 * @param c 待判断字符
	 * @return 若是数字，则返回true；否则，返回false
	 */
	public static boolean isNumeric(char c) {
		return isTargetChar(NUMERIC, c);
	}

	/**
	 * 判断字符串是否是数字，0开头的如0123，由于在0-9的字符串中，所以符合条件
	 *
	 * @param s 待判断字符串
	 * @return 若是数字，则返回true；否则，返回false
	 */
	public static boolean isNumeric(String s) {
		return isTargetString(NUMERIC, s);
	}

	/**
	 * 产生随机数字型字符串，参见{@linkplain #randomString(String, int)}，此处的目标String为纯数字
	 *
	 * @param length 字符串的长度
	 * @return 数字型字符串
	 */
	public static String randomNumeric(int length) {
		return randomString(NUMERIC, length);
	}

	/**
	 * 字符串是否为空
	 *
	 * @param cs 待判断字符串
	 * @return 若字符串为null或长度为0，则返回true；否则，返回false
	 */
	public static boolean isEmpty(CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	/**
	 * 字符串是否不为空，此方法和{@code !}{@linkplain #isEmpty(CharSequence)}效果一致
	 *
	 * @param cs 待判断字符串
	 * @return 若字符串不为null且长度大于0，则返回true；否则，返回false
	 */
	public static boolean isNotEmpty(CharSequence cs) {
		return cs != null && cs.length() > 0;
	}

	/**
	 * 判断字符串是否为空
	 * <p>
	 * 此方法和{@linkplain #isEmpty(CharSequence)}的区别在于，多了一个条件，长度大于0
	 * 的情况下，如果字符串都是空字符，则返回true
	 *
	 * @param cs 待判断字符串
	 * @return
	 */
	public static boolean isBlank(CharSequence cs) {
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0)
			return true;
		for (int i = 0; i < strLen; i++)
			if (!Character.isWhitespace(cs.charAt(i)))
				return false;

		return true;
	}

	/**
	 * 判断字符串非空，效果等同{@code !}{@linkplain #isBlank(CharSequence)}
	 *
	 * @param cs 待判断字符串
	 * @return
	 */
	public static boolean isNotBlank(CharSequence cs) {
		return !isBlank(cs);
	}

	/**
	 * 过滤字符串两端的空字符
	 *
	 * @param str 待过滤字符串
	 * @return 若为null，则返回null；否则，调用{@linkplain String#trim()}
	 */
	public static String trim(String str) {
		return str != null ? str.trim() : null;
	}

	/**
	 * 过滤字符串中所有的空字符串
	 *
	 * @param s 待过滤字符串
	 * @return
	 */
	public static String trimAll(String s) {
		if (s == null || s.isEmpty()) {
			return s;
		}
		return Pattern.compile("(\\s|\\t|\\r|\\n)+").matcher(s).replaceAll(EMPTY);
	}

	/**
	 * 过滤字符串两端的空字符串，然后调用{@linkplain #isEmpty(CharSequence) isEmpty(str)}
	 * ，若为true，则返回null，否则返回trim后的结果
	 *
	 * @param str
	 * @return
	 */
	public static String trimToNull(String str) {
		String ts = trim(str);
		return isEmpty(ts) ? null : ts;
	}

	/**
	 * 若字符串为null，则返回空字符串；否则返回{@linkplain String#trim() str.trim()}
	 *
	 * @param str
	 * @return
	 */
	public static String trimToEmpty(String str) {
		return str != null ? str.trim() : EMPTY;
	}

	/**
	 * 若字符串为null或者字符串长度为0，返回null；否则，返回字符串本身
	 *
	 * @param str
	 * @return
	 */
	public static String emptyToNull(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		return str;
	}

	/**
	 * 生成订单号，20位，时间戳加3位随机数字
	 */
	public static String genRandomNo() {
		return System.currentTimeMillis() + "" + randomNumeric(3);
	}


	/**
	 * 若字符串为null，返回空字符串；否则，返回字符串本身
	 *
	 * @param str
	 * @return
	 */
	public static String nullToEmpty(String str) {
		if (str == null) {
			return EMPTY;
		}
		return str;
	}

	/**
	 * @param str
	 * @param separatorChars
	 * @return
	 */
	public static String[] splitAsArray(final String str, final String separatorChars) {
		return splitAsArray(str, separatorChars, -1, false);
	}

	public static String[] splitAsArray(final String str, final String separatorChars, final int max,
	                                    final boolean preserveAllTokens) {
		List<String> list = split(str, separatorChars, max, preserveAllTokens);
		if (list == null) {
			return null;
		}
		if (list.isEmpty()) {
			return new String[0];
		}
		String[] array = new String[list.size()];
		list.toArray(array);
		return array;
	}

	public static List<String> split(final String str, final String separatorChars) {
		return split(str, separatorChars, -1, false);
	}

	/**
	 * @param str
	 * @param separatorChars
	 * @param max
	 * @param preserveAllTokens
	 * @return
	 */
	public static List<String> split(final String str, final String separatorChars, final int max,
	                                 final boolean preserveAllTokens) {
		// Performance tuned for 2.0 (JDK1.4)
		// Direct code is quicker than StringTokenizer.
		// Also, StringTokenizer uses isSpace() not isWhitespace()

		if (str == null) {
			return null;
		}
		final int len = str.length();
		if (len == 0) {
			return new ArrayList<String>(0);
		}
		final List<String> list = new ArrayList<String>();
		int sizePlus1 = 1;
		int i = 0, start = 0;
		boolean match = false;
		boolean lastMatch = false;
		if (separatorChars == null) {
			// Null separator means use whitespace
			while (i < len) {
				if (Character.isWhitespace(str.charAt(i))) {
					if (match || preserveAllTokens) {
						lastMatch = true;
						if (sizePlus1++ == max) {
							i = len;
							lastMatch = false;
						}
						list.add(str.substring(start, i));
						match = false;
					}
					start = ++i;
					continue;
				}
				lastMatch = false;
				match = true;
				i++;
			}
		} else if (separatorChars.length() == 1) {
			// Optimise 1 character case
			final char sep = separatorChars.charAt(0);
			while (i < len) {
				if (str.charAt(i) == sep) {
					if (match || preserveAllTokens) {
						lastMatch = true;
						if (sizePlus1++ == max) {
							i = len;
							lastMatch = false;
						}
						list.add(str.substring(start, i));
						match = false;
					}
					start = ++i;
					continue;
				}
				lastMatch = false;
				match = true;
				i++;
			}
		} else {
			// standard case
			while (i < len) {
				if (separatorChars.indexOf(str.charAt(i)) >= 0) {
					if (match || preserveAllTokens) {
						lastMatch = true;
						if (sizePlus1++ == max) {
							i = len;
							lastMatch = false;
						}
						list.add(str.substring(start, i));
						match = false;
					}
					start = ++i;
					continue;
				}
				lastMatch = false;
				match = true;
				i++;
			}
		}
		if (match || preserveAllTokens && lastMatch) {
			list.add(str.substring(start, i));
		}
		return list;
	}

	/**
	 * 用指定分隔符将对象集合连接成一个字符串，若为对象，则调用的是toString()方法
	 *
	 * @param collection 待拼接的对象集合
	 * @param seperator  分隔符
	 * @return 拼接后的字符串
	 */
	public static String join(Object collection, String seperator) {
		if (collection == null) {
			return null;
		}
		StringBuilder buf = new StringBuilder();
		if (collection.getClass().isArray()) {
			int length = Array.getLength(collection);
			if (length == 0) {
				return null;
			}
			for (int i = 0; i < length; i++) {
				if (i > 0) {
					buf.append(seperator);
				}
				buf.append(Array.get(collection, i).toString());
			}
		} else {
			Iterable<?> iterable = (Iterable<?>) collection;
			int i = 0;
			for (Iterator<?> iter = iterable.iterator(); iter.hasNext(); ) {
				Object item = iter.next();
				if (i > 0) {
					buf.append(seperator);
				}
				buf.append(item.toString());
				i++;
			}
			if (i == 0) {
				return null;
			}
		}

		return buf.toString();
	}

	/**
	 * 用逗号将对象集合连接成一个字符串，若为对象，则调用的是toString()方法
	 *
	 * @param collection 待拼接的对象集合
	 * @return
	 */
	public static String join(Object collection) {
		return join(collection, ",");
	}

	/**
	 * 是否为邮箱
	 *
	 * @param input 待测试的字符串
	 * @return 若是，则返回true；否则，返回false
	 */
	public static boolean isEmail(String input) {
		if (input == null)
			return false;
		if (input.length() > 50)
			return false;
		Matcher matcher = EMAIL_CHECKER.matcher(input);
		return matcher.matches();
	}

	/**
	 * 是否为中国的手机号
	 *
	 * @param input 待测试的字符串
	 * @return 若是，则返回true；否则，返回false
	 */
	public static boolean isChinaMobile(String input) {
		if (input == null)
			return false;
		if (input.length() != 11)
			return false;
		Matcher matcher = MOBILE_CHECKER.matcher(input);
		return matcher.matches();
	}

	/**
	 * 下划线字符串转为驼峰字符串
	 * <p>
	 * 此方法适用于标准的下划线字符串，单下划线
	 * <p>
	 * 遇到下划线相连的情况，如：__my__string，则转换为，_my_string
	 *
	 * @param s 待转换字符串
	 * @return 驼峰字符串
	 */
	public static String underline2camel(String s) {
		if (s.indexOf("_") < 0) {
			return s;
		}
		StringBuilder newStr = new StringBuilder(s.length());
		char c;
		for (int i = 0; i < s.length(); ) {
			c = s.charAt(i);
			if (c == '_') {
				i++;
				if (i < s.length()) {
					newStr.append(Character.toUpperCase(s.charAt(i)));
					i++;
					continue;
				}
			}
			newStr.append(c);
			i++;
		}
		return newStr.toString();
	}

	/**
	 * 将驼峰字符串转为下划线字符串
	 * <p>
	 * 此方法适用于标准的驼峰命名字符串，即首字母小写、其他首字母大写的字符串，如{@code myString}
	 * <p>
	 * 若遇到不规范的命名，则会发生如下转换，如JSONUtil，转为：_j_s_o_n_util
	 *
	 * @param s 待转换字符串
	 * @return 下划线字符串
	 */
	public static String camel2underline(String s) {
		StringBuilder newStr = new StringBuilder(s.length());
		char c;
		for (int i = 0; i < s.length(); i++) {
			c = s.charAt(i);
			if (Character.isUpperCase(c)) {
				newStr.append('_');
				newStr.append(Character.toLowerCase(s.charAt(i)));
			} else {
				newStr.append(c);
			}
		}
		return newStr.toString();
	}

	// public static boolean isChinese(char c) { // 0x4e00 0x9fbb
	//
	// Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
	//
	// if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
	//
	// || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
	//
	// || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
	//
	// // || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
	// //
	// // || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
	// //
	// // || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
	//
	// ) {
	//
	// return true;
	//
	// }
	//
	// return false;
	// }
	//
	// public static boolean isChinese(String s) {
	// if (StringUtil.isEmpty(s)) {
	// return false;
	// }
	// for (int i = 0; i < s.length(); i++) {
	// if (!isChinese(s.charAt(i))) {
	// return false;
	// }
	// }
	// return true;
	// }

}
