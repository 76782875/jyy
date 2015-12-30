package com.nsc.dem.util.listener.impl;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import com.nsc.base.conf.Configurater;
import com.nsc.base.conf.ConstConfig;
import com.nsc.dem.bean.profile.TRole;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.bean.system.TOperateLog;
import com.nsc.dem.bean.system.TUnit;
import com.nsc.dem.service.base.IService;
import com.nsc.dem.util.listener.IListenerSet;
import com.nsc.dem.util.xml.FtpXmlUtils;
import com.nsc.dem.util.xml.IntenterXmlUtils;
import com.nsc.dem.webservice.client.WSClient;
import com.nsc.dem.webservice.util.ApplicationContext;

public class MainListenerSetImpl implements IListenerSet {

	public void addAttribute(HttpSessionBindingEvent even,
			ServletContext application) throws IOException {
		HttpSession session = even.getSession();
		IService service = (IService) ApplicationContext.getInstance()
		.getApplictionContext().getBean("baseService");
		if (even.getName().equalsIgnoreCase(ConstConfig.USER_KEY)) {

			TUser user = (TUser) session.getAttribute(ConstConfig.USER_KEY);
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy��MM��dd��HHʱmm��ss��");


			TOperateLog log = new TOperateLog();
			log.setOperateTime(new Timestamp(System.currentTimeMillis()));
			log.setTarget(user.getClass().getSimpleName());
			log.setTUser(user);
			log.setType("L21");
			log.setContent("�û�:" + user.getName() + ","
					+ format.format(new Date()) + "��¼ϵͳ");
			service.insertEntity(log);
		} else if ("edm_principal".equals(even.getName())) {
			edmPrincipal(even.getName(), session, service);
		} 

	}

	/**
	 * EDM�����¼����
	 * 
	 * @param attributeName
	 * @param session
	 * @throws IOException
	 */
	private void edmPrincipal(String attributeName, HttpSession session,
			IService service) throws IOException {

		HttpServletRequest req = (HttpServletRequest) session
				.getAttribute("edm_principal");
		// HttpServletResponse res = (HttpServletResponse)
		// req.getAttribute("res");
		// �����ʶ��
		String principal = (String) req.getParameter("edm_principal");
		// ����Ƿ�ӺϷ���ַ����
		String referer = req.getHeader("Referer");
		req.setAttribute("url", referer);
		// ͨ��websrvice���û����кϷ��Լ�飬ͨ�������������ҳ���棬����ת��500������

		// �ֹ�¼���ַ
		if (referer == null) {
			// res.sendError(403);
			return;
		} else {

			URL url = new URL(referer);
			String unitCode = FtpXmlUtils.getUnitCodeByHostName(url.getHost());
			String wsUrl = IntenterXmlUtils.getWSURL(unitCode);
			TUser user = (TUser) session.getServletContext().getAttribute(principal+"_"+unitCode);
			TUser suser = (TUser)session.getAttribute(ConstConfig.USER_KEY);
			if (user == null || suser == null ) {
				// ��Զ��ȡ���û�
				try {
					user =this.getTUserBySessionId(wsUrl, principal, service);
					suser=(TUser) service.EntityQuery(TUser.class, user.getLoginId());
					if (suser!=null) {
						user=suser;
						Timestamp now = new Timestamp(System
								.currentTimeMillis());
						long count = user.getLoginCount() + 1;
						user.setLoginCount(count);
						user.setLastLoginTime(now);
						service.updateEntity(user);
					} else {
						TUnit unit = user.getTUnit();
						TUnit isUnit = (TUnit) service.EntityQuery(TUnit.class,
								unit.getCode());
						if (isUnit == null) {
							service.insertEntity(unit);
						}
						user.setIsValid(true);
						user.setLoginCount(1L);
						service.insertEntity(user);
					}
					req.getSession().setAttribute(ConstConfig.USER_KEY, user);
					session.getServletContext().setAttribute(user.getLoginId(), user);
				} catch (MalformedURLException e) {
					Logger.getLogger(MainListenerSetImpl.class).warn(e.getMessage());
				} catch (Exception e) {
					Logger.getLogger(MainListenerSetImpl.class).warn(e.getMessage());
				}
			}
		}
	}
	
