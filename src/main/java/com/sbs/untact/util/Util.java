package com.sbs.untact.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class Util {

	// 현재 날짜를 String으로 리턴하는 유틸
	public static String getNowDateStr() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date time = new Date();
		return format.format(time);
	}

	// 간단하게 Map을 만들어주는 유틸
	public static Map<String, Object> mapOf(Object... args) {
		// Object... args 알고리즘을 갯수 제한없이 받을 수 있게 해준다

		// 만약 들어온 args이 짝수가 아니면
		// 즉, 키,값 조합이 아니면 throw
		if (args.length % 2 != 0) {
			// throw: 프로그램이 바로 끝남
			throw new IllegalArgumentException("인자를 짝수개 입력해주세요.");
		}

		int size = args.length / 2;

		Map<String, Object> map = new LinkedHashMap<>();

		for (int i = 0; i < size; i++) {
			int keyIndex = i * 2;
			int valueIndex = keyIndex + 1;

			String key;
			Object value;

			try {
				key = (String) args[keyIndex];
			} catch (ClassCastException e) {
				throw new IllegalArgumentException("키는 String으로 입력해야 합니다. " + e.getMessage());
			}

			value = args[valueIndex];

			map.put(key, value);
		}

		return map;
	}

}
