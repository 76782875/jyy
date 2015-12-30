package com.nsc.dem.service.archives;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.BadLocationException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;

import com.nsc.base.index.FileField;
import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.bean.archives.TDoc;
import com.nsc.dem.bean.archives.TDocType;
import com.nsc.dem.bean.archives.TSynDoc;
import com.nsc.dem.bean.archives.TSynProject;
import com.nsc.dem.bean.profile.TRole;
import com.nsc.dem.bean.project.TComponent;
import com.nsc.dem.bean.project.TComponentDoc;
import com.nsc.dem.bean.project.TDocProject;
import com.nsc.dem.bean.project.TPreDesgin;
import com.nsc.dem.bean.project.TProject;
import com.nsc.dem.bean.project.TRecordDrawing;
import com.nsc.dem.bean.project.TShopDoc;
import com.nsc.dem.bean.project.TShopDrawing;
import com.nsc.dem.bean.project.TTender;
import com.nsc.dem.bean.system.TUnit;
import com.nsc.dem.service.base.IService;

public interface IarchivesService extends IService {

	/**
	 * ��ѯ����������Ϣ�б�
	 * 
	 * @param ����ʵ����
	 * @return
	 */
	public List<Object> archivesInfoList();

	/**
	 * �鿴������ϸ��Ϣ
	 * 
	 * @param ��������
	 * @return ����ʵ����
	 */
	public List<Object> archivesInfo();

	/**
	 * ¼�뵵�� ���汾��Ϣ��Ϊ1����ʼ��������Ϣ�����õ�����Ϣ�洢������ �Ե�����Ϣ���������洢����������Ԥ��ͼ��������Ԥ��ͼ
	 * 
	 * @param ����ʵ����
	 */

	public void insertArchives(TDoc tdoc, TDocProject tdocPro,
			TPreDesgin tpreDesgin, TProject tpro, TComponent tcom,
			TComponentDoc tcomp, TDocType docType, String login_id, String local)
			throws Exception;

	/**
	 * �洢������Ϣ �洢������Ϣ���ļ�������Ϣ����������������Ϣ
	 * 
	 * @param tdoc
	 *            �ļ���ʵ����
	 * @param tdocPro
	 *            �������̱�ʵ����
	 * @param tpreDesgin
	 *            ���赵����ʵ����
	 * @param tpro
	 *            ���̱�ʵ����
	 * @param tcom
	 *            ������ʵ����
	 * @param tcomp
	 *            ��������������ʵ����
	 */
	public void addArchives(TDoc tdoc, TDocProject tdocPro,
			TPreDesgin tpreDesgin, TProject tpro, TComponent tcom,
			TComponentDoc tcomp, String login_id, String local)
			throws Exception;

	/**
	 * �������״̬ �����ļ�״̬
	 * 
	 * @param ����ʵ����
	 */

	public void updateArcState();

	/**
	 * ���µ��� ���õ�����Ϣ�洢�����洢�ļ���Ϣ�����ϴ��ļ�������ļ��汾�� �����ļ�ftp�洢����������Ԥ��ͼ��������Ԥ��ͼ
	 * 
	 * @param tdoc
	 *            �ļ���ʵ����
	 * @param code
	 *            �ĵ����������
	 * @param proCode
	 *            ���̱�����
	 * @param local
	 *            �ϴ��ļ���WEB·��
	 * @param preD
	 *            �����ʵ����
	 * @param shopDraw
	 *            ʩ��ͼ������
	 * @param shopDoc
	 *            ʩ��������
	 * @param recordDraw
	 *            ����ͼ������
	 * @param tdocPro
	 *            �������̱�
	 * @param tcom
	 *            ������
	 * @param tcomp
	 *            ��������������
	 * @param login_id
	 *            ��¼ID
	 * @throws Exception
	 */
	public void updateArchives(TDoc tdoc, String code, String proCode,
			String location, TPreDesgin preD, TShopDrawing shopDraw,
			TShopDoc shopDoc, TRecordDrawing recordDraw, TDocProject tdocPro,
			TComponent tcom, TComponentDoc tcomp, TTender tender,
			String login_id, String storeLaction, String isLocal) throws Exception;

	/**
	 * �����ļ��鿴�汾�б� �ṩ��ǰ���ļ��İ汾�ݱ���ʷ�������汾�ţ��ϴ��ˣ��ϴ�ʱ�䣬���
	 * 
	 * @param �ļ�����
	 * @return
	 */

	public List<Object> fileVersionList();

	/**
	 * Ǩ���ļ� ת���ļ�����λ�ã��������ļ�����Ŀ¼Ϊ�·���λ��Ŀ¼
	 * 
	 * @param �ļ�����
	 *            ���ļ�����λ��
	 * @return
	 */

	public void delMoveFile(String storeLocation, String isLocal, String fileCode[], String code) throws Exception;

	/**
	 * ����ɾ���ļ� �ļ�������ɾ�������ļ���Ϣ�����ݿ���ɾ������ɾ�����ϴ�ʵ�塣
	 * 
	 * @param �ļ���������
	 * @return
	 */

	public void delFile(String fileCode[]);

	/**
	 * ɾ��������Ϣ ɾ����������Ϣ���ļ���Ϣ������������Ϣ����������������Ϣ��ftp�ļ�����
	 * 
	 * @param �ļ���������
	 * @return
	 * @throws Exception
	 */

	public void delArchives(String fileCode[]) throws Exception;

	/**
	 * �ĵ�������״�б� T_Doc_Type �ܹ����ݹ������ͣ����̽׶ν��й���
	 * 
	 * @param projectType
	 *            ��������
	 * @param projectMoment
	 *            ���̽׶�
	 * @return
	 */

