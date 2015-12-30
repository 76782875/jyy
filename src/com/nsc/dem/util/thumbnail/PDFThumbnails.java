package com.nsc.dem.util.thumbnail;

import org.apache.log4j.Logger;

import com.nsc.base.thumbnail.ThumbnailInterface;
import com.nsc.base.util.ExtractImages;

public class PDFThumbnails implements ThumbnailInterface{

	//��������ͼ
	public String makeThumbnil(String local) {
		//����ͼ����·��
		String imagePath="";
		try {
			imagePath = ExtractImages.toImages(local);
		} catch (Exception e) {
			Logger.getLogger(ExtractImages.class).warn(local + "��������ͼʧ��", e);
		}
		return imagePath;
	}
}
