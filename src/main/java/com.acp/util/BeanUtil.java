package com.acp.util;

import com.alibaba.fastjson.JSON;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * bean工具类
 *
 * @author buhao
 */
public class BeanUtil {

	/**
	 * 注解，bean映射操作中，忽略操作的属性，通过set方法实现
	 *
	 * @author buhao
	 */
	@Target({ElementType.METHOD})
	@Retention(value = RetentionPolicy.RUNTIME)
	public static @interface BeanIgnore {
	}

	/**
	 * 解析bean的结果
	 *
	 * @author buhao
	 */
	public static class ParseBeanResult {
		private Object value;

		public ParseBeanResult(Object value) {
			super();
			this.value = value;
		}

		public Object getValue() {
			return value;
		}

	}

	/**
	 * 解析bean的拦截器
	 *
	 * @author buhao
	 */
	public static interface ParseBeanInterceptor {
		ParseBeanResult parse(Object value, Class<?> destClass, ParameterizedType pType, LinkedList<String> keys);
	}

	/**
	 * 解析bean的配置项
	 *
	 * @author buhao
	 */
	public static class ParseBeanOptions {
		private ParseBeanInterceptor interceptor;
		private LinkedList<String> contextKeys;

		public ParseBeanInterceptor getInterceptor() {
			return interceptor;
		}

		public ParseBeanOptions setInterceptor(ParseBeanInterceptor interceptor) {
			this.interceptor = interceptor;
			contextKeys = new LinkedList<String>();
			return this;
		}

	}

	/**
	 * bean异常
	 *
	 * @author buhao
	 */
	public static class BeanException extends Exception {

		public BeanException(String message) {
			super(message);
		}

		public BeanException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	/**
	 * map转bean，调用{@link BeanUtil#map2bean(Map, Object, ParseBeanOptions)}，默认的
	 * {@link ParseBeanOptions}
	 *
	 * @param map
	 * @param bean
	 * @return 转换后的bean对象
	 * @throws BeanException
	 */
	public static <T> T map2bean(Map<?, ?> map, T bean) throws BeanException {
		return map2bean(map, bean, null);
	}

	/**
	 * map转bean
	 *
	 * @param map
	 * @param bean
	 * @param options 解析bean的配置项，若为null，则构造一个新的options对象，且TimestampToDate为true
	 * @return 转换后的bean对象
	 * @throws BeanException
	 */
	public static <T> T map2bean(Map<?, ?> map, T bean, ParseBeanOptions options) throws BeanException {
		if (options == null) {
			options = new ParseBeanOptions();
		}
		Method[] methods = bean.getClass().getMethods();
		String methodName = null;
		String key = null;
		Object value = null;
		Map<String, Field> fields = getAllFields(bean.getClass());
		ParseBeanInterceptor interceptor = options.getInterceptor();
		int contextDepth = options.contextKeys != null ? options.contextKeys.size() : 0;
		for (Method method : methods) {
			if (Modifier.isStatic(method.getModifiers())) {
				continue;
			}
			Class<?>[] paramTypes = method.getParameterTypes();
			if (paramTypes.length != 1) {
				continue;
			}
			if (method.isAnnotationPresent(BeanIgnore.class)) {
				continue;
			}
			methodName = method.getName();
			if (!methodName.startsWith("set")) {
				continue;
			}
			key = getFieldName(methodName, 3);
			if (key == null) {
				continue;
			}
			value = map.get(key);
			if (value == null) {
				continue;
			}
			ParameterizedType pType = null;
			Field field = fields.get(key);
			if (field != null) {
				Type type = field.getGenericType();
				if (type instanceof ParameterizedType) {
					pType = (ParameterizedType) type;
				}
			}
			Class<?> destClass = method.getParameterTypes()[0];
			ParseBeanResult result = null;
			if (interceptor != null) {
				options.contextKeys.addLast(key);
				result = interceptor.parse(value, destClass, pType, options.contextKeys);
			}
			if (result != null) {
				value = result.getValue();
			} else {
				value = parse(value, destClass, pType, options);
			}
			if (options.contextKeys != null) {
				int removeSize = options.contextKeys.size() - contextDepth;
				for (int i = 0; i < removeSize; i++) {
					options.contextKeys.removeLast();
				}
			}
			if (value == null) {
				continue;
			}
			try {
				method.invoke(bean, value);
			} catch (Exception e) {
				throw new BeanException("Failed to invoke " + method.getName() + " of " + method.getDeclaringClass().getName(),
						                       e);
			}
		}
		return bean;
	}

