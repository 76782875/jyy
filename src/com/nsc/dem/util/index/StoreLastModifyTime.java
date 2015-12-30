package com.nsc.dem.util.index;

import java.io.File;
import java.util.Date;


/**
 * ����������һ���޸�ʱ��
 *   �����ϴ�����ʱʹ��
 */
public class StoreLastModifyTime {
	private static long  local;//read��local
	private static long  syn;//read��syn
	private static long  localContent;//content��local
	private static long  synContent;//content��syn
	private static StoreLastModifyTime instance ;
	private StoreLastModifyTime(){};
	
	public static synchronized StoreLastModifyTime createInstance(){
		if(instance == null)
			instance = new StoreLastModifyTime();
		return instance;
	}
	
	public  long getLocal() {
		return local;
	}

	public  void setLocal(long local) {
		StoreLastModifyTime.local = local;
	}

	public  long getSyn() {
		return syn;
	}

	public  void setSyn(long syn) {
		StoreLastModifyTime.syn = syn;
	}

	public long getLocalContent() {
		return localContent;
	}

	public void setLocalContent(long localContent) {
		StoreLastModifyTime.localContent = localContent;
	}

	public long getSynContent() {
		return synContent;
	}

	public void setSynContent(long synContent) {
		StoreLastModifyTime.synContent = synContent;
	}
}


