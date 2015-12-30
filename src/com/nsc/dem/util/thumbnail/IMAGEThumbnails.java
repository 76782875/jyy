package com.nsc.dem.util.thumbnail;

import java.io.File;

import org.apache.log4j.Logger;

import com.nsc.base.conf.Configurater;
import com.nsc.base.thumbnail.ThumbnailInterface;
import com.nsc.base.util.DesUtil;
import com.nsc.base.util.ExtractImages;
import com.nsc.base.util.ImageToJpg;

public class IMAGEThumbnails implements ThumbnailInterface{

    //��������ͼ
	public String makeThumbnil(String local) {
		//����ͼ����·��
		String imagePath="";
		try {
			imagePath = DesUtil.getFilePath(local)
					+ Configurater.getInstance()
							.getConfigValue("miniature") + File.separator
					+ DesUtil.getFileName(local);
            //��������ͼ
			ImageToJpg.saveImageAsJpg(local, imagePath, 500, 500);
		} catch (Exception e) {
			Logger.getLogger(ExtractImages.class).warn(local + "��������ͼʧ��", e);
		}
		return imagePath;
	}

}
