package com.acp.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 集合工具类
 *
 * @author buhao
 */
public class CollectionUtil {

	/**
	 * 数组转换为map<br>
	 * <li>参数必须是偶数个，然后根据键值对的方式添加到map对象</li> <li>通常用于http请求参数的封装</li>
	 *
	 * @param t 数组，单数为key，双数为value，且key必须为string类型
	 * @return map对象
	 */
	public static Map<String, Object> arrayAsMap(Object... t) {
		if (t == null || t.length <= 0) {
			return null;
		}
		if (t.length % 2 != 0) {
			throw new RuntimeException("illegal args count");
		}
		Map<String, Object> params = new HashMap<String, Object>(t.length);
		for (int i = 0; i < t.length; i += 2) {
			if (t[i] == null || !t[i].getClass().equals(String.class)) {
				throw new RuntimeException("illegal arg: " + t[i] + "at " + i);
			}
			String key = t[i].toString();
			Object value = t[i + 1];
			params.put(key, value);
		}
		return params;
	}

	public static Map<String, String> stringArrayAsMap(String... t) {
		if (t == null || t.length <= 0) {
			return null;
		}
		if (t.length % 2 != 0) {
			throw new RuntimeException("illegal args count");
		}
		Map<String, String> params = new HashMap<String, String>(t.length);
		for (int i = 0; i < t.length; i += 2) {
			if (t[i] == null) {
				throw new RuntimeException("illegal arg: " + t[i] + "at " + i);
			}
			String key = t[i];
			String value = t[i + 1];
			params.put(key, value);
		}
		return params;
	}

	/**
	 * 判断集合是否为空
	 *
	 * @param collection 待判断集合
	 * @return 若集合为null，或者集合元素个数为0，则返回true；否则，返回false
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	/**
	 * 判断集合是否为非空
	 *
	 * @param collection 待判断集合
	 * @return 若集合不为null且集合元素个数不为0，则返回true；否则，返回false
	 */
	public static boolean isNotEmpty(Collection<?> collection) {
		return collection != null && !collection.isEmpty();
	}

	/**
	 * 处理空集合
	 *
	 * @param collection 待处理集合
	 * @return 若集合满足{@link CollectionUtil#isEmpty(Collection)}，则返回null；否则，返回原集合
	 */
	public static <T extends Collection<?>> T trimToNull(T collection) {
		return isEmpty(collection) ? null : collection;
	}

	/**
	 * 处理空数组
	 *
	 * @param array 待处理数组
	 * @return 若数组为null，或数组长度为0，则返回null；否则，返回原数组
	 */
	public static <T extends Object> T trimArrayToNull(T array) {
		if (array == null) {
			return null;
		}
		if (Array.getLength(array) == 0) {
			return null;
		}
		return array;
	}

	/**
	 * 将对象加入目标list
	 *
	 * @param dest 目标list
	 * @param src  待加入对象
	 * @return 若对象不为null，且目标list不包含该对象，则添加到目标list
	 */
	public static <T> List<T> addToList(List<T> dest, T src) {
		if (src != null && !dest.contains(src)) {
			dest.add(src);
		}
		return dest;
	}

	/**
	 * 将对象集合加入到目标list
	 *
	 * @param dest 目标list
	 * @param src  待加入的对象集合
	 * @return 若对象不为空，则依次判断目标list是否包含带加入集合中的对象，不包含，则添加
	 */
	public static <T> List<T> addAllToList(List<T> dest, Collection<T> src) {
		if (isEmpty(src)) {
			return dest;
		}
		for (T item : src) {
			if (!dest.contains(item)) {
				dest.add(item);
			}
		}
		return dest;
	}

}
