package com.nsc.base.index;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.store.LockObtainFailedException;

public interface IIndexSearcher {

	public static enum TYPE {
		whole, field
	};

	/**
	 * �����ĵ�
	 * 
	 * @param params
	 */
	public List<Map<Enum<?>, Object>> searchDocument(Map<?, Object> params,
			Map<?, Object> filters, int hitsPerPage, int page,
			boolean highLighter) throws ParseException, IOException,
			InvalidTokenOffsetsException, IllegalAccessException,
			InstantiationException, InvocationTargetException,
			NoSuchMethodException;

	public List<String> searchDocument(Map<?, Object> params,
			Map<?, Object> filters,boolean highLighter) throws ParseException, IOException,
			InvalidTokenOffsetsException, IllegalAccessException,
			InstantiationException, InvocationTargetException,
			NoSuchMethodException;
	public int getHitsSize();

	/**
	 * ��ʼ���������ṩ�����ļ��к��﷨������
	 * 
	 * @param docDir
	 * @param analyzer
	 */
	public boolean initIndex(IndexSearcher[] indexSearchers, Analyzer analyzer)
			throws CorruptIndexException, LockObtainFailedException,
			IOException;

	/**
	 * �Լ������¼���
	 * @param srcDir
	 * @return
	 * @throws CorruptIndexException
	 * @throws LockObtainFailedException
	 * @throws IOException
	 */
	public boolean reloadIndex(File srcDir) throws CorruptIndexException,
			LockObtainFailedException, IOException;

	/**
	 * �ͷż�������
	 * @return
	 */
	public boolean releaseIndex();
	
}
