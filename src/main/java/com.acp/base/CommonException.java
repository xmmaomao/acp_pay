package com.acp.base;

import com.acp.util.CollectionUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * Created by Administrator on 2016/8/19.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CommonException extends Exception {

	private Integer errorCode;
	private String message;

	public CommonException(String message) {
		this.errorCode = 1;
		this.message = message;
	}

	public static CommonException getDefaultError() {
		return new CommonException("系统繁忙");
	}

	public Map<String, Object> toMap() {
		return CollectionUtil.arrayAsMap("errorCode", errorCode, "message", message);
	}
}
