package com.nsc.dem.util.index;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.nsc.base.conf.Configurater;

/**
 * IndexSearchManagerά����
 * 
 * ����ģʽ
 */
public class IndexSearchManager {

	private Map<File, IndexSearcher> indexSearches;
	private static IndexSearchManager indexSearchManager;
	private Boolean reloading = false;

	/**
	 * ��ȡ����
	 * 
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static IndexSearchManager getInstance() throws URISyntaxException,
			IOException {
		if (indexSearchManager == null) {
			indexSearchManager = new IndexSearchManager();
		}
		return indexSearchManager;
	}

	/**
	 * ���췽�� ��ʼ�����еļ����ļ�
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	private IndexSearchManager() throws URISyntaxException, IOException {
		String unitCode = Configurater.getInstance().getConfigValue("unitCode");
		indexSearches = new HashMap<File, IndexSearcher>();

		// ����word
		reloadSingleFile(FileDirUtils.getWordFile());

		// ��λID������ǹ��ҵ���ϵͳ�������λID
		if (StringUtils.isNotBlank(unitCode)) {
			String[] unitIds = unitCode.split(",");

			// ����ϵͳ���ж��ID
			for (String unitId : unitIds) {
				List<File> writeFiles = FileDirUtils.getAllLoadDirs("doc_write_Dir", unitId);
				// ͬ�����ص�������,�ɹ����ʼ��
				for (File file : writeFiles) {
					File readFile = FileDirUtils.getReadFileByWriteFile(file);
					if (initReadFolder(file, readFile)) {
						reloadSingleFile(readFile);
					}
				}
			}
		}
	}

	/**
	 * ���ص����ļ�
	 */
	public boolean reloadSingleFile(File file) {
		if (file == null)
			return false;
		if (file.list().length <= 0) {
			Logger.getLogger(IndexSearchManager.class).info(
					"������Ŀ¼Ϊ�գ����ܼ��أ�" + file.getPath());
			return false;
		}
		// ����Ѿ����أ����ٳ�ʼ��
		if (indexSearches.get(file) == null) {
			try {
				IndexSearcher searcher = new IndexSearcher(IndexReader
						.open(FSDirectory.open(file)));
				indexSearches.put(file, searcher);
				Logger.getLogger(IndexSearchManager.class).info(
						"���سɹ���" + file.getPath());
				return true;
			} catch (CorruptIndexException e) {
				Logger.getLogger(IndexSearchManager.class).warn(
						file.getPath() + e);
			} catch (IOException e) {
				Logger.getLogger(IndexSearchManager.class).warn(
						"�����ⲻ�ܴ�" + file.getPath() + e);
			}
		}
		return false;
	}

	/**
	 * ������ͬ��
	 */
	public boolean initReadFolder(File writeFolder, File readFolder)
			throws URISyntaxException, IOException {
		//����Ѿ����أ�����ͬ��
		if (indexSearches.get(readFolder) != null) {
			return false;
		}
		if (!readFolder.exists()) {
			readFolder.mkdirs();
		}
		//�����һ��ʹ��ʱwrite��Ϊ�գ�readΪ��ʱ������
		//write����һ���޸�ʱ�����read����һ��ʱ��ʱ������
		if ((writeFolder.lastModified() > readFolder.lastModified() && 
				 writeFolder.list().length > 0) || (writeFolder.listFiles().length > 0 &&
						 readFolder.listFiles().length == 0)) {

			File[] files = readFolder.listFiles();

			for (File file : files) {
				if (file.isDirectory())
					FileUtils.deleteDirectory(file);
				else
					file.delete();
			}

			FSDirectory srcDic = FSDirectory.open(writeFolder);
			FSDirectory destDic = FSDirectory.open(readFolder);

			Directory.copy(srcDic, destDic, true);

			srcDic.close();
			destDic.close();

			Logger.getLogger(IndexSearchManager.class).info(
					"�����ļ�ͬ�����:" + writeFolder);
		}

		return readFolder.list().length > 0;
	}

	/**
	 * �ͷŵ�������
	 * 
	 * @param docDir
	 * @return
	 */
	public boolean releaseSearch(File file) {
		IndexSearcher indexSearcher = indexSearches.get(file);
		//���û��ʹ�ã�ֱ�ӷ���true
		if(null == indexSearcher){
			return true;
		}

		boolean released = false;
		synchronized (reloading) {
			reloading = true;
		}
		
		try {
			indexSearcher.getIndexReader().close();
			Logger.getLogger(IndexSearchManager.class).info(
					"�ͷųɹ�" + file.getPath());
			indexSearches.remove(file);
		} catch (IOException e) {
			Logger.getLogger(IndexSearchManager.class).info(
					"�ͷ�ʧ��" + file.getPath());
		}
		
		
		synchronized (reloading) {
			reloading = false;
		}
		return released;
	}

	/**
	 * �ͷ������ļ�
	 */
	public void releaseAllSearch() {
		for (File file : indexSearches.keySet()) {
			try {
				indexSearches.get(file).getIndexReader().close();
				Logger.getLogger(IndexSearchManager.class).info(
						"�ͷųɹ�" + file.getPath());
			} catch (IOException e) {
				Logger.getLogger(IndexSearchManager.class).info(
						"�ͷ�ʧ��" + file.getPath() + e);
			}
		}
		indexSearchManager = null;
		indexSearches = null;
	}

	/**
	 * ȡ������������
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public IndexSearcher[] getIndexSearcher(String unitId)
			throws URISyntaxException, IOException {
		synchronized (reloading) {
			if (reloading) {
				Logger.getLogger(IndexSearchManager.class).info("Ŀ¼������ʹ��");
				return null;
			}
		}
		
		
		String systemType = Configurater.getInstance().getConfigValue("system_type");
		//�����ʡ��˾�û��ڹ�����¼��Ӧ�ü���ͬ��������
		if(StringUtils.isNotBlank(unitId) && unitId.trim().length() == 8
				&& "1".equals(systemType.trim())){
			String synWrite = FileDirUtils.getRealPathByUnitId("doc_write_Dir", unitId, "syn");
			File synFile = new File(synWrite);
			if(!synFile.exists()){
				synFile.mkdirs();
			}else{
				File synReadFile = FileDirUtils.getReadFileByWriteFile(synFile);
				if(this.initReadFolder(synFile, synReadFile)){
					reloadSingleFile(synReadFile);
					Logger.getLogger(IndexSearchManager.class).info(unitId + "ʡ��˾�û���¼����," +
							"ͬ����������سɹ�");
				}
			}
		}
		
		List<File> readFile = new ArrayList<File>();
		if(StringUtils.isNotBlank(unitId)){
			readFile.addAll(FileDirUtils.getAllLoadDirs("doc_read_Dir",unitId));
		}
		
		//��ȡword
		File wordFile = FileDirUtils.getWordFile();
		readFile.add(wordFile);
		
		List<IndexSearcher> lists = new ArrayList<IndexSearcher>();
		for (File file : readFile) {
			if (null != indexSearches.get(file)) {
				lists.add(indexSearches.get(file));
			}
		}
		
		// lists to array
		IndexSearcher[] indexs = new IndexSearcher[lists.size()];
		int i = 0;
		for (IndexSearcher searcher : lists) {
			indexs[i++] = searcher;
		}
		Logger.getLogger(IndexSearchManager.class).info(
				"����ȡ" + lists.size() + "��IndexSearch����");
		return indexs;
	}

}
