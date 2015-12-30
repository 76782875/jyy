package com.nsc.base.util;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * ������
 * 
 * ��������Java����ĵײ���������÷���ԭ��Զ�����е��á�
 * 
 * @author bs-team
 *
 * @date Oct 19, 2010 10:35:32 AM
 * @version
 */
@SuppressWarnings("all") 
public class Reflections {
	/**
	 * ʹ��target��method
	 * 
	 * @param method
	 *            ����
	 * @param target
	 *            ����ʵ��
	 * @param args
	 *            ��������
	 * @return
	 * @throws Exception
	 */
	public static Object invoke(Method method, Object target, Object... args)
			throws Exception {
		try {
			return method.invoke(target, args);
		} catch (IllegalArgumentException iae) {
			String message = "Could not invoke method by reflection: "
					+ toString(method);
			if (args != null && args.length > 0) {
				message += " with parameters: (" + args.toString() + ')';
			}
			message += " on: " + target.getClass().getName();
			throw new IllegalArgumentException(message, iae);
		} catch (InvocationTargetException ite) {
			if (ite.getCause() instanceof Exception) {
				throw (Exception) ite.getCause();
			} else {
				throw ite;
			}
		}
	}

	/**
	 * ȡ��target����fieldֵ
	 * 
	 * @param field
	 * @param target
	 * @return
	 * @throws Exception
	 */
	public static Object get(Field field, Object target) throws Exception {
		try {
			return field.get(target);
		} catch (IllegalArgumentException iae) {
			String message = "Could not get field value by reflection: "
					+ toString(field) + " on: " + target.getClass().getName();
			throw new IllegalArgumentException(message, iae);
		}
	}

	/**
	 * �趨target����fieldֵΪvalue
	 * 
	 * @param field
	 * @param target
	 * @param value
	 * @throws Exception
	 */
	public static void set(Field field, Object target, Object value)
			throws Exception {
		try {
			field.set(target, value);
		} catch (IllegalArgumentException iae) {
			// target may be null if field is static so use
			// field.getDeclaringClass() instead
			String message = "Could not set field value by reflection: "
					+ toString(field) + " on: "
					+ field.getDeclaringClass().getName();
			if (value == null) {
				message += " with null value";
			} else {
				message += " with value: " + value.getClass();
			}
			throw new IllegalArgumentException(message, iae);
		}
	}

