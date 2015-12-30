package com.nsc.dem.util.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.nsc.base.conf.Configurater;
import com.nsc.base.conf.ConstConfig;
import com.nsc.base.filter.AuthenticationBaseFilter;
import com.nsc.dem.bean.profile.TUser;

public class AuthenticationEDMFilter extends AuthenticationBaseFilter {

	@Override
	public boolean authenticate(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {

		HttpSession session = req.getSession();
		StringBuffer reqUrl = req.getRequestURL();
		String principal = req.getParameter("edm_principal");
		String principal2 = req.getParameter("portal_principal");
		// ����ǵ����¼
		if (principal != null) {
			req.setAttribute("res", res);
			session.setAttribute("edm_principal", req);
			//��Ȼû�и��û���Ϣ�������¼ʧ��
			
			if(session.getAttribute(ConstConfig.USER_KEY) == null){
				return false;
			}
			res.setHeader("P3P","CP=CAO PSA OUR");
			return true;
		//����ǽ�����΢�����¼
		} else if(principal2!=null){
			//��Ȼû�и��û���Ϣ�������¼ʧ��
			if(session.getAttribute(ConstConfig.USER_KEY) == null){
				return false;
			}
			res.setHeader("P3P","CP=CAO PSA OUR");
			return true;
		} 
		
		//�û��Ѿ���¼
		if(session.getAttribute(ConstConfig.USER_KEY) != null){
			TUser user = (TUser) session.getAttribute(ConstConfig.USER_KEY);
			Logger.getLogger(AuthenticationEDMFilter.class).info(
					"��ǰ�û���" + user.getLoginId());
			return true;
		}else{//����û����ʵĵ�ַ��������еĵ�ַ �������
			for(String url : super.allowPassUrl){
				if(reqUrl.indexOf(url) != -1){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * ��ʼ����������֤ʧ��·��
	 */
	public void init(FilterConfig config) throws ServletException {
		super.loginUrl = config.getInitParameter("login"); 
		super.allowPassUrl.add(config.getInitParameter("loginAction"));
		super.allowPassUrl.add(config.getInitParameter("securityCode"));
		super.allowPassUrl.add(config.getInitParameter("checkSecurityCode"));
		super.allowPassUrl.add(config.getInitParameter("httpDownLoad"));
		super.allowPassUrl.add(config.getInitParameter("sendRedirectDownLoad"));
		super.allowPassUrl.add(config.getInitParameter("getStations"));
		super.allowPassUrl.add(loginUrl);
	}

	@Override
	public void doChain(FilterChain chain, HttpServletRequest req,
			HttpServletResponse res) throws IOException, ServletException {

		String principal = req.getParameter("edm_principal");
		// ����ǵ����¼
		if (principal != null) {
			TUser user=(TUser) req.getSession().getAttribute(ConstConfig.USER_KEY);
			userInfoSite(user,req, res);
			
			if(!res.isCommitted())
			chain.doFilter(req, res);
		} else {
			chain.doFilter(req, res);
		}
	}

	private void userInfoSite(TUser user, HttpServletRequest req,HttpServletResponse res)
			throws IOException, ServletException {
		// ����ҳ��
		String page = (String) req.getParameter("reqPage");
		Configurater con = Configurater.getInstance();
		
		//��������
		String referer = (String) req.getAttribute("url");
		// ����û���Ϊ��ʱ�����û�����session�У���д����ҳ��
		if (user != null) {
			
			// ���������page��ʶ�����ĸ�ҳ�棬���ض��򵽵�ǰҳ��
			if (page != null) {
				String pageUrl = req.getContextPath() + "/" +con.getConfigValue(page);
				res.sendRedirect(pageUrl);
			// ���������pageδ��ʶ���ĸ�ҳ�棬���ض�����ҳ
			} else if(req.getServletPath().endsWith("sso.jsp")) {
				String pageUrl = req.getContextPath()+"/" +con.getConfigValue("Romate_default");
				res.sendRedirect(pageUrl);
			}
			// �����ǰ�û�Ϊ�գ��򷵻ش�����ҳ��
		} else {
			res.sendRedirect(referer);
		}
	}
	
	public void destroy() {

	}
}
