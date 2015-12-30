package com.nsc.dem.util.index;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.BadLocationException;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;

import com.nsc.base.conf.Configurater;
import com.nsc.base.index.DOCFIELDEnum;
import com.nsc.base.index.FIELDEnum;
import com.nsc.base.index.FileField;
import com.nsc.base.index.TextAbstractFactory;
import com.nsc.base.util.AppException;

public class DocumentWraper {

	/**
	 * αװ�ĵ� ������Ӧ�ļ����ֶ�
	 * 
	 * @param file
	 * @param params
	 * @param cPath
	 *            ��������·��
	 * @return
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @throws BadLocationException
	 * @throws InterruptedException 
	 */
	public static Document wrapDocument(File file,
			Map<Enum<?>, FileField> params, File cPath) throws IOException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, BadLocationException, XmlException,
			OpenXML4JException, InterruptedException {
		List<FileField> docParams = new ArrayList<FileField>();
		List<FileField> exParams = new ArrayList<FileField>();

		Set<Enum<?>> enums = params.keySet();

		for (Enum<?> e : enums) {
			if (e instanceof DOCFIELDEnum)
				docParams.add(params.get(e));
			else
				exParams.add(params.get(e));
		}

		return wrapDocument(file, docParams, exParams, cPath);
	}

	/**
	 * �Թؼ��ʽ�������
	 * @param keyword
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws BadLocationException
	 * @throws XmlException
	 * @throws OpenXML4JException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static Document wrapKeyWords(DOCFIELDEnum keyword) throws IOException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, BadLocationException, XmlException,
			OpenXML4JException, InvocationTargetException, NoSuchMethodException {

		// make a new, empty document
		Document doc = new Document();
		FileField f=keyword.getF();
		
		Logger.getLogger(DocumentWraper.class).info(
				"�������� " + f.getName() + " : " + f.getContent());

		Field field = new Field(f.getName(), f.getContent(),
				f.isStored() ? Field.Store.YES : Field.Store.NO, f
						.isIndexed() ? Field.Index.ANALYZED
						: Field.Index.NOT_ANALYZED,
				f.isHasPosition() ? Field.TermVector.WITH_POSITIONS_OFFSETS
						: Field.TermVector.NO);

		field.setBoost(f.getBoost());

		doc.add(field);

		return doc;
	}
	/**
	 * αװ�ĵ� ������Ӧ�ļ����ֶ�
	 * 
	 * @param file
	 * @param params
	 * @return
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @throws BadLocationException
	 * @throws InterruptedException 
	 */
	private static Document wrapDocument(File file, List<FileField> docParams,
			List<FileField> exParams, File cPath) throws IOException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, BadLocationException, XmlException,
			OpenXML4JException, InterruptedException {

		// make a new, empty document
		Document doc = new Document();

		// ׼����������
		for (FIELDEnum e : FIELDEnum.values()) {

			doFileField(file, cPath, doc, (FIELDEnum) e);
		}

		List<FileField> params = new ArrayList<FileField>();
		params.addAll(docParams);
		params.addAll(exParams);

		// �����ĵ�������
		for (FileField f : params) {

			Logger.getLogger(DocumentWraper.class).info(
					"�������� " + f.getName() + " : " + f.getContent());

			Field field = new Field(f.getName(), f.getContent(),
					f.isStored() ? Field.Store.YES : Field.Store.NO, f
							.isIndexed() ? Field.Index.ANALYZED
							: Field.Index.NOT_ANALYZED,
					f.isHasPosition() ? Field.TermVector.WITH_POSITIONS_OFFSETS
							: Field.TermVector.NO);

			field.setBoost(f.getBoost());

			doc.add(field);
		}

		List<Fieldable> fieldList = doc.getFields();

		for (Fieldable field : fieldList) {
			if (field.isIndexed())
				Logger.getLogger(DocumentWraper.class).info(
						file.getName() + " : �����ֶ� " + field.name() + " : "
								+ field.stringValue());
		}

		return doc;
	}

