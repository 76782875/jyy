package com.nsc.dem.util.index.abstractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;

import com.nsc.base.index.ITextAbstractor;

public class WordAbstractor extends ITextAbstractor {

	public StringBuffer abstractText(File file) throws IOException {
		StringBuffer content = new StringBuffer("");// �ĵ�����

		HWPFDocument doc = new HWPFDocument(new FileInputStream(file));
		Range range = doc.getRange();
		int paragraphCount = range.numParagraphs();// ����
		for (int i = 0; i < paragraphCount; i++) {// ���������ȡ����
			Paragraph pp = range.getParagraph(i);
			content.append(pp.text().replaceAll("", "")
									.replaceAll("", "")
									.replaceAll("", "")
									.replaceAll("", "")
									.replaceAll("", "")
									.replaceAll("", ""));
		}
		return content;
	}
}