	/**
	 * ����ȡ�÷���
	 * @param field
	 * @param target
	 * @return
	 */
	public static Object getAndWrap(Field field, Object target) {
		try {
			return get(field, target);
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			} else {
				throw new IllegalArgumentException("exception setting: "
						+ field.getName(), e);
			}
		}
	}

	/**
	 * �������÷���
	 * @param field
	 * @param target
	 * @param value
	 */
	public static void setAndWrap(Field field, Object target, Object value) {
		try {
			set(field, target, value);
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			} else {
				throw new IllegalArgumentException("exception setting: "
						+ field.getName(), e);
			}
		}
	}

	/**
	 * ���÷���
	 * @param method
	 * @param target
	 * @param args
	 * @return ���÷����ķ���ֵ
	 */
	public static Object invokeAndWrap(Method method, Object target,
									   Object... args) {
		try {
			return invoke(method, target, args);
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			} else {
				throw new RuntimeException("exception invoking: "
						+ method.getName(), e);
			}
		}
	}

	/**
	 * ������������ת�����ַ���
	 * 
	 * @param method
	 * @return
	 */
	private static String toString(Method method) {
		return method.getDeclaringClass().getName() + '.' + method.getName()
				+ '(' + method.getDeclaringClass().getName() + ')';
	}

	/**
	 * �����Ե�����ת�����ַ���
	 * 
	 * @param field
	 * @return
	 */
	private static String toString(Field field) {
		return field.getDeclaringClass().getName() + '.' + field.getName();
	}

	/**
	 * ����һ����name�ַ���Ϊ���Ƶ����ʵ��
	 * 
	 * @param name
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> classForName(String name)
			throws ClassNotFoundException {
		try {
			return Thread.currentThread().getContextClassLoader().loadClass(
					name);
		} catch (Exception e) {
			return Class.forName(name);
		}
	}

	/**
	 * Return's true if the class can be loaded using Reflections.classForName()
	 */
	public static boolean isClassAvailable(String name) {
		try {
			classForName(name);
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}

	public static Class<?> getCollectionElementType(Type collectionType) {
		if (!(collectionType instanceof ParameterizedType)) {
			throw new IllegalArgumentException(
					"collection type not parameterized");
		}
		Type[] typeArguments = ((ParameterizedType) collectionType)
				.getActualTypeArguments();
		if (typeArguments.length == 0) {
			throw new IllegalArgumentException(
					"no type arguments for collection type");
		}
		Type typeArgument = typeArguments.length == 1 ? typeArguments[0]
				: typeArguments[1]; // handle Maps
		if (!(typeArgument instanceof Class)) {
			throw new IllegalArgumentException("type argument not a class");
		}
		return (Class<?>) typeArgument;
	}

	/**
	 * 
	 * @param collectionType
	 * @return
	 */
	public static Class<?> getMapKeyType(Type collectionType) {
		if (!(collectionType instanceof ParameterizedType)) {
			throw new IllegalArgumentException(
					"collection type not parameterized");
		}
		Type[] typeArguments = ((ParameterizedType) collectionType)
				.getActualTypeArguments();
		if (typeArguments.length == 0) {
			throw new IllegalArgumentException(
					"no type arguments for collection type");
		}
		Type typeArgument = typeArguments[0];
		if (!(typeArgument instanceof Class)) {
			throw new IllegalArgumentException("type argument not a class");
		}
		return (Class<?>) typeArgument;
	}

	/**
	 * ����clazz�����ַ���name����set����
	 * 
	 * @param clazz
	 * @param name
	 * @return
	 */
	public static Method getSetterMethod(Class<?> clazz, String name) {
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			String methodName = method.getName();
			if (methodName.startsWith("set")) {
				if (Introspector.decapitalize(methodName.substring(3)).equals(
						name)) {
					return method;
				}
			}
		}
		throw new IllegalArgumentException("no such setter method: "
				+ clazz.getName() + '.' + name);
	}

	/**
	 * ����clazz�����ַ���name����get����
	 * 
	 * @param clazz
	 * @param name
	 * @return
	 */
	public static Method getGetterMethod(Class<?> clazz, String name) {
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			String methodName = method.getName();
			if (methodName.matches("^(get|is).*")
					&& method.getParameterTypes().length == 0) {
				if (Introspector.decapitalize(methodName.substring(3)).equalsIgnoreCase(
						name)) {
					return method;
				}
			}
		}
		throw new IllegalArgumentException("no such getter method: "
				+ clazz.getName() + '.' + name);
	}

	/**
	 * ����ע�õ����е�Getter����
	 * @param clazz
	 * @param annotation
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Method> getGetterMethods(Class<?> clazz, Class annotation) {
		List<Method> methods = new ArrayList<Method>();
		for (Method method : clazz.getMethods()) {
			if (method.isAnnotationPresent(annotation)) {
				methods.add(method);
			}
		}
		return methods;
	}

	/**
	 * �õ����е�Getter����
	 * @param clazz
	 * @return
	 */
	public static List<Method> getGetterMethods(Class<?> clazz) {
		List<Method> methods = new ArrayList<Method>();
		for (Method method : clazz.getMethods()) {
			if (method.getName().matches("^(get|is).*")
					&& method.getParameterTypes().length == 0) {
				methods.add(method);
			}
		}
		return methods;
	}
	
	/**
	 * �õ��������е�Getter����,�������̳�����
	 * @param clazz
	 * @return
	 */
	public static List<Method> getCurrentClassGetterMethods(Class<?> clazz) {
		List<Method> methods = new ArrayList<Method>();
		for (Method method : clazz.getDeclaredMethods()) {
			if (method.getName().startsWith("get")
					&& method.getParameterTypes().length == 0) {
				methods.add(method);
			}
		}
		return methods;
	}
	
	/**
	 * ����clazz�������ַ���name����Ϊ���Ƶ�����
	 * 
	 * @param clazz
	 * @param name
	 * @return
	 */
	public static Field getField(Class<?> clazz, String name) {
		for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				Field[] fields = superClass.getDeclaredFields();
				for(Field field:fields){
					if(field.getName().equalsIgnoreCase(name)){
						return  field;
					}
				}
			} catch (Exception nsfe) {
			}
		}
		throw new IllegalArgumentException("no such field: " + clazz.getName()
				+ '.' + name);
	}
	
	/**
	 * ����clazz�����ַ���name���Ƶķ���(����������
	 * @param clazz
	 * @param name
	 * @return
	 */
	public static Method getMethod(Class<?> clazz, String name) {
		return getMethod(clazz,name,null);
	}

	/**
	 * ����clazz�����ַ���name���Ƶķ���
	 * @param clazz
	 * @param name
	 * @param types
	 * @return
	 */
	public static Method getMethod(Class<?> clazz, String name,Class<?>[] types) {
		for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				return superClass.getDeclaredMethod(name,types);
			} catch (NoSuchMethodException nsme) {
			}
		}
		throw new IllegalArgumentException("no such method: " + clazz.getName()
				+ '.' + name);
	}

	/**
	 * ���ݱ�ע�õ��ƶ���������ֶ�
	 * @param clazz
	 * @param annotation
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Field> getFields(Class<?> clazz, Class annotation) {
		List<Field> fields = new ArrayList<Field>();
		for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			for (Field field : superClass.getDeclaredFields()) {
				if (field.isAnnotationPresent(annotation)) {
					fields.add(field);
				}
			}
		}
		return fields;
	}

	/**
	 * �õ�ָ����������ֶ�
	 * @param clazz
	 * @return
	 * @deprecated �˷�����
	 */
	public static List<Field> getFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();
		for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			for (Field field : superClass.getDeclaredFields()) {
				fields.add(field);
			}
		}
		return fields;
	}

	/**
	 * �����Ƶõ�����
	 * @param annotation
	 * @param name
	 * @return
	 */
	public static Method getMethod(Annotation annotation, String name) {
		try {
			return annotation.annotationType().getMethod(name);
		} catch (NoSuchMethodException nsme) {
			return null;
		}
	}

	/**
	 * �ж�clazz�������Ƿ����ַ���name���ݵĵ�����
	 * 
	 * @param clazz
	 * @param name
	 * @return
	 */
	public static boolean isInstanceOf(Class<?> clazz, String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null");
		}
		for (Class<?> c = clazz; c != Object.class; c = c.getSuperclass()) {
			if (name.equals(c.getName())) {
				return true;
			}
		}
		for (Class<?> c : clazz.getInterfaces()) {
			if (name.equals(c.getName())) {
				return true;
			}
		}
		return false;
	}
}
