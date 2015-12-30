package com.nsc.base.jsf.security;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import com.nsc.base.recource.ResourceLoader;
import com.nsc.base.util.AppException;

/**
 * Ȩ����֤��
 * 
 * ��������Ȩ����֤���ơ����������ļ��е����ã�����ĳ�û��Ƿ���Է��ʻ�����߽���һ��������
 * 
 * @author bs-team
 *
 * @date Oct 19, 2010 10:17:50 AM
 * @version
 */
public class Authentication {
	private static Authentication auth;
	private Properties views;
	private static final String views_file="views.xml";
	public static final String ISLOGIN="islogin";
	public static final String ALIAS="Authentication";
	
	/**
	 * ���캯��
	 * @return
	 */
	public static synchronized Authentication getInstance(){
		

			if(auth==null){
				auth=new Authentication();
				auth.loadRoleFile();
			}
		
		return auth;
	}
	
	/**
	 * ���ĳһ��ͼ��Ȩ��
	 * @param viewId
	 * @param servletContext
	 * @return
	 */
	public boolean authenticate(String viewId,ServletContext servletContext){
		
		
		if(isLoginRedirectRequired(viewId)){
			return false;
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param viewId ����
	 * @param action ����
	 * @return ��֤ͨ�����(true ͨ�� false δͨ��)
	 */
	public boolean authenticate(String viewId,String action){
		if(views==null || views.size()==0){
			loadRoleFile();
		}
		
		if(isRolePriorityRequired(viewId,action)){
			return false;
		}
		
		return true;
	}
	
	/**
	 * ���ĳһ��ͼ��Ȩ��
	 * @param viewId
	 * @return
	 */
	private boolean isLoginRedirectRequired(String viewId){
		String roleExpress;
		
		if(!views.containsKey(viewId)){
			return false;
		}else{
			roleExpress=views.getProperty(viewId);
			int index=roleExpress.indexOf(";")==-1?roleExpress.length():roleExpress.indexOf(";");
			roleExpress=roleExpress.substring(0,index);
			index=roleExpress.indexOf(":")==-1?roleExpress.length():roleExpress.indexOf(":");
			roleExpress=roleExpress.substring(index+1).trim();
			
			if("true".equalsIgnoreCase(roleExpress)){
				HttpSession session=(HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				if(session==null)
					return true;
				Boolean islogin=(Boolean)session.getAttribute(ISLOGIN);
				if(islogin==null || !islogin)
					return true;
			}
		}
		return false;
	}
	
	/**
	 * ���ĳһ��ͼ��ĳһ�¼���Ȩ��
	 * @param viewId
	 * @param action
	 * @return
	 */
	private boolean isRolePriorityRequired(String viewId,String action){
		String roleExpress;
		
		if(!views.containsKey(viewId)){
			return false;
		}else if(action==null){
			return false;
		}else{
			StringBuffer express=new StringBuffer(action.substring(2, action.length()-1));
			String method=express.substring(express.lastIndexOf(".")+1);
			express.delete(express.length()-method.length()-1, express.length());
			String clazz=express.substring(express.lastIndexOf(".")==-1?0:express.lastIndexOf(".")+1);
			
			roleExpress=views.getProperty(viewId);
			if(roleExpress.indexOf(clazz+"."+method)!=-1){
				int begin=roleExpress.indexOf(clazz+"."+method);
				begin=roleExpress.indexOf(":", begin);
				int end=roleExpress.indexOf(";", begin);
				
				roleExpress= roleExpress.substring(begin+1, end);
				
				if(roleExpress==null)
					return false;
				
				FacesContext fctx = FacesContext.getCurrentInstance(); 
				ELContext elctx = fctx.getELContext(); 
				Application jsfApp = fctx.getApplication(); 
				ExpressionFactory exprFactory = jsfApp.getExpressionFactory(); 
				
				ValueExpression valueExpression=exprFactory.createValueExpression(elctx,roleExpress, Boolean.class);
				Boolean validate=(Boolean)valueExpression.getValue(elctx);
				
				return !validate;
			}else{
				return false;
			}
		}
	}
	
	/**
	 * ���ĳһ��ͼ��Ȩ��
	 */
	private void loadRoleFile(){
		views=new Properties();
		try{
			//logger.info("Loading the view definitions from file "+views_file);
			views.loadFromXML(ResourceLoader.getResourceAsStream(views_file,
					((ServletContext)FacesContext.getCurrentInstance()
												.getExternalContext()
												.getContext())));
			//logger.info("Configuration loading has bean done!");
		}catch(IOException e){
			throw new AppException(e,"sys.auth.views.load",null,new String[]{views_file});
		}
	}
}
