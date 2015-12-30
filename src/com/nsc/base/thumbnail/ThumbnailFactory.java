package com.nsc.base.thumbnail;

import com.nsc.base.conf.Configurater;
import com.nsc.base.index.Factory;
import com.nsc.base.util.FileUtil;

public class ThumbnailFactory extends Factory<ThumbnailInterface> {
	
    private static ThumbnailFactory thumbnail=null;
    
	public static ThumbnailFactory getInstance(){
		if(thumbnail==null){
			thumbnail=new ThumbnailFactory();
		}
		return thumbnail;
	}
	/**
	 * �����ļ���׺ȡ���ļ���ȡ����
	 * �ļ����ͣ�PDF��Word
	 * @param fileType
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public  ThumbnailInterface getAbstractor(String fileName) 
	       throws ClassNotFoundException, InstantiationException, 
	       IllegalAccessException{
		
		//���ж��Ƿ���ͼƬ�ļ�
		String fileSuffix = fileName.substring(fileName.lastIndexOf(".")+1);
		String mimeType = Configurater.getInstance().getConfigValue("mime",
				fileSuffix.toLowerCase());
		if (mimeType!=null && mimeType.indexOf("image")!=-1) {
			return super.getImplement("image_Thumbnails");
		}
		
		String metaType = FileUtil.getFileFormat(fileName)
				.toLowerCase();
		if(metaType==null || "".equals(metaType))
			return null;
		
		return super.getImplement(metaType + "_Thumbnails");
	}
}