	/**
	 * �����ļ������ֶ�
	 * 
	 * @param file
	 * @param cPath
	 *            ���ݴ洢·��
	 * @param doc
	 * @param key
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @throws BadLocationException
	 * @throws InterruptedException 
	 */
	private static void doFileField(File file, File cPath, Document doc,
			FIELDEnum key) throws IOException, ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			BadLocationException, XmlException, OpenXML4JException, InterruptedException {

		switch (key) {
		case path:
			// Add the path of the file as a field named "path". Use a field
			// that is
			// indexed (i.e. searchable), but don't tokenize the field into
			// words.
			doc.add(new Field(key.name(), cPath.getPath(), Field.Store.YES,
					Field.Index.NOT_ANALYZED));
			break;
		case modified:
			// Add the last modified date of the file a field named "modified".
			// Use
			// a field that is indexed (i.e. searchable), but don't tokenize the
			// field
			// into words.
			doc.add(new Field(key.name(), DateTools.timeToString(file
					.lastModified(), DateTools.Resolution.MINUTE),
					Field.Store.YES, Field.Index.NOT_ANALYZED));
			break;
		case format:
			doc.add(new Field(key.name(), FilenameUtils.getExtension(file
					.getCanonicalPath()).toLowerCase(), Field.Store.YES,
					Field.Index.NOT_ANALYZED));
			break;
		case contents:
			// Add the contents of the file to a field named "contents". Specify
			// a Reader,
			// so that the text of the file is tokenized and indexed, but not
			// stored.
			// Note that FileReader expects the file to be in the system's
			// default encoding.
			// If that's not the case searching for special characters will
			// fail.
			// doc.add(new Field(key.name(), new FileReader(file)));

			StringBuffer sb = getContents(file);

			if (sb != null && sb.length() > 0 && cPath != null
					&& cPath.getPath().length() > 0) {

				// ContinueFTP ftp = ContinueFTP.getInstance();

				if (!cPath.exists()) {
					cPath.mkdir();
				}

				File target = File.createTempFile("con_", ".txt", cPath);

				// ftp.upload(file.getAbsolutePath(), target);

				// ftp.ftpClient.disconnect();

				String sbStr = sb.toString().trim();

				FileOutputStream os = new FileOutputStream(target, true);

				ByteArrayInputStream is = new ByteArrayInputStream(sbStr
						.getBytes());

				byte[] buf = new byte[1024];
				int r = -1;
				for (; (r = is.read(buf)) != -1;) {
					os.write(buf, 0, r);
				}

				is.close();
				os.close();

				FileField f = FIELDEnum.cpath.f;
				//��ȡcontent����Զ�·�� 
				String contentDir = Configurater.getInstance().getConfigValue("doc_content_Dir");
				contentDir = contentDir.replace("/", "\\");
				String cFile = target.getPath();
				cFile = cFile.substring(cFile.indexOf(contentDir)+contentDir.length());
				
				Field field = new Field(
						f.getName(),
						cFile,
						f.isStored() ? Field.Store.YES : Field.Store.NO,
						f.isIndexed() ? Field.Index.ANALYZED
								: Field.Index.NOT_ANALYZED,
						f.isHasPosition() ? Field.TermVector.WITH_POSITIONS_OFFSETS
								: Field.TermVector.NO);

				field.setBoost(f.getBoost());

				doc.add(field);

				doc.add(new Field(key.name(), sbStr, Field.Store.NO,
						Field.Index.ANALYZED,
						Field.TermVector.WITH_POSITIONS_OFFSETS));
				// �����ݣ����洢���ĵ�
			} else if (sb != null) {
				/*
				 * String sbStr = sb.toString().trim();
				 * 
				 * doc.add(new Field(key.name(), sbStr, Field.Store.YES,
				 * Field.Index.ANALYZED,
				 * Field.TermVector.WITH_POSITIONS_OFFSETS));
				 */
				throw new AppException("�ĵ�������", null, null, null);
			}

			break;
		}
	}

	/**
	 * �õ��ļ�������Ϣ
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @throws BadLocationException
	 * @throws InterruptedException 
	 */
	private static StringBuffer getContents(File file) throws IOException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, BadLocationException, XmlException,
			OpenXML4JException, InterruptedException {

		TextAbstractFactory textFactory = new TextAbstractFactory();
		return textFactory.getText(file);
	}

	private DocumentWraper() {
	}
}
