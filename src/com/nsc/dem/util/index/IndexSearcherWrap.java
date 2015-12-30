package com.nsc.dem.util.index;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

import com.nsc.base.conf.Configurater;
import com.nsc.base.index.DOCFIELDEnum;
import com.nsc.base.index.FIELDEnum;
import com.nsc.base.index.FileField;
import com.nsc.base.index.IIndexSearcher;
import com.nsc.base.index.KeywordFilter;
import com.nsc.base.recource.ResourceLoader;

public class IndexSearcherWrap implements IIndexSearcher {

	private MultiSearcher searcher;
	private Analyzer analyzer;
	private int hitsSize = 0;


	
	/**
	 * ��ʼ������
	 * @throws IOException 
	 */
	public boolean initIndex(IndexSearcher[] indexSearchers, Analyzer analyzer) throws IOException {

		this.searcher = new MultiSearcher(indexSearchers);
		this.analyzer = analyzer;
		return true;
	}

	/**
	 * �������¼���
	 */
	public boolean reloadIndex(IndexSearcher[] indexSearchers) throws CorruptIndexException,
			LockObtainFailedException, IOException {
		this.searcher = new MultiSearcher(indexSearchers);
		return true;
	}

	
	
	
	/**
	 * �õ��������
	 */
	public int getHitsSize() {
		return this.hitsSize;
	}

