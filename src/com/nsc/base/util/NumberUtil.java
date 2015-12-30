package com.nsc.base.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class NumberUtil {


    /**  
     * 
     * 
     *  @param  p1 ����
     *  @param  p2 ��ĸ
     *  @return 
      */ 
      public static String percent( double p1,  double p2){
        String str;
        double p3 =  p1 / p2;
        NumberFormat nf  =  NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(0);
        str  =  nf.format(p3);
        return  str;
      }

	/**
	 * �ַ���תΪDouble
	 * @param obj
	 * @return Double ����
	 */
  	public static double notNullDouble(String obj) {
		double re = 0d;
		if (obj != null && !"".equals(obj)) {
			re = Double.parseDouble(obj);
		}
		return re;
	}
  	
  	/**
  	 * ȡ�ðٷֱ�
  	 * @param val1
  	 * @param val2
  	 * @return ������ʽ����İٷֱ�ֵ
  	 */
	public float getPercentValue(float val1,float val2){
		float f = 0f;
		if(val2>0){
			f = (val1 / val2) * 100;
			f = toFloat(formatValue(f));
		}
		return f;
	}
	/**
	 * ��String��ת����Float��
	 * @param str
	 * @return floatֵ
	 */
	public float toFloat(String str) {
		float f = 0f;

		if (str != null && !"".equals(str.trim())) {
			f = Float.parseFloat(str);
		}

		return f;
	}

	/**
	 * �Ѷ���ת����Float��
	 * @param obj
	 * @return floatֵ
	 */
	public float notNullFloat(Object obj) {
		float f = 0f;
		if (obj == null) {
			f = 0f;
		} else {
			f = (Float) obj;
		}

		return f;
	}

	/**
	 * �Ѷ���0.00��ʽ����ʽ��
	 * @param obj ����ʽ������
	 * @return �ַ���
	 */
	public static String formatValue(Object obj){
		return formatValue(obj,"0.00");
	}
	
	/**
	 * �Ѷ���ָ����ʽ���и�ʽ��
	 * @param obj ����ʽ������
	 * @param pattern ��ʽ����ʽ
	 * @return �ַ���
	 */
	public static String formatValue(Object obj,String pattern){
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(obj);
	}
	
	/**
	 * �Ѷ����ʽ������С������ַ���
	 * @param obj
	 * @return
	 */
	public static String formatToIntStr(Object obj){
		return formatValue(obj,"0");
	}
	
}
