package com.nsc.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * �ж����ʱ�����Ƿ�Ϊ����,�Ƿ�Ҫ��¼����־
 * @author ycl
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuditAnnotation {
	public boolean isRequired();
}