	/**
	 * ����ȫ�ļ���
	 * @throws  
	 */
	public List<Map<Enum<?>, Object>> searchDocument(Map<?, Object> params,
			Map<?, Object> filters, int hitsPerPage, int page,
			boolean highLighter) throws ParseException, IOException,
			InvalidTokenOffsetsException, IllegalAccessException,
			InstantiationException, InvocationTargetException,
			NoSuchMethodException{

		List<Map<Enum<?>, Object>> docList = new ArrayList<Map<Enum<?>, Object>>();

		// searcher.setSimilarity(similarity);

		BooleanQuery bQuery = new BooleanQuery(); // ��ϲ�ѯ

		Set<?> keys = params.keySet();

		for (Object param : keys) {

			String qStr = (String) params.get(param);

			if (qStr == null || qStr.length() == 0)
				continue;

			try {

				// ȥ�������ַ�
				qStr = KeywordFilter.filterKeyword(qStr);

				Query preQuery = new QueryParser(Version.LUCENE_30, " ",
						analyzer).parse(qStr);
				qStr = preQuery.toString().replaceAll("\"", "").replaceAll(":",
						"");
			} catch (ParseException ex) {
				Logger.getLogger(IndexSearcherWrap.class).warn(ex);
				continue;
			}

			if (qStr.length() == 0) {
				continue;
			} else if (param.equals(TYPE.whole)) {
				doWholeQuery(bQuery, qStr);
			} else if (param instanceof DOCFIELDEnum
					|| param instanceof EXFIELDEnum
					|| param instanceof FIELDEnum) {

				Enum<?> e = (Enum<?>) param;

				FileField f = DOCFIELDEnum.getValue(e.name()) != null ? DOCFIELDEnum
						.getValue(e.name()).getF()
						: EXFIELDEnum.getValue(e.name()) != null ? EXFIELDEnum
								.getValue(e.name()).getF() : FIELDEnum
								.getValue(e.name()).f;

				Query fieldQuery = new QueryParser(Version.LUCENE_30, e.name(),
						analyzer).parse(qStr);

				bQuery.add(fieldQuery, f.getOccurInt() == 3 ? Occur.SHOULD : f
						.getOccurInt() == 2 ? Occur.MUST_NOT : Occur.MUST);

				Logger.getLogger(IndexSearcherWrap.class).info(
						"�����ʻ�: " + fieldQuery.toString());
			} else if (param instanceof String) {
				Query fieldQuery = new QueryParser(Version.LUCENE_30, param
						.toString(), analyzer).parse(qStr);

				bQuery.add(fieldQuery, Occur.SHOULD);

				Logger.getLogger(IndexSearcherWrap.class).info(
						"�����ʻ�: " + fieldQuery.toString());
			} else {
				Logger.getLogger(IndexSearcherWrap.class).info(
						"�޷��жϸ����������ͣ� " + param.getClass().getName()
								+ " : �����ʻ�: " + qStr);
			}
		}

		if (bQuery.clauses().size() == 0)
			return docList;

		Logger.getLogger(IndexSearcherWrap.class).info(
				"ȫ�������ʻ�: " + bQuery.toString());

		// Collect enough docs to show 5 pages
		TopScoreDocCollector collector = TopScoreDocCollector.create(page
				* hitsPerPage, false);

		BooleanQuery bfQuery = new BooleanQuery(); // ��ϲ�ѯ
		for (Object filter : filters.keySet()) {

			String qStr = (String) filters.get(filter);

			if (filter instanceof DOCFIELDEnum || filter instanceof EXFIELDEnum
					|| filter instanceof FIELDEnum) {

				Enum<?> e = (Enum<?>) filter;

				Term term = new Term(e.name(), qStr);
				Query query = new TermQuery(term);

				bfQuery.add(query, Occur.MUST);

				Logger.getLogger(IndexSearcherWrap.class).info(
						"���˴ʻ�: " + query.toString());
			} else if (filter instanceof String) {

				Term term = new Term(filter.toString(), qStr);
				Query query = new TermQuery(term);

				bfQuery.add(query, Occur.MUST);

				Logger.getLogger(IndexSearcherWrap.class).info(
						"���˴ʻ�: " + query.toString());
			} else {
				Logger.getLogger(IndexSearcherWrap.class).info(
						"�޷��жϸ����������ͣ� " + filter.getClass().getName()
								+ " : ���˴ʻ�: " + qStr);
			}
		}

		QueryWrapperFilter filter = new QueryWrapperFilter(bfQuery);

		Logger.getLogger(IndexSearcherWrap.class).info(
				"ȫ�����˴ʻ�: " + bfQuery.toString());

		if (bfQuery.clauses().size() != 0)
			searcher.search(bQuery, filter, collector);
		else
			// searcher.search(new TermQuery(new Term("cpath","con_59065.txt")),
			// collector);
			searcher.search(bQuery, collector);

		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		hitsSize = collector.getTotalHits();
		Logger.getLogger(IndexSearcherWrap.class).info(
				hitsSize + " total matching documents");

		if (hitsSize == 0)
			return docList;

		int start = (page - 1) * hitsPerPage;
		int end = Math.min(hits.length, start + hitsPerPage);
		// if (hits.length >= start) {
		// // end = Math.max(hits.length, start + hitsPerPage);
		// } else {

		// }

		// ��������и�����ʾ��Ĭ����<b>..</b>
		// �����ָ��<read>..</read>
		SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter(
				"<b><font color='red'>", "</font></b>");
		QueryScorer queryScorer = new QueryScorer(bQuery);
		// �������
		// ָ�������ĸ�ʽ
		// ָ����ѯ����
		Highlighter highlighter = new Highlighter(simpleHTMLFormatter,
				queryScorer);
		// ���һ�������Ҫ���صģ����������ݳ���
		// ���̫С����ֻ�����ݵĿ�ʼ���ֱ��������������ҷ��ص�����Ҳ��
		// ̫����ʱ̫�˷��ˡ�
		highlighter
				.setTextFragmenter(new SimpleSpanFragmenter(queryScorer, 200));
		for (int i = start; i < end; i++) {

			Document doc = searcher.doc(hits[i].doc);

			Logger.getLogger(IndexSearcherWrap.class).info(
					doc.get("title") + " ƥ�����: " + hits[i].score);

			Map<Enum<?>, Object> fieldMap = new HashMap<Enum<?>, Object>();

			List<Fieldable> fieldList = doc.getFields();

			for (Fieldable field : fieldList) {
				String name = field.name();
				String content = doc.get(name);

				Enum<?> filedEnum = FIELDEnum.getValue(name);

				if (filedEnum != null) {
					FIELDEnum fileF = (FIELDEnum) filedEnum;

					// ��ȡ����,������ݴ��·����Ϊ�գ��������ļ�����ȡ����
					if (fileF.equals(FIELDEnum.cpath)) {

						String cFile = doc.get(FIELDEnum.cpath.name());
						
						String contentDir = Configurater.getInstance().getConfigValue("doc_content_Dir");
						URL contentUrl = ResourceLoader.getDefaultClassLoader().getResource(contentDir);
						try {
							contentDir = contentUrl.toURI().getPath();
						} catch (URISyntaxException e) {
							Logger.getLogger(IndexSearcherWrap.class).warn(e.getMessage());
						}
						File file = new File(contentDir,File.separator+cFile);

						if (file.exists()) {
							FileReader ir = new FileReader(file);

							StringBuffer sb = new StringBuffer();

							int r = -1;
							char[] buf = new char[1024];
							while ((r = ir.read(buf)) != -1)
								sb.append(buf, 0, r);

							ir.close();

							content = sb.toString();
						} else {
							content = "";
						}

						filedEnum = FIELDEnum.contents;
						fileF = FIELDEnum.contents;
					}

					if (highLighter && fileF.f.isHighLighter()
							&& content != null && content.trim().length() > 0) {
						content = highLigher(highlighter, hits[i].doc, content,
								200).toString();
					} else if (fileF.equals(FIELDEnum.contents)) {
						content = content.substring(0, Math.min(content
								.length(), 200));
					}
				} else {
					filedEnum = DOCFIELDEnum.getValue(name);

					if (filedEnum != null) {
						DOCFIELDEnum docF = (DOCFIELDEnum) filedEnum;

						if (highLighter && docF.getF().isHighLighter()) {
							content = highLigher(highlighter, hits[i].doc,
									content, 200).toString();
						} else if (docF.equals(DOCFIELDEnum.title)) {
							content = content.substring(0, Math.min(content
									.length(), 200));
						}
					} else {
						filedEnum = EXFIELDEnum.getValue(name);

						if (filedEnum != null) {
							EXFIELDEnum docF = (EXFIELDEnum) filedEnum;

							if (highLighter && docF.getF().isHighLighter()) {
								content = highLigher(highlighter, hits[i].doc,
										content, 200).toString();
							} else {
								content = content.substring(0, Math.min(content
										.length(), 200));
							}
						}
					}
				}

				fieldMap.put(filedEnum, content);
			}

			docList.add(fieldMap);
		}

		this.searcher.close();

		return docList;
	}

