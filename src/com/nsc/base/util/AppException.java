package com.nsc.base.util;


/**
 * Ӧ���쳣��
 * 
 * �������ڱ�����ҵ���߼������з����������쳣��������չ��RuntimeException������������ϵͳ�Զ�����쳣���ݡ�
 * 
 * @author bs-team
 *
 * @date Oct 19, 2010 10:28:45 AM
 * @version
 */
public class AppException extends RuntimeException {	
	static final long serialVersionUID = -7034897190745791228L;
	
	private String sysMId;
	private String userMId;
	private String originalM;
	private Object[] arguments;
	
	/**
	 * ���캯��
	 * @param ex �쳣
	 * @param sid ϵͳ��ϢID
	 * @param uid �û���ϢID
	 * @param arguments ������Ϣ
	 */
	public AppException(Exception ex,String sid,String uid,Object[] arguments){
		super(ex);
		sysMId=sid;
		userMId=uid;
		originalM=ex.getMessage();
		this.arguments=arguments;
	}
	
	/**
	 * ���캯��
	 * @param msg ������Ϣ
	 * @param sid ϵͳ��ϢID
	 * @param uid �û���ϢID
	 * @param arguments ������Ϣ
	 */
	public AppException(String msg,String sid,String uid,Object[] arguments){
		super();
		sysMId=sid;
		userMId=uid;
		originalM=msg;
		this.arguments=arguments;
	}

	/**
	 * ȡ��ϵͳ��Ϣ��ʶ
	 * @return ϵͳ��Ϣ��ʶ
	 */
	public String getSysMId() {
		return sysMId;
	}

	/**
	 * ȡ���û���Ϣ��ʶ
	 * @return �û���Ϣ��ʶ
	 */
	public String getUserMId() {
		return userMId;
	}

	/**
	 * ȡ��ԭʼ��Ϣ
	 * @return ԭʼ��Ϣ
	 */
	public String getOriginalM() {
		return originalM;
	}

	/**
	 * ȡ�ò�����Ϣ
	 * @return ��������
	 */
	public Object[] getArguments() {
		return arguments;
	}
}
