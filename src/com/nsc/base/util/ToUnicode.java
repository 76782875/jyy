package com.nsc.base.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ToUnicode  
{     
  // ���ַ���תunicode  
  public static String convert(String s) {  
    String unicode = "";  
    char[] charAry = new char[s.length()];  
    for(int i=0; i<charAry.length; i++) {  
      charAry[i] = (char)s.charAt(i);  
      if(Character.isLetter(charAry[i])&&(charAry[i]>255))  
           unicode+="/u" + Integer.toString(charAry[i], 16);  
      else  
           unicode+=charAry[i];  
    }  
    return unicode;  
  }  
//���ļ�   
  public static String readFile(String filePath) throws IOException, FileNotFoundException {   
              String result = null;   
              File file = new File(filePath);   
              if (file.exists()) {   
                           FileInputStream fis = new FileInputStream(file);   
            byte[] b = new byte[fis.available()];   
            //���������ж�ȡb.length�����ֽڲ�����洢�ڻ��������� b �С�  
            fis.read(b);   
            //ʹ��ָ�����ַ�������ָ�����ֽ�����  
            result = new String(b, "utf-8");   
            fis.close();   
        }   
        return convert(result);   
    }   
}   