	/**
	 * ��������
	 * 
	 * @param highlighter
	 * @param doc
	 * @param contents
	 * @param limit
	 * @return
	 * @throws IOException
	 * @throws InvalidTokenOffsetsException
	 */
	@SuppressWarnings("all")
	private StringBuffer highLigher(Highlighter highlighter, int doc,
			String contents, int limit) throws IOException,
			InvalidTokenOffsetsException {

		if (contents != null) {
			StringBuffer sb = new StringBuffer();
			// TermPositionVector tpv = (TermPositionVector) searcher
			// .getIndexReader().getTermFreqVector(doc,
			// FIELDEnum.contents.name());

			TokenStream tokenStream;
			// if (tpv != null)
			// tokenStream = TokenSources.getTokenStream(tpv,true);
			// else
			tokenStream = this.analyzer.tokenStream(FIELDEnum.contents.name(),
					new StringReader(contents));

			String str = highlighter.getBestFragment(tokenStream, contents);

			sb// .append("<li><li>")
					.append(str);
			// .append("<br/>");

			int length = Math.min(limit, contents.length());

			return str == null ? new StringBuffer(contents.substring(0, length))
					: sb;
		}

		return null;
	}

	/**
	 * ȫ�ֶβ�ѯ
	 * 
	 * @param bQuery
	 * @param qStr
	 * @throws ParseException
	 */
	private void doWholeQuery(BooleanQuery bQuery, String qStr)
			throws ParseException {

		// ���������ֶ�
		for (FIELDEnum key : FIELDEnum.values()) {

			Query fieldQuery = new QueryParser(Version.LUCENE_30, key.name(),
					analyzer).parse(qStr);

			bQuery.add(fieldQuery, Occur.SHOULD);

			Logger.getLogger(IndexSearcherWrap.class).info(
					"�����ʻ�: " + fieldQuery.toString());
		}

		// ���������ֶ�
		for (DOCFIELDEnum key : DOCFIELDEnum.values()) {

			Query fieldQuery = new QueryParser(Version.LUCENE_30, key.name(),
					analyzer).parse(qStr);

			bQuery.add(fieldQuery, Occur.SHOULD);

			Logger.getLogger(IndexSearcherWrap.class).info(
					"�����ʻ�: " + fieldQuery.toString());
		}

		// ������չ�ֶ�
		for (EXFIELDEnum key : EXFIELDEnum.values()) {
			Query fieldQuery = new QueryParser(Version.LUCENE_30, key.name(),
					analyzer).parse(qStr);

			bQuery.add(fieldQuery, Occur.SHOULD);

			Logger.getLogger(IndexSearcherWrap.class).info(
					"�����ʻ�: " + fieldQuery.toString());
		}
	}

	/**
	 * �ͷż���
	 * @throws IOException 
	 */
	public boolean releaseIndex() {
		try {
			this.searcher.close();
			return true;
		} catch (IOException e) {
			Logger.getLogger(IndexSearcherWrap.class).info(
					"�ͷż���ʧ��: " + e);
		}
		return false;
	}

