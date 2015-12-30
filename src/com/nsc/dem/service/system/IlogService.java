package com.nsc.dem.service.system;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.bean.system.TDictionary;
import com.nsc.dem.bean.system.TLogFile;
import com.nsc.dem.bean.system.TOperateLogTemp;
import com.nsc.dem.service.base.IService;

public interface IlogService extends IService {
	
	
	/**
	 * ��־����--��־�鿴--ϵͳ��־ 
	 * ��Ҫ���ݲ�ѯ�����Ӳ�����־���л�ȡ��־�б���Ϣ
	 * @param  ����Ϊ��־ʵ����
	 * @return
	 */
	public List<Object> logInfoList();
	/**
	 * ��־����--��־�鿴--������־ 
	 * ��Ҫ��ѯ��oracle�Զ���¼����־��Ϣ
	 * @param  ����Ϊ��־ʵ����
	 * @return
	 */
	public List<Object> logOracleInfoList();
	
	
	/**
	 * ��־����--��־���� (�ֶ�����)
	 */
	@SuppressWarnings("unchecked")
	public List logBackupHand(Map<String,Object> map);
	
	/*
	 * ɾ�����ݹ�����־
	 */
	public void deleteLog(Map<String,Object> map);
	
	/**
	 * ��־����--��־ɾ�� (��ѯ=ɾ������)
	 * @param  
	 */
	public List<TLogFile> logDelList(Map<String,Object> map);
	
	/**
	 * ��־��Ϣ -- �ĵ������������ 
	 * @param  
	 */
	public Date logLastUpdate();
	
	/**
	 * ������־�����ֵ�
	 * @return
	 */
	public List<TDictionary> docLogDicList();
	
	/**
	 * ϵͳ��־�����ֵ�
	 * @return
	 */
	public List<TDictionary> sysLogDicList();
	@SuppressWarnings("unchecked")
	public List queryOperateLogList(Object[] obj,int firstResult,int maxResults,TableBean table) throws Exception;

	
	List<Object[]> queryBackupList(Map<String,Object> map,int firstResult,int maxResults,TableBean table)throws Exception;
	
	List<Object[]> queryLogDeleteList(Map<String,Object> map,int firstResult,int maxResults,TableBean table)throws Exception;

	/**
	 * ����־���Ͳ�ѯ��־
	 * @param typeName
	 * @return
	 */
	
	
	public List<TOperateLogTemp> findOperateTempLog(Map<String,Object> map);
	
	/**
	 * ɾ�����е���־��ʱ������
	 */
	public void deleteAllTempOperateLog();
}
