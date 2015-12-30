package com.nsc.dem.util.filter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nsc.base.filter.ExceptionBaseFilter;

public class EDMExceptionFilter extends ExceptionBaseFilter{

	@Override
	public void doException(Exception e,ServletRequest request) {

		Log logger = LogFactory.getLog(getClass());
		//��֯��־��Ϣ
		logger.error(e.getMessage(), e.getCause());
		
		//��֯������Ϣ
		request.setAttribute("userMsg", "ϵͳ�����˲���Ԥ֪�Ĵ��󣬸ô����Ѿ�����¼����ȴ���֪ͨϵͳ����Ա�����");
		request.setAttribute("errMsg", e.getMessage());
	}

	public void destroy() {
		//do nothing
	}

	public void init(FilterConfig arg0) throws ServletException {
		//do nothing
	}

	@Override
	public void doException(Throwable e, ServletRequest request) {
		Log logger = LogFactory.getLog(getClass());
		
		//��֯��־��Ϣ
		logger.error(e.getMessage(), e.getCause());
		
		//��֯������Ϣ
		request.setAttribute("userMsg", "ϵͳ�����˲���Ԥ֪�Ĵ��󣬸ô����Ѿ�����¼����ȴ���֪ͨϵͳ����Ա�����");
		request.setAttribute("errMsg", e.getMessage());
		
	}
}
