package com.nsc.dem.service.system;

import java.util.List;
import java.util.Map;

import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.action.system.TMenu;
import com.nsc.dem.bean.profile.TProfile;
import com.nsc.dem.bean.profile.TRoleProfile;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.bean.profile.TUserProfile;
import com.nsc.dem.bean.system.TUserShortcut;
import com.nsc.dem.service.base.IService;

public interface IprofileService extends IService {

	/**
	 * Ȩ�޹���--Ȩ�޶���--��ѯ�б� ��Ҫ���ݲ�ѯ������Ȩ�޶�����л�ȡȨ����Ϣ�б�
	 * 
	 * @param ����ΪȨ�޶����ʵ����
	 * @return
	 */
	public List<Object> profileInfoList();

	/**
	 * Ȩ�޹���--��ɫ��Ȩ--��ѯ�б� ��Ҫ���ݲ�ѯ�����ӽ�ɫȨ�ޱ��л�ȡȨ����Ϣ�б�
	 * 
	 * @param ����ΪȨ�޶����ʵ����
	 * @return
	 */
	public List<Object> roleProfileInfoList();

	/**
	 * Ȩ�޹���--�û���Ȩ--��ѯ�б� ��Ҫ���ݲ�ѯ�������û�Ȩ�ޱ��л�ȡȨ����Ϣ�б�
	 * 
	 * @param ����Ϊ�û�Ȩ�ޱ�ʵ����
	 * @return
	 */
	public List<Object> userProfileInfoList();

	/**
	 * ����û����е�Ȩ�޲˵�
	 */
	public List<TMenu> queryUserAllProList(String userId, String node,
			String menuType,String roleId,String isLocal);

	public List<TUserShortcut> queryShortcutByUser(String userId);

	/**
	 * ����Ȩ�����Ͳ�ѯ������Ȩ��Ϣ
	 */
	public List<TProfile> getTProfileByType(String type);

	/**
	 * Ȩ�޹�����Ϣ ��ҳ��ѯ
	 * 
	 * @param map
	 * @param firstResult
	 *            ��ʼ��¼����
	 * @param maxResults
	 *            ������¼����
	 * @param table
	 * @return
	 * @throws Exception
	 */
	public List<Object[]> queryTProfileInfoList(Map<String, Object> map,
			int firstResult, int maxResults, TableBean table) throws Exception;

	/**
	 * ɾ����Ȩ����Ȩ�������Ϣ
	 * 
	 * @param profileId
	 */
	public void deleteTProfileInfo(IprofileService profileService,
			String profileId);

	/**
	 * ��ɫ��Ȩ��Ϣ ��ҳ��ѯ
	 * 
	 * @param map
	 * @param firstResult
	 *            ��ʼ��¼����
	 * @param maxResults
	 *            ������¼����
	 * @param table
	 * @return
	 * @throws Exception
	 */
	public List<Object[]> queryTRoleProfileInfoList(Map<String, Object> map,
			int firstResult, int maxResults, TableBean table) throws Exception;

	/**
	 * ɾ����ɫ��Ȩ��Ϣ
	 * 
	 * @param roleId
	 * @param profileId
	 */
	public void deleteTRoleProfile(String roleId, String profileId);

	/**
	 * ɾ���û���Ȩ��Ϣ
	 * 
	 * @param userId
	 * @param profileId
	 */
	public void deleteTUserProfile(String userId, String profileId);

	/**
	 * ���ݽ�ɫID����ȨID��ѯ��ɫ����Ϣ
	 * 
	 * @param roleId
	 * @param profileId
	 * @return
	 */
	public List<TRoleProfile> getRoleProfileByRoleIdandProfileId(String roleId,
			String profileId);

	public List<TUserProfile> getUserProfileByRoleIdAndProfileId(String roleId,
			String profileId);

	/**
	 * ���ݽ�ɫID��Ȩ�޷����ѯ��Ȩ��Ϣ
	 * 
	 * @param roleId
	 * @param type
	 * @return
	 */
	public List<TProfile> getTProfileByroleIdandProfileId(String roleId,
			String type);

	/**
	 * �û���Ȩ��Ϣ ��ҳ��ѯ
	 * 
	 * @param map
	 * @param firstResult
	 *            ��ʼ��¼����
	 * @param maxResults
	 *            ������¼����
	 * @param table
	 * @return
	 * @throws Exception
	 */
	public List<Object[]> queryTUserProfileInfoList(Map<String, Object> map,
			int firstResult, int maxResults, TableBean table) throws Exception;

	/**
	 * ���ݽ�ɫID��ѯ�����û�
	 * 
	 * @param roleId
	 * @return
	 */
	public List<TUser> queryAllUserList(String roleId);

	/**
	 * �����û���ѯδ��Ȩ�Ķ���
	 * 
	 * @param userId
	 * @return
	 */
	public List<TProfile> getTProfileByUserId(String userId);

	/**
	 * �鿴Ȩ��
	 * 
	 * @param userId
	 *            �û�ID
	 * @param profileId
	 *            Ȩ��ID
	 * @param site
	 *            λ��
	 * @return boolean
	 */
	public boolean getProfileByauthControl(TUser user, String profileId,
			String siteName);
}
