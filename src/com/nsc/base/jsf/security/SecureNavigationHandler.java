package com.nsc.base.jsf.security;

import javax.faces.application.NavigationHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 * ����������
 * 
 * ������ҳ��ת��ʱ�����������еĿ��Ʒ�����������Ϊҳ����ʿ�����ʹ�ã���������Ȩ�޿��ơ�
 * Ϊʹ�ø��๦�ܣ���Ҫ�������ļ��ж��������ΪAction��������
 * 
 * @author bs-team
 *
 * @date Oct 19, 2010 10:21:25 AM
 * @version
 */
public class SecureNavigationHandler extends NavigationHandler{
	private NavigationHandler handler;
	
	/**
	 * ���캯��
	 * @param handler
	 */
	public SecureNavigationHandler(NavigationHandler handler) {
		super();
		this.handler = handler;
	}
	
	/**
	 * ����ҳ����ת
	 * 
	 * �������תҳ�治������ʣ�����ת������ҳ��
	 */
	public void handleNavigation(FacesContext fc, String actionMethod, String actionView) {
		
		if(cancelActionByAuthen(getViewId(fc),actionMethod)){
			
		}else{
			handler.handleNavigation(fc, actionMethod, actionView);
		}
	}
	
	/**
	 * ����Ƿ�����Ȩ
	 * @param viewId
	 * @param action
	 * @return �Ƿ�����Ȩ
	 */
	private boolean cancelActionByAuthen(String viewId,String action){
		return Authentication.getInstance().authenticate(viewId, action);
	}
	
	/**
	 * ȡ��ҳ��ID
	 * @param facesContext
	 * @return ҳ��ID
	 */
	private String getViewId(FacesContext facesContext){
		if (facesContext!=null){
			UIViewRoot viewRoot = facesContext.getViewRoot();
			if (viewRoot!=null)
				return viewRoot.getViewId();
			}
		return null;
	}
}
