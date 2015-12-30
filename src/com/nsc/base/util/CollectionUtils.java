package com.nsc.base.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * ���ϲ�����
 * 
 * �������ڶԼ��϶�����в������������ҡ�����ȡ�
 * 
 * @author bs-team
 *
 * @date Oct 19, 2010 10:31:55 AM
 * @version
 */
public class CollectionUtils {

	/**
	 * ��List �е����鰴��������ĳһ���ֶν��з��࣬����hashmap�С�
	 * 
	 * @param list
	 * @param index
	 *            �����е��ֶ����кţ���0��ʼ
	 * @return
	 */
	public static HashMap<String, List<Object[]>> getUnitDataMap(
			List<Object[]> list, int index) {
		HashMap<String, List<Object[]>> dataMap = new HashMap<String, List<Object[]>>();
		for (int i = 0; i < list.size(); i++) {
			Object obj = list.get(i)[index];
			String unit = obj.toString();

			if (dataMap.containsKey(unit)) {
				dataMap.get(unit).add((Object[]) list.get(i));
			} else {
				ArrayList<Object[]> rowdata = new ArrayList<Object[]>();
				rowdata.add((Object[]) list.get(i));
				dataMap.put(unit, rowdata);
			}
		}

		return dataMap;
	}

	/**
	 * 
	 * @param list
	 * @param findex
	 * @param sindex
	 * @return
	 */
	public static HashMap<String, HashMap<String, List<Object[]>>> getUnitDataHashMap(
			List<Object[]> list, int findex, int sindex) {

		HashMap<String, List<Object[]>> dataMap = new HashMap<String, List<Object[]>>();
		for (int i = 0; i < list.size(); i++) {
			Object obj = list.get(i)[findex];
			String unit = obj.toString();

			if (dataMap.containsKey(unit)) {
				dataMap.get(unit).add((Object[]) list.get(i));
			} else {
				ArrayList<Object[]> rowdata = new ArrayList<Object[]>();
				rowdata.add((Object[]) list.get(i));
				dataMap.put(unit, rowdata);
			}
		}
		Iterator<String> keys = dataMap.keySet().iterator();

		HashMap<String, HashMap<String, List<Object[]>>> returnMap = new HashMap<String, HashMap<String, List<Object[]>>>();

		while (keys.hasNext()) {
			Object keyStr = keys.next();
			String company = String.valueOf(keyStr);// ���ﶨ�����վ������
			List<Object[]> getDate = dataMap.get(company);
			HashMap<String, List<Object[]>> getMap = getUnitDataMap(getDate,
					sindex);

			returnMap.put(company, getMap);
		}

		return returnMap;

	}

	/**
	 * ��List �е����鰴��������ĳһ���ֶν��з��࣬����hashmap�С�
	 * 
	 * @param list
	 * @param indexnum
	 *            �����е��ֶ����кţ���0��ʼ
	 * @return
	 */
	public static HashMap<String, List<Object[]>> getUnitDataMapList(
			List<Object[]> list, int[] indexnum) {
		HashMap<String, List<Object[]>> dataMap = new HashMap<String, List<Object[]>>();
		for (int i = 0; i < list.size(); i++) {
			StringBuffer returnStringBuffer = new StringBuffer();
			for (int ai = 0; ai < indexnum.length; ai++) {
				int index = indexnum[ai];
				Object obj = list.get(i)[index];
				String gunit = obj.toString();
				if (ai == 0) {
					returnStringBuffer.append(gunit);
				} else {
					returnStringBuffer.append("(" + gunit + ")");
				}

			}
			String unit = returnStringBuffer.toString();
			if (dataMap.containsKey(unit)) {
				dataMap.get(unit).add((Object[]) list.get(i));
			} else {
				ArrayList<Object[]> rowdata = new ArrayList<Object[]>();
				rowdata.add((Object[]) list.get(i));
				dataMap.put(unit, rowdata);
			}
		}

		return dataMap;
	}

	/**
	 * ȡ��list����������ΪfieldName ֵΪvalue�Ķ���
	 * 
	 * @param list
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public static Object getObjectByValue(List<?> list, String fieldName,
			Object value) {

		for (Object o : list) {
			Object value2 = BeanUtil.getFieldValue(fieldName, o);
			if (value.equals(value2))
				return o;
		}

		return null;
	}

	/**
	 * ȡ��list�У���������indexֵΪvalue�Ķ���
	 * 
	 * @param list
	 * @param index
	 * @param value
	 * @return
	 */
	public static Object[] getObjectByValue(List<?> list, int index,
			Object value) {
		for (Object o : list) {
			Object[] os = (Object[]) o;
			if (value.equals(os[index]))
				return os;
		}
		return null;
	}

	/**
	 * ����list�ж�������ĵ�index������з���
	 * 
	 * @param list
	 * @param index
	 */
	public static void groupListData(List<Object[]> list, int index) {

		String last = null;
		// sortListData(list,field);
		for (int i = 0; i < list.size(); i++) {
			Object[] objs = list.get(i);
			if (!objs[index].equals(last)) {

				Object[] temp = objs.clone();

				for (int j = 0; j < temp.length; j++) {
					if (j != index) {
						temp[j] = null;
					}
				}

				list.add(i, temp);

				last = String.valueOf(objs[index]);
			}
		}
	}

	/**
	 * ����list�ж����field���Խ��з��飬��������ĸ���
	 * 
	 * @param list
	 * @param field
	 * @param asc
	 * @return
	 */
	public static int groupListData(List<Object> list, String field, boolean asc) {
		Object last = null;
		int groupNum = 0;
		try {
			sortListData(list, field, asc);
			for (int i = 0; i < list.size(); i++) {
				Method method = Reflections.getGetterMethod(list.get(i)
						.getClass(), field);
				Object value = Reflections.invoke(method, list.get(i),
						new Object[] {});
				if (!value.equals(last)) {
					Object temp = list.get(i).getClass().newInstance();
					Method md = Reflections.getSetterMethod(temp.getClass(),
							field);
					Reflections.invoke(md, temp, new Object[] { value });
					list.add(i, temp);
					last = value;
					groupNum++;
				}
			}
		} catch (Exception ex) {
			throw new AppException(ex, "app.collection.group", null,
					new String[] {});
		}

		return groupNum;
	}

	/**
	 * ��list�ж���field���Խ�������
	 * 
	 * @param list
	 * @param field
	 * @param asc
	 *            Ϊ1��ʱ��Ϊ����-1Ϊ����
	 */
	public static void sortListData(List<Object> list, final String field,
			final boolean asc) {

		Collections.sort(list, new Comparator<Object>() {
			private int order = asc ? 1 : -1;

			public int compare(Object o1, Object o2) {
				try {
					Method method = Reflections.getGetterMethod(o1.getClass(),
							field);
					Object value1 = Reflections.invoke(method, o1,
							new Object[] {});

					method = Reflections.getGetterMethod(o2.getClass(), field);
					Object value2 = Reflections.invoke(method, o2,
							new Object[] {});

					if (value1 instanceof String) {
						return order
								* ((String) value1).compareTo((String) value2);
					} else if (value1 instanceof Long) {
						return order * ((Long) value1).compareTo((Long) value2);
					} else if (value1 instanceof Float) {
						return order
								* ((Float) value1).compareTo((Float) value2);
					} else {
						return 0;
					}
				} catch (Exception ex) {
					throw new AppException(ex, "app.collection.sort", null,
							new String[] {});
				}
			}
		});
	}
}
