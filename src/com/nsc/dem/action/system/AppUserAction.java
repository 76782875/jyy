package com.nsc.dem.action.system;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import sun.org.mozilla.javascript.internal.ContextAction;

import com.nsc.base.conf.Configurater;
import com.nsc.base.conf.ConstConfig;
import com.nsc.base.util.CheckCode;
import com.nsc.dem.action.BaseAction;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.bean.system.TUnit;
import com.nsc.dem.service.system.IuserService;
import com.nsc.dem.util.xml.IntenterXmlUtils;

public class AppUserAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	private IuserService userService;

	private String loginId;
	private String password;
	private String captcha;
	private List stations;

	public List getStations() {
		return stations;
	}

	public void setStations(List stations) {
		this.stations = stations;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public IuserService getUserService() {
		return userService;
	}

	public void setUserService(IuserService userService) {
		this.userService = userService;
	}

	private String loginType;

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	/**
	 * �û���¼
	 * 
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public String login() throws Exception {
		if (getRequest() != null) {
			getRequest().removeAttribute("message");
		}
		TUser user = (TUser) userService.EntityQuery(TUser.class, loginId);
		// ���ڸ��û�
		if (user != null) {
			String validateC = (String) getRequest().getSession().getAttribute(
					"checkCode");
			if (user.getIsLocal().equals("R")) {
				getRequest().setAttribute("message", "���û�������");
				return "logout";
			} else if (validateC == null || captcha == null) {
				getRequest().setAttribute("message", "��֤������");
				return "logout";
			} else if (!validateC.equals(captcha.toUpperCase())) {
				getRequest().setAttribute("message", "��֤������");
				return "logout";
			} else if (!password.equals(user.getPassword().toString())) {
				getRequest().setAttribute("message", "�û�������������");
				return "logout";
			} else if (user.getIsValid() == false) {
				getRequest().setAttribute("message", "�˺ű�����");
				return "logout";
			} else {
				// ���µ�¼��Ϣ
				if (user.getLoginTime() != null) {
					user.setLastLoginTime(user.getLoginTime()); // �ϴε�¼ʱ��
				} else {
					user.setLastLoginTime(null); // �ϴε�¼ʱ��
				}
				Timestamp timestamp = new Timestamp(new Date().getTime());
				user.setLoginTime(timestamp); // ��¼ʱ��
				user.setLoginCount((user.getLoginCount() + 1)); // ��¼����
				userService.updateEntity(user);
				// ϵͳ����Ա
				TUnit unit = (TUnit) userService.EntityQuery(TUnit.class, user
						.getTUnit().getCode());
				TUnit proxyUnit = (TUnit) userService.EntityQuery(TUnit.class,
						unit.getProxyCode());
				user.setTUnit(proxyUnit);
				if (this.getSession(ConstConfig.USER_KEY) != null) {
					this.getSession().removeAttribute(ConstConfig.USER_KEY);
				}
				this.setSession(ConstConfig.USER_KEY, user);
				// ����ս����µ�¼ʧЧ
				this.getSession().removeAttribute("portal_principal");

				//��ϵ����¼��webservice�����û�IDȡsession��
				getSession().getServletContext().setAttribute(
						user.getLoginId(), this.getSession());

				String unitCode = super.getLoginUser().getTUnit().getProxyCode();
				String localhost = IntenterXmlUtils.getAppServerAdd(unitCode);
				if (loginType.equals(localhost)) {
					return "display";
				} else if(loginType.startsWith("http://")){
					//main/sso.jsp?edm_principal=
					String url = loginType +"/main/sso.jsp?edm_principal="+loginId+"&unitCode="+super.getLoginUser().getTUnit().getProxyCode();
					super.getRequest().setAttribute("url", url);
					return "romotedisplay";
				}else {//�����ѡ��Ĭ��Ϊ��¼��ϵͳ
					return "display";
				}
			}
		}
		getRequest().setAttribute("message", "�û�������������");
		return "logout";
	}

	// �����¼
	public String ssoLogin() {
		return "display";
	}

	/**
	 * ��ȫ�˳�
	 * 
	 * @throws IOException
	 */
	public String logout() throws IOException {
		TUser user = (TUser) super.getSession().getAttribute(
				ConstConfig.USER_KEY);
		if (user == null) {
			getRequest().getSession().removeAttribute(ConstConfig.USER_KEY);
			return "logout";
		} else if (user.getRomoteLogo() != null) {
			String unitCode = super.getLoginUser().getTUnit().getProxyCode();
			String appIp = IntenterXmlUtils.getAppServerAdd(unitCode);
			String loginId = user.getLoginId().split("_")[0];
			String urlT = appIp+"system/logoutAppUser.action"+loginId+"&reqPage=esc";
			super.getSession().getServletContext().setAttribute(loginId,
					super.getSession());
			String tempUrl = "<script>window.location.href='" + urlT
					+ "'</script>";
			this.getResponse().getWriter().write(tempUrl);
			return null;
		} else {
			getRequest().getSession().removeAttribute(ConstConfig.USER_KEY);
			getRequest().getSession().getServletContext().removeAttribute(user.getLoginId());
			return "logout";
		}
	}

	/**
	 * ������֤��
	 */
	public void checkCode() {

		CheckCode cc = new CheckCode();

		HttpServletResponse response = this.getResponse();
		response.setContentType("image/jpeg");
		ServletOutputStream sos = null;
		try {
			sos = response.getOutputStream();
		} catch (IOException e1) {
			Logger.getLogger(AppUserAction.class).warn("������֤��ʱ�õ�������쳣:", e1);
		}

		// ����������������ͼƬ
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		// �����ڴ�ͼ�񲢻����ͼ��������
		BufferedImage image = new BufferedImage(60, 20,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();

		// �����������֤��
		char[] rands = cc.generateCheckCode();

		// ����ͼ��
		cc.drawBackground(g);
		cc.drawRands(g, rands);

		// ����ͼ��Ļ��ƹ���,���ͼ��
		g.dispose();

		// ��ͼ��������ͻ���
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "JPEG", bos);
			byte[] buf = bos.toByteArray();
			response.setContentLength(buf.length);
			sos.write(buf);
			bos.close();
			sos.close();
		} catch (IOException e) {
			Logger.getLogger(AppUserAction.class).warn("������֤��ʱ����쳣:", e);
		}

		// ����ǰ��֤����뵽session��
		String rand = new String(rands);
		rand = rand.toUpperCase();
		this.getSession().setAttribute("checkCode", rand);
		Logger.getLogger(AppUserAction.class).info(
				"��֤�룺 " + this.getSession().getAttribute("checkCode"));
	}

	/**
	 * �ж����ɵ���֤���Ƿ���ȷ
	 */
	public void isRightCode() {
		HttpServletResponse response = this.getResponse();
		HttpServletRequest request = this.getRequest();

		response.setContentType("text/html;charset=utf-8");
		String validateC = (String) request.getSession().getAttribute(
				"checkCode");
		// String veryCode = request.getParameter("c");
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			Logger.getLogger(AppUserAction.class).warn("��ȡWriter�����쳣:", e);
		}
		out.println(validateC);
		// if(veryCode==null||"".equals(veryCode)){
		// out.println("��֤��Ϊ��");
		// }else{
		// if(validateC.equals(veryCode)){
		// out.println("��֤����ȷ");
		// }else{
		// out.println("��֤�����");
		// }
		// }
		out.flush();
		out.close();
	}
	
	/**
	 * �õ�վ������
	 * @return վ�����֣�վ�㲿����Ŀ��
	 */
	public String getStation(){
		stations = IntenterXmlUtils.getAllInfo();
		return SUCCESS;
	}

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

}
