package com.nsc.dem.util.interceptor;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.nsc.base.struts.interceptor.ExceptionBaseInterceptor;
import com.nsc.base.util.AppException;
import com.opensymphony.xwork2.ActionSupport;

public class ExceptionInterceptor extends ExceptionBaseInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9102209382511010849L;

	@Override
	protected void exception(Exception e, ActionSupport actionSupport) {

		//BaseAction baseAction = (BaseAction) actionSupport;

		List<String> msgList = new ArrayList<String>();
		
		Logger logger=Logger.getLogger(ExceptionInterceptor.class);

		if (e instanceof AppException) {
			AppException app = (AppException) e;
			
			//��֯��־��Ϣ
			logger.warn(app.getUserMId());
			logger.warn(app.getOriginalM(),e.getCause());
			
			//��֯���������Ϣ
			StringBuffer sb = new StringBuffer();
			sb.append("���ʳ����쳣,����������:");
			sb.append(app.getUserMId());

			msgList.add(sb.toString());
			actionSupport.setActionErrors(msgList);
		} else {
			//��֯��־��Ϣ
			logger.warn(e.getMessage(),e);
			
			//��֯���������Ϣ
			msgList.add(e.getMessage());
			actionSupport.setActionErrors(msgList);
		}
	}

	@Override
	protected void exception(Throwable e, ActionSupport actionSupport) {
		//BaseAction baseAction = (BaseAction) actionSupport;

		//Logger logger = baseAction.getLogManager().getLogger(ExceptionInterceptor.class);
		
		Logger logger=Logger.getLogger(ExceptionInterceptor.class);

		List<String> msgList = new ArrayList<String>();
		
		//��֯��־��Ϣ
		logger.warn(e.getMessage(),e);
		
		//��֯���������Ϣ
		msgList.add(e.getMessage());
		actionSupport.setActionErrors(msgList);		
	}
}