	/**
	 * bean转换为序列化的map
	 *
	 * @param bean
	 * @return map集合
	 * @throws BeanException
	 */
	public static Map<String, Object> bean2serializedMap(Object bean) throws BeanException {
		Map<String, Object> map = JSONUtil.parseAsMap(JSONUtil.toJSONString(bean));
		if (map == null) {
			throw new BeanException("Failed to call bean2serializedMap");
		}
		return map;
	}

	/**
	 * 扩展bean
	 *
	 * @param dest bean对象（扩展目标）
	 * @param src  bean对象（扩展源）
	 * @return bean对象（扩展目标）
	 * @throws BeanException
	 */
	public static <T> T extend(T dest, Object src) throws BeanException {
		BeanUtil.map2bean(BeanUtil.bean2map(src), dest, null);
		return dest;
	}

	/**
	 * 获取class类的所有属性，包括父类，父类的父类...
	 *
	 * @param clazz
	 * @return 属性集合
	 */
	public static Map<String, Field> getAllFields(Class<?> clazz) {
		Map<String, Field> fields = new HashMap<String, Field>();
		getAllFields(clazz, fields);
		return fields;
	}

	/**
	 * bean转换为map
	 *
	 * @param bean 实体bean
	 * @return 转换的map
	 * @throws BeanException
	 */
	public static Map<String, Object> bean2map(Object bean) throws BeanException {
		Map<String, Object> map = new HashMap<String, Object>();
		Method[] methods = bean.getClass().getMethods();
		String methodName = null;
		String field = null;
		Object value = null;
		for (Method method : methods) {
			if (Modifier.isStatic(method.getModifiers())) {
				continue;
			}
			Class<?>[] paramTypes = method.getParameterTypes();
			if (paramTypes.length != 0) {
				continue;
			}
			if (method.isAnnotationPresent(BeanIgnore.class)) {
				continue;
			}
			methodName = method.getName();
			int offset = 0;
			if (methodName.startsWith("get")) {
				offset = 3;
			} else if (methodName.startsWith("is")) {
				offset = 2;
			} else {
				continue;
			}
			field = getFieldName(methodName, offset);
			if (field == null || field.equals("class")) {
				continue;
			}
			try {
				value = method.invoke(bean);
			} catch (Exception e) {
				throw new BeanException("Failed to invoke " + method.getName() + " of " + method.getDeclaringClass().getName(),
						                       e);
			}
			map.put(field, value);
		}
		return map;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 获取属性名
	 *
	 * @param methodName 方法名
	 * @param offset     偏移
	 * @return 属性名，偏移量>=方法名长度，返回null，截取长度为1，则返回一个长度的小写字符串，否则，返回首字母小写的字符串
	 */
	private static String getFieldName(String methodName, int offset) {
		if (methodName.length() <= offset) {
			return null;
		}
		String field = methodName.substring(offset);
		if (field.length() == 1) {
			return field.toLowerCase();
		}
		return field.toLowerCase().charAt(0) + field.substring(1);
	}

	/**
	 * 解析json string
	 *
	 * @param raw 待解析的对象
	 * @return 解析后的对象，若raw参数为string类型，则
	 */
	private static Object parseIfIsJSONString(Object raw) {
		if (raw instanceof String) {
			String s = (String) raw;
			if (JSONUtil.isJSONString(s)) {
				return JSON.parse(s);
			}
		}
		return raw;
	}

	/**
	 * * 解析对象
	 *
	 * @param raw       对象集合，数组或集合
	 * @param destClass 模版class
	 * @param pType
	 * @param options   解析配置项
	 * @return 解析后的新对象
	 * @throws BeanException
	 */
	private static Object parse(Object raw, Class<?> destClass, ParameterizedType pType, ParseBeanOptions options)
			throws BeanException {
		if (raw == null) {
			return null;
		}
		if (raw.getClass().equals(destClass)) {
			return raw;
		}

		// most case
		if (destClass.equals(String.class)) {
			return raw.toString();
		}
		if (destClass.equals(Boolean.class)) {
			return FormatUtil.parseBooleanStrictly(raw);
		}
		if (destClass.equals(boolean.class)) {
			return FormatUtil.parseBoolean(raw, Boolean.FALSE);
		}
		if (destClass.equals(Integer.class)) {
			return FormatUtil.parseInteger(raw);
		}
		if (destClass.equals(int.class)) {
			Integer integer = FormatUtil.parseInteger(raw);
			return integer == null ? 0 : integer.intValue();
		}
		if (destClass.equals(Long.class)) {
			return FormatUtil.parseLong(raw);
		}
		if (destClass.equals(long.class)) {
			Long l = FormatUtil.parseLong(raw);
			return l == null ? 0L : l.longValue();
		}
		if (destClass.equals(Double.class)) {
			return FormatUtil.parseDouble(raw);
		}
		if (destClass.equals(double.class)) {
			Double d = FormatUtil.parseDouble(raw);
			return d == null ? 0d : d.doubleValue();
		}
		if (destClass.equals(Float.class)) {
			return FormatUtil.parseFloat(raw);
		}
		if (destClass.equals(float.class)) {
			Float f = FormatUtil.parseFloat(raw);
			return f == null ? 0f : f.floatValue();
		}
		if (destClass.equals(Date.class)) {
			if (raw instanceof Date) {
				return raw;
			}
			if (raw instanceof Number) {
				return FormatUtil.timestampToDate(FormatUtil.parseLong(raw));
			}
			String s = raw.toString();
			if (StringUtil.isNumeric(s)) {
				return FormatUtil.timestampToDate(Long.valueOf(s));
			}
			return FormatUtil.parseISO8601Date(s);
		}
		if (destClass.equals(Short.class)) {
			return FormatUtil.parseShort(raw);
		}
		if (destClass.equals(short.class)) {
			Short s = FormatUtil.parseShort(raw);
			return s == null ? (short) 0 : s.shortValue();
		}
		if (destClass.equals(Byte.class)) {
			return FormatUtil.parseByte(raw);
		}
		if (destClass.equals(byte.class)) {
			Byte s = FormatUtil.parseByte(raw);
			return s == null ? (byte) 0 : s.byteValue();
		}
		if (destClass.equals(Character.class) || destClass.equals(char.class)) {
			return raw.toString().charAt(0);
		}

		// Array
		if (destClass.isArray()) {
			raw = parseIfIsJSONString(raw);
			return parseArray(raw, destClass.getComponentType(), options);
		}

		// Map
		if (Map.class.isAssignableFrom(destClass)) {
			raw = parseIfIsJSONString(raw);
			Class<?> valueClass = Object.class;
			Type type = (Type) destClass;
			if (type instanceof ParameterizedType) {
				pType = (ParameterizedType) type;
			}
			if (pType != null) {
				Type valueType = pType.getActualTypeArguments()[1];
				if (valueType instanceof Class) {
					valueClass = (Class<?>) valueType;
				}
			}
			@SuppressWarnings("unchecked")
			Map<?, Object> rawMap = (Map<?, Object>) raw;
			for (Entry<?, Object> entry : rawMap.entrySet()) {
				entry.setValue(parse(entry.getValue(), valueClass, null, options));
			}
			return rawMap;
		}

		// List/Set
		if (Collection.class.isAssignableFrom(destClass)) {
			raw = parseIfIsJSONString(raw);
			Class<?> componentClass = Object.class;
			Type type = (Type) destClass;
			if (type instanceof ParameterizedType) {
				pType = (ParameterizedType) type;
			}
			if (pType != null) {
				Type componentType = pType.getActualTypeArguments()[0];
				if (componentType instanceof Class) {
					componentClass = (Class<?>) componentType;
				}
			}
			if (List.class.isAssignableFrom(destClass)) {
				return parseCollection(raw, componentClass, new ArrayList<Object>(), options);
			} else if (Set.class.isAssignableFrom(destClass)) {
				return parseCollection(raw, componentClass, new HashSet<Object>(), options);
			}
		}

		// bean
		if (destClass.equals(Object.class)) {
			return raw;
		}

		raw = parseIfIsJSONString(raw);
		if (Map.class.isAssignableFrom(raw.getClass())) {
			Object object;
			try {
				object = destClass.newInstance();
			} catch (Exception e) {
				throw new BeanException("Failed to create instance of " + destClass, e);
			}
			return map2bean((Map<?, ?>) raw, object, options);
		}

		return raw;
	}

	/**
	 * 解析对象集合
	 *
	 * @param raw           对象集合，数组或集合
	 * @param componentType 需解析成的class
	 * @param newCollection 解析后的新集合
	 * @param options       解析配置项
	 * @return 解析后的新集合
	 * @throws BeanException
	 */
	private static Object parseCollection(Object raw, Class<?> componentType, Collection<Object> newCollection,
	                                      ParseBeanOptions options) throws BeanException {
		if (raw.getClass().isArray()) {
			int length = Array.getLength(raw);
			for (int i = 0; i < length; i++) {
				Object item = Array.get(raw, i);
				Object parsedItem = parse(item, componentType, null, options);
				newCollection.add(parsedItem);
			}
			return newCollection;
		} else if (raw instanceof Collection) {
			Collection<?> collection = (Collection<?>) raw;
			for (Object item : collection) {
				Object parsedItem = parse(item, componentType, null, options);
				newCollection.add(parsedItem);
			}
			return newCollection;
		}
		throw new IllegalArgumentException("Failed to parseCollection: " + raw.getClass());
	}

	/**
	 * 解析对象集合
	 *
	 * @param raw           对象集合，数组或集合
	 * @param componentType 需解析成的class
	 * @param options       解析配置项
	 * @return 解析后的新集合
	 * @throws BeanException
	 */
	private static Object parseArray(Object raw, Class<?> componentType, ParseBeanOptions options)
			throws BeanException {
		if (raw.getClass().isArray()) {
			int length = Array.getLength(raw);
			Object newArray = Array.newInstance(componentType, length);
			for (int i = 0; i < length; i++) {
				Object item = Array.get(raw, i);
				Object parsedItem = parse(item, componentType, null, options);
				Array.set(newArray, i, parsedItem);
			}
			return newArray;
		} else if (raw instanceof Collection) {
			Collection<?> collection = (Collection<?>) raw;
			int length = collection.size();
			Object newArray = Array.newInstance(componentType, length);
			int i = 0;
			for (Object item : collection) {
				Object parsedItem = parse(item, componentType, null, options);
				Array.set(newArray, i, parsedItem);
				i++;
			}
			return newArray;
		}
		throw new IllegalArgumentException("Failed to parseArray: " + raw.getClass());
	}

	/**
	 * 获取class的所有属性，若有父类，则加载父类属性，父类的父类...
	 *
	 * @param clazz    class类
	 * @param fieldMap 属性map对象
	 */
	private static void getAllFields(Class<?> clazz, Map<String, Field> fieldMap) {
		for (Field field : clazz.getDeclaredFields()) {
			if (!fieldMap.containsKey(field.getName())) {
				fieldMap.put(field.getName(), field);
			}
		}
		if (clazz.getSuperclass() != null) {
			getAllFields(clazz.getSuperclass(), fieldMap);
		}
	}

}
