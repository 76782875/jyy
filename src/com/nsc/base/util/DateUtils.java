package com.nsc.base.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.nsc.base.conf.ConstConfig;

/**
 * ���ڹ�����
 * 
 * �������ڶ����ڽ���һ���������������ת�ַ����ȡ�
 * 
 * @author bs-team
 * 
 * @date Oct 19, 2010 10:34:08 AM
 * @version
 */
public class DateUtils {

	/**
	 * ���ص�ǰ������Ѯ�ĵ�һ������һ��
	 * 
	 * @param isBegin
	 * @return ��isBeginΪtrue����������Ѯ�ĵ�һ�����ڣ���ΪFALSE�򷵻�����Ѯ�����һ������
	 */
	public static Date getTenday(boolean isBegin) {
		Calendar cal = Calendar.getInstance();
		int beginDay;
		int endDay;
		// SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int dayofM = cal.get(Calendar.DAY_OF_MONTH);

		if (dayofM > 20) {
			beginDay = 21;
			endDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		} else if (dayofM > 10) {
			beginDay = 11;
			endDay = 20;
		} else {
			beginDay = 1;
			endDay = 10;
		}

		if (isBegin) {
			cal.set(year, month, beginDay);
		} else {
			cal.set(year, month, endDay);
		}
		return cal.getTime();
	}

	/**
	 * ���ص�ǰ�µ�ǰѮ��ǰ����һ��Ѯ�ĵ�һ�����ڻ����һ������
	 * 
	 * @param isBegin
	 *            trueΪ��һ�죬FALSEΪ���һ��
	 * @param tenday
	 *            ȡֵΪ1��-1
	 * @return 1Ϊ��һ��Ѯ���ڣ�-1Ϊǰһ��Ѯ����
	 */
	public static Date getAddTenday(boolean isBegin, int tenday) {
		Calendar cal = Calendar.getInstance();
		int beginDay;
		int endDay;

		Date beginDate = getDayAfterDays(getTenday(false), tenday);
		cal.setTime(beginDate);

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int dayofM = cal.get(Calendar.DAY_OF_MONTH);

		if (dayofM > 20) {
			beginDay = 21;
			endDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		} else if (dayofM > 10) {
			beginDay = 11;
			endDay = 20;
		} else {
			beginDay = 1;
			endDay = 10;
		}

		if (isBegin) {
			cal.set(year, month, beginDay);
		} else {
			cal.set(year, month, endDay);
		}
		return cal.getTime();
	}

	/**
	 * ��dateʱ��Ϊ��׼�������days��֮�������
	 * 
	 * @param date
	 *            ��׼ʱ��
	 * @param days
	 *            �������
	 * @return date֮��days�������
	 */
	public static Date getDayAfterDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

