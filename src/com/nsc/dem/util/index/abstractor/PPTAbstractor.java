package com.nsc.dem.util.index.abstractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextRun;
import org.apache.poi.hslf.usermodel.SlideShow;

import com.nsc.base.index.ITextAbstractor;

public class PPTAbstractor extends ITextAbstractor {

	public StringBuffer abstractText(File file) throws IOException {
		StringBuffer content = new StringBuffer("");

		SlideShow ss = new SlideShow(new HSLFSlideShow(
				new FileInputStream(file)));// is

		// Ϊ�ļ���InputStream������SlideShow
		Slide[] slides = ss.getSlides();// ���ÿһ�Żõ�Ƭ
		for (int i = 0; i < slides.length; i++) {
			TextRun[] t = slides[i].getTextRuns();// Ϊ��ȡ�ûõ�Ƭ���������ݣ�����TextRun
			for (int j = 0; j < t.length; j++) {
				content.append(t[j].getText());// ����Ὣ�������ݼӵ�content��ȥ
			}
		}

		return content;
	}
}
