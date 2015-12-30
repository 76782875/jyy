package com.nsc.base.jsf.util;

import javax.el.ELContext;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 * ���湤��
 * 
 * ���������ں�̨�����ж�ҳ��Ԫ�ؽ��п���ʱʹ�á�
 * 
 * @author bs-team
 *
 * @date Oct 19, 2010 10:25:04 AM
 * @version
 */
public class FacesUtils {
	
	/**
	 * ��UIComponent��Ѱ��idΪuid����UIComponent
	 * @param uiComponent
	 * @param uId
	 * @return idΪuid��UIComponent���������null
	 */
	public static UIComponent getUIComponent(UIComponent uiComponent,String uId){
		for(UIComponent ui : uiComponent.getChildren()){
			if(ui.getId().equals(uId))
				return ui;
			else{
				UIComponent tui= getUIComponent(ui,uId);
				if (null!=tui) return tui;
			}
		}
		return null;
	}
	
	/**
	 * �����Ƶõ���FacesContext�����java����
	 * @param facesContext
	 * @param beanAlias
	 * @return java����
	 */
	public static Object getManagedBean(FacesContext facesContext,String beanAlias){
		ELContext elContext = facesContext.getELContext();
		return FacesContext.getCurrentInstance()
							.getApplication()
							.getELResolver()
							.getValue(elContext, null, beanAlias);
	}
	
	public static String getBasePath(){
		String filePath;
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
		filePath = session.getServletContext().getRealPath("/").replace("/", "\\");
		return filePath;
	}
}
