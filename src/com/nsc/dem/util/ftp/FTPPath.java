package com.nsc.dem.util.ftp;

import com.nsc.dem.bean.archives.TDoc;

public class FTPPath {
	
	public static String getFTPPath(TDoc tdoc){
		String filePath = tdoc.getPath();
		
		//��ȥ�ĵ�������ҵ����λ
		String unitName=filePath.replaceAll("/","\\\\");
		unitName=unitName.substring(unitName.indexOf("\\")+1);
		unitName=unitName.substring(unitName.indexOf("\\")+1);
		unitName=unitName.substring(0,unitName.indexOf("\\"));
		
		return unitName;
	}

}