	public List<String> searchDocument(Map<?, Object> params,
			Map<?, Object> filters, boolean highLighter) throws ParseException,
			IOException, InvalidTokenOffsetsException, IllegalAccessException,
			InstantiationException, InvocationTargetException,
			NoSuchMethodException {

		List<String> docList = new ArrayList<String>();
		BooleanQuery bQuery = new BooleanQuery(); // ��ϲ�ѯ
		Set<?> keys = params.keySet();
		for (Object param : keys) {
			String qStr = (String) params.get(param);
			if (qStr == null || qStr.length() == 0)
				continue;
			try {
				// ȥ�������ַ�
				qStr = KeywordFilter.filterKeyword(qStr);
				Query preQuery = new QueryParser(Version.LUCENE_30, " ",
						analyzer).parse(qStr);
				qStr = preQuery.toString().replaceAll("\"", "").replaceAll(":",
						"");
			} catch (ParseException ex) {
				Logger.getLogger(IndexSearcherWrap.class).warn(ex);
				continue;
			}

			if (qStr.length() == 0) {
				continue;
			} else if (param.equals(TYPE.whole)) {
				doWholeQuery(bQuery, qStr);
			} else if (param instanceof DOCFIELDEnum
					|| param instanceof EXFIELDEnum
					|| param instanceof FIELDEnum) {
				Enum<?> e = (Enum<?>) param;
				FileField f = DOCFIELDEnum.getValue(e.name()) != null ? DOCFIELDEnum
						.getValue(e.name()).getF()
						: EXFIELDEnum.getValue(e.name()) != null ? EXFIELDEnum
								.getValue(e.name()).getF() : FIELDEnum
								.getValue(e.name()).f;

				Query fieldQuery = new QueryParser(Version.LUCENE_30, e.name(),
						analyzer).parse(qStr);
				bQuery.add(fieldQuery, f.getOccurInt() == 3 ? Occur.SHOULD : f
						.getOccurInt() == 2 ? Occur.MUST_NOT : Occur.MUST);
				Logger.getLogger(IndexSearcherWrap.class).info(
						"�����ʻ�: " + fieldQuery.toString());
			} else if (param instanceof String) {
				Query fieldQuery = new QueryParser(Version.LUCENE_30, param
						.toString(), analyzer).parse(qStr);

				bQuery.add(fieldQuery, Occur.SHOULD);
				Logger.getLogger(IndexSearcherWrap.class).info(
						"�����ʻ�: " + fieldQuery.toString());
			} else {
				Logger.getLogger(IndexSearcherWrap.class).info(
						"�޷��жϸ����������ͣ� " + param.getClass().getName()
								+ " : �����ʻ�: " + qStr);
			}
		}

		if (bQuery.clauses().size() == 0)
			return docList;
		Logger.getLogger(IndexSearcherWrap.class).info(
				"ȫ�������ʻ�: " + bQuery.toString());
		TopScoreDocCollector collector = TopScoreDocCollector
				.create(200, false);
		BooleanQuery bfQuery = new BooleanQuery(); // ��ϲ�ѯ
		QueryWrapperFilter filter = new QueryWrapperFilter(bfQuery);
		Logger.getLogger(IndexSearcherWrap.class).info(
				"ȫ�����˴ʻ�: " + bfQuery.toString());
		if (bfQuery.clauses().size() != 0)
			searcher.search(bQuery, filter, collector);
		else
			searcher.search(bQuery, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		hitsSize = collector.getTotalHits();
		Logger.getLogger(IndexSearcherWrap.class).info(
				hitsSize + " total matching documents");
		if (hitsSize == 0)
			return docList;
		// ��ѯ���
		for (int i = 0; i < hitsSize; i++) {
			Document doc = searcher.doc(hits[i].doc);
			Logger.getLogger(IndexSearcherWrap.class).info(
					(doc.get("title") == null ? doc.get("keyword") : doc.get("title"))
					+ " ƥ�����: " + hits[i].score);
			List<Fieldable> fieldList = doc.getFields();
			String content = "";
			for (Fieldable field : fieldList) {
				content = doc.get(field.name());
			}
			docList.add(content);
		}
		this.searcher.close();
		return docList;
	}

	public boolean reloadIndex(File srcDir) throws CorruptIndexException,
			LockObtainFailedException, IOException {
		// TODO Auto-generated method stub
		return false;
	}
}
