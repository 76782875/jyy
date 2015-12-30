package com.nsc.base.index;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.swing.text.BadLocationException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;

public interface IIndexWriter {

	/**
	 * ���ĵ����ӵ�������
	 * 
	 * @param file
	 * @param params
	 * @param cPath
	 * @throws IOException
	 * @throws OpenXML4JException
	 * @throws XmlException
	 */
	public void addDocument(File file, File cPath,
			Map<Enum<?>, FileField> params) throws IOException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, BadLocationException, XmlException,
			OpenXML4JException,InterruptedException;

	// ���ĵ����ӵ������� ��ʾ
	public void addKeyWords(DOCFIELDEnum keyword) throws IOException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, BadLocationException, XmlException,
			OpenXML4JException, InvocationTargetException,
			NoSuchMethodException;

	/**
	 * �����ĵ�ID�����ĵ�
	 * 
	 * @param file
	 * @param cPath
	 * @param params
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws BadLocationException
	 * @throws OpenXML4JException
	 * @throws XmlException
	 */
	public void updateDocument(File file, File cPath,
			Map<Enum<?>, FileField> params) throws IOException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, BadLocationException, XmlException,
			OpenXML4JException,InterruptedException;

	/**
	 * �����ĵ�ID�����ĵ��ֶ�
	 * 
	 * @param params
	 */
	public void updateDocument(Map<Enum<?>, FileField> params);

	/**
	 * ���ݱ�ʶ��ɾ������
	 * 
	 * @param id
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public void deleteDocument(DOCFIELDEnum id) throws IllegalAccessException,
			InstantiationException, InvocationTargetException,
			NoSuchMethodException, CorruptIndexException, IOException;

	/**
	 * ��ʼ���ļ��������ṩ�����ļ��к��﷨������
	 * 
	 * @param docDir
	 * @param analyzer
	 */
	public void initIndex(File docDir, Analyzer analyzer)
			throws CorruptIndexException, LockObtainFailedException,
			IOException;

	/**
	 * ��ʼ���ڴ��������ṩ�����ļ��к��﷨������
	 * 
	 * @param docDir
	 * @param analyzer
	 */
	public void initIndex(Analyzer analyzer) throws CorruptIndexException,
			LockObtainFailedException, IOException;
	
	/**
	 * �ϲ�����
	 * @param dics
	 */
	public void addIndex(Directory[] dics) throws CorruptIndexException, IOException;
	
	/**
	 * �õ�Ŀ¼
	 * @return
	 */
	public Directory getDirectory();

	/**
	 * ���Ե��ùرճ��򣬹ر���������
	 */
	public void closeWriter() throws IOException;
}
