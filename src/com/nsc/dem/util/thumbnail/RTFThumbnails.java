package com.nsc.dem.util.thumbnail;

import java.io.File;

import org.apache.log4j.Logger;

import com.nsc.base.thumbnail.ThumbnailInterface;
import com.nsc.base.util.ExtractImages;
import com.nsc.base.util.FileUtil;
import com.nsc.base.util.Word2PDF;

public class RTFThumbnails implements ThumbnailInterface {

	/**
	 * �Ƚ�RTF�ļ�ת����PDF�ļ����ڽ�PDF��������ͼ
	 * ��������ͼ����·��
	 */
	public String makeThumbnil(String local) {
		String imagePath = "";
		String wordToPdf = "";
		try {
			wordToPdf = local.substring(0, local.lastIndexOf(".")) + ".pdf";
			Word2PDF tools = new Word2PDF(new File(local), new File(
					wordToPdf));
			//rtfת����pdf�ļ�
			tools.docToPdf();
            //��������ͼ
			imagePath = ExtractImages.toImages(wordToPdf);
		} catch (Exception e) {
			Logger.getLogger(ExtractImages.class).warn(local + "��������ͼʧ��", e);
		}
		
		if (!wordToPdf.equals("")) {
			FileUtil.deleteFile(wordToPdf);
		}
		return imagePath;
	}
}