	public List<Object> tdocTypeList(String projectType, String projectMoment);

	/**
	 * �����ļ����������ļ�·��
	 * 
	 * @param �ļ�����λ��
	 * @return
	 */
	public List<Object> filePathProduce(String fileType);

	/**
	 * �����ļ�Ԥ��ͼ ���ݲ�ͬ���ļ���ʽ����Ԥ��ͼ
	 * 
	 * @param �ļ��ļ���ʽ
	 * @return
	 */

	public OutputStream filePreview(String fileFormat);

	/**
	 * ��ȡԤ��ͼ �����ļ������ȡԤ��ͼ�ļ���ַ
	 * 
	 * @param �ļ�����
	 * @return
	 */

	public String filePreviewPath(String fileCode);

	/**
	 * ���������ļ�
	 * 
	 * @param files
	 * @param folder
	 *            �����ļ��У��򽫴��������������ָ���ļ����У�����ͳһ��ŵ�Ĭ��·����
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws URISyntaxException
	 * @throws BadLocationException
	 * @throws InterruptedException 
	 */
	public Set<File> addArchiveIndex(Map<File, Map<Enum<?>, FileField>> files,
			String storeLocation, String isLocal) throws IOException, ClassNotFoundException,
			InstantiationException, IllegalAccessException, URISyntaxException,
			BadLocationException, InterruptedException;

	/**
	 * ��ȡ�ĵ�������״�б�
	 */
	public List<TDocType> docTypeList(String docType);

	public List<TDocType> docTypeList(String id, String flag);

	/**
	 * ��ȡ�ĵ����������б�,�ڽ��ҳ
	 */
	public List<TDocType> docTypeResultList(String code, String parentCode);

	/**
	 * �����ļ����������ļ�·��
	 * 
	 * @param �ļ�����λ��
	 * @return
	 */
	// public String filePathProduce(String docType,Long id);
	public String filePathProduce(String docTypeName, TProject tpro);

	/**
	 * ����������ҳ��ѯ
	 * 
	 * @param map
	 * @param firstResult
	 *            ��ʼ��¼����
	 * @param maxResults
	 *            ������¼����
	 * @return
	 * @throws Exception
	 */
	public List<Object[]> queryTdocTypeList(Object[] obj, int firstResult,
			int maxResults, TableBean table) throws Exception;

	// ��ѯ��ɫ��Ϣ
	public List<TRole> TRoleList();

	/**
	 * ȫ�Ľ�������
	 * 
	 * @param tdoc
	 * @param tpro
	 * @param local
	 * @param tunit
	 *            ҵ����λ
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public Map<Enum<?>, FileField> setArchivesIndex(TDoc tdoc, TProject tpro,
			String local, TUnit tunit) throws IllegalAccessException,
			InstantiationException, InvocationTargetException,
			NoSuchMethodException;

	/**
	 * 
	 * @param queryString
	 * @param clazz
	 * @return
	 */
	public Date getDocLastUpdate(String loginId, String logType);

	public List<Object[]> creatIndexingTDoc(Object[] obj);

	public void deleteArchiveIndex(String storeLocation, String isLocation, String docid)
			throws URISyntaxException, CorruptIndexException,
			LockObtainFailedException, ClassNotFoundException,
			InstantiationException, IllegalAccessException, IOException,
			InvocationTargetException, NoSuchMethodException;

	public List<Object> batchQueryTDoc(String flag);

	// ��Ҫ��������ͼ���ĵ�
	public List<Object[]> creatThumbnailsTDoc(Object[] obj, String flag);

	/**
	 * ���ݷ�������ѯdoc
	 * 
	 * @param docTypeCode
	 *            �������
	 * @return
	 */
	public List<TDoc> getDocByDocTypeCode(String docTypeCode);

	/**
	 * ɾ������
	 * 
	 * @param codeList
	 * @return
	 */
	public String removeDocType(String[] codeList);

	/**
	 * ��ѯ�ĵ�
	 * 
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List queryDoc(Map<String, Object> map);

	/**
	 * ��ѯ�ѹ鵵�ĵ�
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List queryDocIsArchives(Map<String,Object> map);
	
	/**
	 * �����ĵ�ID���ҹ���
	 * 
	 * @param docId
	 * @return
	 */
	public TProject getTProjectBydocId(String docId);

	/**
	 * ��������״̬
	 * 
	 * @param folder
	 * @param docids
	 */
	public void updateIndex(String isLocal, String docid, String storeLocation)
			throws Exception;

	public void updateIndex(String isLocal, TDoc tdoc, TProject tpro,
			String storeLocation, boolean isIndex) throws Exception;

	public void tranArchives(TDocType docType, TProject tpro, TDoc tdoc,
			String local) throws Exception;
	
	/*
	 * ������߸���ͬ���ĵ�
	 */
	public void saveOrUpdateSynDoc(TSynDoc doc) throws Exception ;
	
	public void saveOrUpdateSynProject(TSynProject project);
	
	/**
	 * ��ID��ѯ�ĵ�
	 * @param ids
	 * @return
	 */
	public List<TDoc> queryDocByIds(String[] ids);

	/**
	 * ɾ���ļ�
	 * @param fid
	 */
	public void deleteSynDoc(String docId);

	public void removeIndex(String isLocal, String oldPath, String newPath,
			TDoc doc, TProject pro, String storeLocation, boolean isIndex) throws Exception;

	public void archivesIndex(String unit, String tempDir, TDoc doc,
			TProject pro,  String storeLocation,
			String isLocal, boolean isIndex) throws Exception;
	
	/**
	 * ��û�в��������Ĺ��̲�ѯ�ĵ�
	 * @return
	 */
	public List<TDoc> getDocByNoIndexProject(Object[] projectIds);
}
