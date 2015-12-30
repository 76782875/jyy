package com.nsc.dem.util.index.abstractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

import com.nsc.base.index.ITextAbstractor;

public class RTFAbstractor extends ITextAbstractor {
	
	
	
	public StringBuffer abstractText(File file) throws IOException, BadLocationException {
		StringBuffer result = new StringBuffer();
		
			DefaultStyledDocument styledDoc = new DefaultStyledDocument();
			InputStream is = new FileInputStream(file);
			new RTFEditorKit().read(is, styledDoc, 0);
			result.append(new String(styledDoc.getText(0, styledDoc.getLength())
					.getBytes("iso8859-1"), "gbk"));
			// ��ȡ�ı�����ȡ������Ҫʹ��gbk���룬������������
		return result;
	}

}