	/**
	 * ���ݴ�����sessionId��ѯ�û���Ϣ
	 * 
	 * @param sessionId
	 *            sessionId
	 * @return �û���Ϣ
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public TUser getTUserBySessionId(String wsUrl,String param, IService service)
			throws Exception {
		String pwd = Configurater.getInstance().getConfigValue("wspwd");
		byte[] result =WSClient.getClient(wsUrl).getService().getUserInfo(param, pwd);
		TUser user = new TUser();
		String str = new String(result);
		InputSource is = new InputSource(new StringReader(str));
		Document document = (new SAXBuilder()).build(is);
		Element root = document.getRootElement();
		List<Element> pros = root.getChildren();
		TUnit unit = new TUnit();

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String loginId = "";
		for (Element p : pros) {
			String pName = p.getName();
			String content = p.getTextTrim();
			if ("unitCode".equals(pName)) {
				unit.setCode(content);
			} else if ("unitName".equals(pName)) {
				unit.setName(content);
			} else if ("unitShortName".equals(pName)) {
				unit.setShortName(content);
			} else if ("unitAddress".equals(pName)) {
				unit.setAddress(content);
			} else if ("unitTelephoe".equals(pName)) {
				unit.setTelephone(content);
			} else if("unitIsUsable".equals(pName)){
				if (content.equals("1")) {
					unit.setIsUsable(true);
				} else {
					unit.setIsUsable(false);
				}
			}else if ("unitType".equals(pName)) {
				unit.setType(content);
			} else if ("unitProxyCode".equals(pName)) {
				unit.setProxyCode(content);
			}
		}
		for (Element p : pros) {
			String pName = p.getName();
			String content = p.getTextTrim();
			 if ("loginName".equals(pName)) {
				user.setName(content);
			} else if ("loginId".equals(pName)) {
				loginId = content + "_"+unit.getCode();
				user.setLoginId(loginId);
			}  else if ("creator".equals(pName)) {
				user.setCreator(content);
			} else if ("createDate".equals(pName)) {
				Timestamp timestamp = new Timestamp(format.parse(content)
						.getTime());
				user.setCreateDate(timestamp);
			} else if ("duty".equals(pName)) {
				user.setDuty(content);
			} else if ("telePhone".equals(pName)) {
				user.setTelephone(content);
			} else if ("password".equals(pName)) {
				user.setPassword(Long.valueOf(content));
			} 
		}
		
		Configurater con = Configurater.getInstance();
		String romote_roleId = Configurater.getInstance().getConfigValue("romote_roleId");
		TRole role = (TRole) service.EntityQuery(TRole.class, romote_roleId);
		
		TUser interfaceuser = (TUser)service.EntityQuery(TUser.class,"interface"); 
		unit.setCreateDate(new Date());
		unit.setTUser(interfaceuser);
		
		user.setTRole(role);
		user.setTUnit(unit);

		// Ҫ��������
		//user.setRomoteLogo(romoteLogo);
		user.setIsLocal("R");

		return user;
	}
	
	public void changeAttribute(HttpSessionBindingEvent even,
			ServletContext application) throws IOException {
		String attributeName = even.getName();
		HttpSession session = even.getSession();
		IService service = (IService) ApplicationContext.getInstance()
				.getApplictionContext().getBean("baseService");

		Logger.getLogger(ModuleListenerSetImpl.class).info(
				"��ֵ: " + session.getAttribute(attributeName));

		if ("edm_principal".equals(attributeName)) {
			HttpServletRequest req = (HttpServletRequest) session
					.getAttribute("edm_principal");
			String principal = (String) req.getParameter("edm_principal");
			String tempName = principal + "_R";
			TUser currentU = (TUser) session.getAttribute(ConstConfig.USER_KEY);
			if (currentU == null || !currentU.getLoginId().equals(tempName)) {
				edmPrincipal(attributeName, session, service);
			}
		}
	}

}
