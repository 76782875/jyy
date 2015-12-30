package com.nsc.dem.action.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nsc.base.conf.Configurater;
import com.nsc.dem.action.BaseAction;
import com.nsc.dem.bean.profile.TProfile;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.bean.system.TUserShortcut;
import com.nsc.dem.service.system.IprofileService;
import com.nsc.dem.util.log.Logger;

public class ShortcutMenuAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	private List<Map<String,Object>> list;
	private List<TUserShortcut> tusList;
    private String menuIds;
    private IprofileService profileService;
    
	public void setProfileService(IprofileService profileService) {
		this.profileService = profileService;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}
  
	/**
	 * ��ʾ�õ�¼�û���,׼����ӻ��߸��µĿ�ݲ˵�
	 * ListBox ���
	 * @return
	 */
	public String newMenu(){
		list=new ArrayList<Map<String,Object>>();
		String id=getRequest().getParameter("id");  
		boolean flag=false;
			long tid=Long.parseLong(id);
			//�õ����û����еĿ�ݲ˵�
			List<TUserShortcut> tus=profileService.queryShortcutByUser(super.getLoginUser().getLoginId());
			for(int i=0;i<tus.size();i++){
				TUserShortcut ts=(TUserShortcut)tus.get(i);
				long tPid=Long.parseLong(ts.getTProfile().getId());
				//�ж��Ƿ�ò˵����� ��ݲ˵����� 
				if(tPid==tid){
					flag=true;
				}
			}
			// ����� ���������ʾ
			if(!flag){
				TProfile tProfile=new TProfile();
				tProfile.setId(id);
				Object tpfList= profileService.EntityQuery(TProfile.class,id);
				TProfile tpf=(TProfile)tpfList;
				Map<String,Object> map= new HashMap<String,Object>();
				map.put("code", tpf.getId());
				map.put("name", tpf.getName());
				list.add(map);
			}
		return "list";
	}
	
	/**
	 * ��ʾ�õ�¼�û���  ���еĿ�ݲ˵�
	 * ListBox �ұ�
	 * @return
	 */
	public String initMenu(){
		list=new ArrayList<Map<String,Object>>();
		List<TUserShortcut> tus=profileService.queryShortcutByUser(super.getLoginUser().getLoginId());
		for(int i=0;i<tus.size();i++){
			TUserShortcut ts=(TUserShortcut)tus.get(i);
			Map<String,Object> map= new HashMap<String,Object>();
			map.put("code", ts.getTProfile().getId());
			map.put("name", ts.getTProfile().getName());
			list.add(map);
		}
		return "list";
	}
	
	/**
	 * ��ʼ��topҳ�洦, ��ݲ˵�����ʾ
	 * @return
	 */
    public String initTop(){
    	String homePage=Configurater.getInstance().getConfigValue("homePage");
    	getRequest().setAttribute("homePage", homePage);
    	TUser user=super.getLoginUser();
    	tusList=profileService.queryShortcutByUser(user.getLoginId());
    	return "top";
    }
    
    /**
     * ���¿�ݲ˵�ʱ  ���ô˷���
     * @return
     */
	public String  edit(){
		//menuIds ΪListBox �ұ� ���в˵���ID  ������,�ֿ�
		String[] str=menuIds.split(",");
		TUserShortcut tTUserShortcut=new TUserShortcut();
		tTUserShortcut.setTUser(getLoginUser());
		List<Object> pfsList=profileService.EntityQuery(tTUserShortcut);
		Logger logger=super.logger.getLogger(ShortcutMenuAction.class);
		//��ɾ�����п�ݲ˵�
		for(Object obj:pfsList){
			profileService.delEntity(obj);
		}
		//���ListBox�ұ�û���κ�����,�������
		if(!"".equals(menuIds)){
			for(int i=0;i<str.length;i++){
				TUserShortcut tus=new TUserShortcut();
				tus.setTUser(getLoginUser());
				tus.setShortOrder((long)(i+1));
				TProfile tProfile=new TProfile();
				tProfile.setId(str[i]);
				Object tpfList= profileService.EntityQuery(TProfile.class,str[i]);
				TProfile tpf=(TProfile)tpfList;
				tus.setTProfile(tpf);
				profileService.insertEntity(tus);
				logger.info("���ÿ�ݷ�ʽ�� "+str[i]);
			}
		}
		return "list";
	}
	
	/**
	 * ����ҳ�����óɿ�ݲ˵��Ƿ��ܵ�
	 * @return
	 */
	public String imgDisplay(){
		list=new ArrayList<Map<String,Object>>();
		String id=getRequest().getParameter("id");  
		boolean flag=false;
		long tid=Long.parseLong(id);
		//�õ����û����еĿ�ݲ˵�
		List<TUserShortcut> tus=profileService.queryShortcutByUser(super.getLoginUser().getLoginId());
		for(int i=0;i<tus.size();i++){
			TUserShortcut ts=(TUserShortcut)tus.get(i);
			long tPid=Long.parseLong(ts.getTProfile().getId());
			//�ж��Ƿ�ò˵����� ��ݲ˵����� 
			if(tPid==tid){
				flag=true;
			}
		}
		if(!flag){
			Map<String,Object> map= new HashMap<String,Object>();
			map.put("code", "success");
			list.add(map);
		}
		return "list";
	}
	
	public String getMenuIds() {
		return menuIds;
	}

	public void setMenuIds(String menuIds) {
		this.menuIds = menuIds;
	}

	public List<TUserShortcut> getTusList() {
		return tusList;
	}

	public void setTusList(List<TUserShortcut> tusList) {
		this.tusList = tusList;
	}
}
