package com.nsc.base.jsf.util;

import java.util.Iterator;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * ��Ϣ����
 * 
 * ��������Ajax�¼������У���ҳ���ȡ��������Ϣ��
 * 
 * @author bs-team
 *
 * @date Oct 19, 2010 10:26:39 AM
 * @version
 */
public class MessageUtils {
	
	public boolean isError(){
		Iterator<FacesMessage> messages=FacesContext.getCurrentInstance().getMessages();
		while(messages.hasNext()){
			FacesMessage facesMessage=messages.next();
			if(FacesMessage.SEVERITY_ERROR.equals(facesMessage.getSeverity())){
				return true;
			}
		}
		return false;
	}
	
	public boolean isGlobalError(){
		Iterator<FacesMessage> messages=FacesContext.getCurrentInstance().getMessages(null);
		while(messages.hasNext()){
			FacesMessage facesMessage=messages.next();
			if(FacesMessage.SEVERITY_ERROR.equals(facesMessage.getSeverity())){
				return true;
			}
		}
		return false;
	}
	
	public String getErrorMessage(){
		Iterator<FacesMessage> messages=FacesContext.getCurrentInstance().getMessages(null);
		while(messages.hasNext()){
			FacesMessage facesMessage=messages.next();
			if (facesMessage.getSeverity().equals(FacesMessage.SEVERITY_ERROR)){
				return facesMessage.getSummary();
			}
		}
		return null;
	}
	
	public boolean isWarn(){
		Iterator<FacesMessage> messages=FacesContext.getCurrentInstance().getMessages();
		while(messages.hasNext()){
			FacesMessage facesMessage=messages.next();
			if(FacesMessage.SEVERITY_WARN.equals(facesMessage.getSeverity())){
				return true;
			}
		}
		return false;
	}
	
	public boolean isGlobalWarn(){
		Iterator<FacesMessage> messages=FacesContext.getCurrentInstance().getMessages(null);
		while(messages.hasNext()){
			FacesMessage facesMessage=messages.next();
			if(FacesMessage.SEVERITY_WARN.equals(facesMessage.getSeverity())){
				return true;
			}
		}
		return false;
	}
	
	public String getWarnMessage(){
		Iterator<FacesMessage> messages=FacesContext.getCurrentInstance().getMessages(null);
		while(messages.hasNext()){
			FacesMessage facesMessage=messages.next();
			if (facesMessage.getSeverity().equals(FacesMessage.SEVERITY_WARN)){
				return facesMessage.getSummary();
			}
		}
		return null;
	}
	
	/**
	 * ����ҳ����ʾ��Ϣ
	 * @param msg  ��ʾ��Ϣ����
	 */
	public static void setMessge(String msg){
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,msg,""));			
	}
	
}
