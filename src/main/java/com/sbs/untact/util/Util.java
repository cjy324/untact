package com.sbs.untact.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
	
	// 현재 날짜를 String으로 리턴하는 유틸
	public static String getNowDateStr() {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date time = new Date();
		return format.format(time);
	}

}