	/**
	 * ȡ�ø��µ����һ�������
	 * 
	 * @param aDate
	 *            ����
	 * @return �������һ�������
	 */
	@SuppressWarnings("deprecation")
	public static final Date getEndMonthDate(Date aDate) {
		int date = 30;
		if (aDate == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(aDate);
		date = cal.getActualMaximum(Calendar.DATE);
		aDate.setDate(date);
		return aDate;
	}

	/**
	 * ��һ�������ַ���dateStr��format����ʽ��������
	 * 
	 * @param dateStr
	 *            �����ַ���
	 * @param format
	 *            ���ڵĸ�ʽ�ַ���
	 * @return format��ʽ������
	 */
	public static Date StringToDate(String dateStr, String format) {
		if (dateStr == null || dateStr.length() == 0)
			return null;

		DateFormat dateFormat = new SimpleDateFormat(format);

		try {
			return dateFormat.parse(dateStr);
		} catch (ParseException ex) {
			throw new AppException(ex, "app.string.todate", null,
					new String[] {});
		}
	}

	/**
	 * ��������ʵ����format����ʽ��������
	 * 
	 * @param date
	 *            ����
	 * @param format
	 *            ���ڵĸ�ʽ�ַ���
	 * @return ��format����ʽ���õ�����
	 */
	public static String DateToString(Date date, String format) {
		if (date == null)
			return "";
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}

	/**
	 * ȡ�õ�������
	 * 
	 * @param aDate
	 * @return ��������
	 */
	public static final String getDate(Date aDate) {
		SimpleDateFormat df = null;
		String returnValue = "";

		if (aDate != null) {
			df = new SimpleDateFormat(ConstConfig.defaultDatePattern);
			returnValue = df.format(aDate);
		}

		return (returnValue);
	}

	/**
	 * ȡ��ָ����ʽ�ĵ�������
	 * 
	 * @param aMask
	 * @param aDate
	 * @return ��������
	 */
	public static final String getDate(String aMask, Date aDate) {
		SimpleDateFormat df = null;
		String returnValue = "";

		if (aDate == null) {
			return "";
		} else {
			df = new SimpleDateFormat(aMask);
			returnValue = df.format(aDate);
		}

		return (returnValue);
	}

	/**
	 * ����ʱ��֮�������������
	 * 
	 * @param one
	 *            ʱ����� 1��
	 * @param two
	 *            ʱ����� 2��
	 * @return �������
	 */
	public static long getDistanceDays(Date one, Date two){
		long days = 0;
		long time1 = one.getTime();
		long time2 = two.getTime();
		long diff;
		if (time1 < time2) {
			diff = time2 - time1;
		} else {
			diff = time1 - time2;
		}
		days = diff / (1000 * 60 * 60 * 24);
		return days;
	}

	/**
	 * ����ʱ����������������Сʱ���ٷֶ�����
	 * 
	 * @param str1
	 *            ʱ����� 1 ��ʽ��1990-01-01 12:00:00
	 * @param str2
	 *            ʱ����� 2 ��ʽ��2009-01-01 12:00:00
	 * @return long[] ����ֵΪ��{��, ʱ, ��, ��}
	 */
	public static long[] getDistanceTimes(String str1, String str2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date one;
		Date two;
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		try {
			one = df.parse(str1);
			two = df.parse(str2);
			long time1 = one.getTime();
			long time2 = two.getTime();
			long diff;
			if (time1 < time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			day = diff / (24 * 60 * 60 * 1000);
			hour = (diff / (60 * 60 * 1000) - day * 24);
			min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
			sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		} catch (ParseException e) {
			Logger.getLogger(DateUtils.class).warn("��������ʱ����������������Сʱ���ٷֶ������ַ�ת���쳣:",e);
		}
		long[] times = { day, hour, min, sec };
		return times;
	}

	/**
	 * ����ʱ����������������Сʱ���ٷֶ�����
	 * 
	 * @param str1
	 *            ʱ����� 1 ��ʽ��1990-01-01 12:00:00
	 * @param str2
	 *            ʱ����� 2 ��ʽ��2009-01-01 12:00:00
	 * @return String ����ֵΪ��xx��xxСʱxx��xx��
	 */
	public static String getDistanceTime(String str1, String str2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date one;
		Date two;
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		try {
			one = df.parse(str1);
			two = df.parse(str2);
			long time1 = one.getTime();
			long time2 = two.getTime();
			long diff;
			if (time1 < time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			day = diff / (24 * 60 * 60 * 1000);
			hour = (diff / (60 * 60 * 1000) - day * 24);
			min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
			sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		} catch (ParseException e) {
			Logger.getLogger(DateUtils.class).warn("��������ʱ����������������Сʱ���ٷֶ������ַ�ת���쳣:",e);
		}
		return day + "��" + hour + "Сʱ" + min + "��" + sec + "��";
	}

	public static final String DateToString(Date aDate) {
		return DateToString(aDate, ConstConfig.defaultDatePattern);
	}

	public static Date StringToDate(String strDate) {
		Date aDate = null;

		if (strDate == null || "".equals(strDate)) {
			return aDate;
		}

		aDate = StringToDate(ConstConfig.defaultDatePattern, strDate);

		return aDate;
	}
}
