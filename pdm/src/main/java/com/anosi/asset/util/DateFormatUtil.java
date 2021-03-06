package com.anosi.asset.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFormatUtil {
	public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public static String getFormateDate(Date date) {
		SimpleDateFormat dateFormat = setFormat(DATE_PATTERN);
		String timeString = dateFormat.format(date);
		return timeString;
	}

	public static String getFormateDate(Date date, String pattern) {
		SimpleDateFormat dateFormat = setFormat(pattern);
		String timeString = dateFormat.format(date);
		return timeString;
	}

	private static SimpleDateFormat setFormat(String pattern) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat;
	}

	public static Date getDateByParttern(String date, String pattern) throws ParseException{
		SimpleDateFormat dateFormat = setFormat(pattern);
		Date resultDate = dateFormat.parse(date);
		return resultDate;
	}

	public static Date getDateByParttern(String date) {
		SimpleDateFormat dateFormat = setFormat(DATE_PATTERN);
		try {
			Date resultDate = dateFormat.parse(date);
			return resultDate;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String getMonthTimeQuantum() {
		// 获取当前日期前一个月日期到当前日期的时间段，格式:2016/02/12-2016/03/12
		Date date = new Date();// 当前日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 格式化对象
		Calendar calendar = Calendar.getInstance();// 日历对象
		calendar.setTime(date);// 设置当前日期
		calendar.add(Calendar.MONTH, -1);// 月份减一
		String startTimeString = sdf.format(calendar.getTime());
		String endTimeString = sdf.format(date);
		String timeQuantum = startTimeString + "~" + endTimeString;
		return timeQuantum;
	}

	public static Date getBeforeMonthTime(Date date, int i) {
		return getLaterMonthTime(date, -i);
	}

	public static Date getBeforeWeekTime(Date date, int i) {
		return getLaterWeekTime(date, -i);
	}

	public static Date getBeforeDayTime(Date date, int i) {
		return getLaterDayTime(date, -i);
	}

	public static Date getBeforeYearTime(Date date, int i) {
		return getLaterYearTime(date, -i);
	}

	public static Date getLaterMonthTime(Date date, int i) {
		Calendar calendar = Calendar.getInstance();// 日历对象
		calendar.setTime(date);// 设置当前日期
		calendar.add(Calendar.MONTH, i);// 月份减一
		Date startTimeString = calendar.getTime();
		return startTimeString;
	}

	public static Date getLaterWeekTime(Date date, int i) {
		Calendar calendar = Calendar.getInstance();// 日历对象
		calendar.setTime(date);// 设置当前日期
		calendar.add(Calendar.WEDNESDAY, i);//
		Date startTimeString = calendar.getTime();
		return startTimeString;
	}

	public static Date getLaterDayTime(Date date, int i) {
		Calendar calendar = Calendar.getInstance();// 日历对象
		calendar.setTime(date);// 设置当前日期
		calendar.add(Calendar.DATE, i);//
		Date startTimeString = calendar.getTime();
		return startTimeString;
	}

	public static Date getLaterYearTime(Date date, int i) {
		Calendar calendar = Calendar.getInstance();// 日历对象
		calendar.setTime(date);// 设置当前日期
		calendar.add(Calendar.YEAR, i);//
		Date startTimeString = calendar.getTime();
		return startTimeString;
	}

	public static boolean compareYear(Date date1, Date date2) {
		Calendar calendar1 = Calendar.getInstance();// 日历对象
		calendar1.setTime(date1);// 设置当前日期

		Calendar calendar2 = Calendar.getInstance();// 日历对象
		calendar2.setTime(date2);// 设置当前日期
		return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR);
	}

	public static boolean compareMonth(Date date1, Date date2) {
		Calendar calendar1 = Calendar.getInstance();// 日历对象
		calendar1.setTime(date1);// 设置当前日期

		Calendar calendar2 = Calendar.getInstance();// 日历对象
		calendar2.setTime(date2);// 设置当前日期
		return calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH);
	}

	public static boolean compareDay(Date date1, Date date2) {
		Calendar calendar1 = Calendar.getInstance();// 日历对象
		calendar1.setTime(date1);// 设置当前日期

		Calendar calendar2 = Calendar.getInstance();// 日历对象
		calendar2.setTime(date2);// 设置当前日期
		return calendar1.get(Calendar.DATE) == calendar2.get(Calendar.DATE);
	}

	/**
	 * 计算两个日期之间相差的天数
	 * 
	 * @param smdate
	 *            较小的时间
	 * @param bdate
	 *            较大的时间
	 * @return 相差天数
	 */
	public static int daysBetween(Date smdate, Date bdate) {
		long between_days = (bdate.getTime() - smdate.getTime()) / (1000 * 3600 * 24);
		return ((Number)between_days).intValue();
	}

	/**
	 * 计算两个日期之间相差的小时数
	 *
	 * @param smdate
	 *            较小的时间
	 * @param bdate
	 *            较大的时间
	 * @return 相差小时数
	 */
	public static int daysBetweenHours(Date smdate, Date bdate) {
		long between_days = (bdate.getTime() - smdate.getTime()) / (1000 * 3600);
		return ((Number)between_days).intValue();
	}

	/**
	* 获取当日0点0分0秒的时间
	* @since  2018/7/31 11:23
	* @author 倪文骅 
	* @param   
	* @return java.util.Date
	*/
	public static Date initDateOfToday(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		return calendar.getTime();
	}
}
