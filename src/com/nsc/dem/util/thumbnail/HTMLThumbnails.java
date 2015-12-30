package com.nsc.dem.util.thumbnail;

import java.awt.Color;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import com.nsc.base.thumbnail.ThumbnailInterface;
import com.nsc.base.util.GraphUtils;

public class HTMLThumbnails  implements ThumbnailInterface {

	//��������ͼ
	public String makeThumbnil(String local) {
		//����ͼ����·��
		String imagePath=local.substring(0,local.lastIndexOf("."))+".jpeg";
		Color c=new Color(255,255,255);
		String str;
		try {
			str = GraphUtils.getHtml(imagePath);
			str=Pattern.compile("<meta(.*?)>").matcher(str).replaceAll("");
			//��������ͼ
			GraphUtils.toImages(c,str,500,500,imagePath);
		} catch (Exception e) {
			Logger.getLogger(GraphUtils.class).warn(local + "��������ͼʧ��", e);
		}
		return imagePath;
	}

}
