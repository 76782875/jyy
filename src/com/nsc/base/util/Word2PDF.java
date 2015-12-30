package com.nsc.base.util;

import java.io.File;
import java.util.Date;
import java.net.ConnectException;

import org.apache.log4j.Logger;

import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.artofsolving.jodconverter.DocumentConverter;

public class Word2PDF {

	private File inputFile;// ��Ҫת�����ļ�
	private File outputFile;// ������ļ�

	public Word2PDF(File inputFile, File outputFile) {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
	}

	public void docToPdf() {
		Date start = new Date();
		// connect to an OpenOffice.org instance running on port 8100
		OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);
		try {
			connection.connect();

			// convert
			DocumentConverter converter = new OpenOfficeDocumentConverter(
					connection);
			converter.convert(inputFile, outputFile);
		} catch (ConnectException cex) {
			Logger.getLogger(Word2PDF.class).warn("����OpenOffice.org�����쳣:",cex);
		} finally {
			// close the connection
			if (connection != null) {
				connection.disconnect();
				connection = null;
			}
		}
		long l = (start.getTime() - new Date().getTime());
		long day = l / (24 * 60 * 60 * 1000);
		long hour = (l / (60 * 60 * 1000) - day * 24);
		long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		Logger.getLogger(Word2PDF.class).info("����" + outputFile.getName() + "�ķѣ�" + min + "��" + s
				+ "��");
	}
	
	public static void main(String[] args) {
		//ת��word2007Ҳ��û�����
	    File f1=new File("D:\\11.docx");
	    File f2=new File("D:\\22.pdf");
		Word2PDF w2p=new Word2PDF(f1,f2);
		w2p.docToPdf();
	}
}
