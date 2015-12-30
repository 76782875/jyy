package com.nsc.base.jsf.security;

import java.io.IOException;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.ServletContext;
import javax.faces.application.FacesMessage;

import com.nsc.base.util.AppException;

import static javax.faces.event.PhaseId.APPLY_REQUEST_VALUES;
import static javax.faces.event.PhaseId.RENDER_RESPONSE;
import static javax.faces.event.PhaseId.ANY_PHASE;

/**
 * URL�׶μ�����
 * 
 * �ڻ������ɵĸ����׶ν��п��ƣ�������ΪȨ�޿��ƽڵ㱻ʹ�á�
 * Ϊʹ�ø��๦�ܣ���Ҫ�������ļ��ж��������ΪUrlPhase��������
 * 
 * @author bs-team
 *
 * @date Oct 19, 2010 10:23:23 AM
 * @version
 */
public class UrlPhaseListener implements PhaseListener{
	private static final long serialVersionUID = -9127555729455066493L;
	
	public UrlPhaseListener(){
	}
	
	public void beforePhase(PhaseEvent event){
		
		if ( event.getPhaseId() == APPLY_REQUEST_VALUES){			
		}
		
		if ( event.getPhaseId() == RENDER_RESPONSE ){
			String viewId=getViewId(event.getFacesContext());
			
			if(!Authentication.getInstance().authenticate(viewId,
					(ServletContext)event.getFacesContext().getExternalContext().getContext())){
				
				event.getFacesContext().addMessage(null, new FacesMessage("�Ự�ѹ��ڣ������µ�¼��"));
				redirect(event.getFacesContext(),"/login.xhtml");
			}
		}
	}
	
	/**
	 * �õ�����ID
	 * @param facesContext
	 * @return
	 */
	private String getViewId(FacesContext facesContext){
		if (facesContext!=null){
			UIViewRoot viewRoot = facesContext.getViewRoot();
			if (viewRoot!=null)
				return viewRoot.getViewId();
			}
		return null;
	}
	
	/**
	 * ��ת����
	 * @param context
	 * @param viewId
	 */
	private void redirect(FacesContext context,String viewId){
		String url = context.getApplication().getViewHandler().getActionURL(context, viewId);
	    ExternalContext externalContext = context.getExternalContext();
	    try
	      {
	        externalContext.redirect( externalContext.encodeActionURL(url));
	      }
	      catch (IOException ioe)
	      {
	         throw new AppException(ioe,"sys.redirect.io",null,new String[]{viewId});
	      }
	      context.responseComplete();
	}
	
	public void afterPhase(PhaseEvent event){
	}
	
	public PhaseId getPhaseId(){
		return ANY_PHASE;
	}
}
