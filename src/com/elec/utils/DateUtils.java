package com.elec.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	/**将日期类型转换成String类型，file的格式*/
	public static String dateToStringByFile(Date date) {
		String sDate = new SimpleDateFormat("/yyyy/MM/dd/").format(date);
		return sDate;
	}
	/**将日期类型转换成String类型，yyyy-MM-dd HH:mm:ss格式*/
	public static String dateToString(Date date) {
		String sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		return sDate;
	}
	/**将日期类型转换成String类型，yyyyMMddHHmmss格式*/
	public static String dateToStringWithExcel(Date date) {
		String sDate = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
		return sDate;
	}
	/**将String类型转换成日期类型：格式：yyyy-MM-dd*/
	public static Date stringToDate(String sDate) {
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(sDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}


}
