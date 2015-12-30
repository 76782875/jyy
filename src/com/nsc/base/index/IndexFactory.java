package com.nsc.base.index;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;
import org.directwebremoting.util.Logger;

public class IndexFactory extends Factory<IIndexWriter> {

	static String INDEX_WRITER = "index_Writer";

	private static IndexFactory indexFactory;

	private ConcurrentMap<File, Object[]> indexWrites = new ConcurrentHashMap<File, Object[]>();

	public static IndexFactory getInstance() {
		if (indexFactory == null) {
			indexFactory = new IndexFactory();
		}
		return indexFactory;
	}

	/**
	 * ȡ����������
	 * 
	 * @param docDir
	 * @param analyzer
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws CorruptIndexException
	 * @throws LockObtainFailedException
	 * @throws IOException
	 */
	public  IIndexWriter getIndexWriter(File docDir, Analyzer analyzer)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, CorruptIndexException,
			LockObtainFailedException, IOException {
		
		synchronized (indexWrites) {
			IIndexWriter indexWriter = null;
			if (indexWrites.get(docDir) == null) {
				indexWriter = super.getImplement(INDEX_WRITER);
				indexWriter.initIndex(docDir, analyzer);
				ArrayBlockingQueue<IIndexWriter> block = new ArrayBlockingQueue<IIndexWriter>(100);
				indexWrites.put(docDir, new Object[] { indexWriter, block});
			}

			Object[] objs = indexWrites.get(docDir);
			indexWriter =(IIndexWriter) objs[0];
			ArrayBlockingQueue<IIndexWriter> block = (ArrayBlockingQueue<IIndexWriter>) objs[1];
			// ��ͼ�������
			block.add(indexWriter);
			return (IIndexWriter) objs[0];
		}
	}

	/**
	 * �õ��ڴ���������
	 * 
	 * @param analyzer
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws CorruptIndexException
	 * @throws LockObtainFailedException
	 * @throws IOException
	 */
	public IIndexWriter getIndexWriter(Analyzer analyzer)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, CorruptIndexException,
			LockObtainFailedException, IOException {
		IIndexWriter indexWriter = super.getImplement(INDEX_WRITER);

		if (null != indexWriter)
			indexWriter.initIndex(analyzer);

		return indexWriter;
	}

	public void close(File docDir) throws InterruptedException {

		synchronized (indexWrites) {
			Object[] objs = indexWrites.get(docDir);
			if (objs == null)
				return;

			IIndexWriter index = (IIndexWriter) objs[0];
			ArrayBlockingQueue<IIndexWriter> block = (ArrayBlockingQueue<IIndexWriter>) objs[1];

			// ȡ��һ����ֻ�������ã�����
			block.poll();
			if (block.isEmpty()) {
				try {
					index.closeWriter();
					indexWrites.remove(docDir);
				} catch (IOException e) {
					Logger.getLogger(IndexFactory.class).warn(e.getMessage());
				}
			}
		}
	}
}
