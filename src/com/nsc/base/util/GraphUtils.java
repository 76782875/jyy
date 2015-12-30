package com.nsc.base.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicEditorPaneUI;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * �������ڽ�HTML�ļ�ת����ͼƬ�ļ�
 * @author Administrator
 *
 */
public class GraphUtils {

	private final static Logger logger = Logger.getLogger(GraphUtils.class);
	 public static int DEFAULT_IMAGE_WIDTH = 1024;
	 public static int DEFAULT_IMAGE_HEIGHT = 768;

	 /**
	  * ��bufferedImageת��ΪͼƬ����Ϣ
	  * 
	  * @param image
	  * @return
	  */
	 public static String toJpeg(BufferedImage image,String imageName) {
	  // ��ȡͼƬ�ļ����ڷ�������·��
	  try {
	   ByteArrayOutputStream baos = new ByteArrayOutputStream();
	   JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(baos);
	   JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
	   param.setQuality(1.0f, false);
	   encoder.setJPEGEncodeParam(param);
	   encoder.encode(image);
	   byte[] buff = baos.toByteArray();
	   baos.close();
	   // ���ֽ���д���ļ�����ΪͼƬ
	   FileUtils.writeByteArrayToFile(new File(imageName), buff);
	   logger.info("����ɹ�!....");
	  } catch (Exception ex) {
	   logger.error("����ɾ��ͼƬʧ��:" + ex.getMessage());
	  }
	  return imageName;
	 }

	 /**
	  * htmlת��Ϊ������ļ�
	  * 
	  * @param bgColor
	  *            ͼƬ�ı���ɫ
	  * @param html
	  *            html���ı���Ϣ
	  * @param width
	  *            ��ʾͼƬ�ģԣ���������Ŀ��
	  * @param height
	  *            ��ʾͼƬ�ģԣ���������ĸ߶�
	  * @param eb
	  *            �O�������ı߿�
	  * @return
	  * @throws Exception
	  */
	 private static ArrayList<String> html2jpeg(Color bgColor, String html,
	   int width, int height, EmptyBorder eb,String local) throws Exception {
	  ArrayList<String> ret = new ArrayList<String>();
	  try {
	   JTextPane tp = new JTextPane();
	   tp.setSize(width, height);
	   if (eb == null) {
	    eb = new EmptyBorder(0, 50, 0, 50);
	   }
	   if (bgColor != null) {
	    tp.setBackground(bgColor);
	   }
	   if (width <= 0) {
	    width = DEFAULT_IMAGE_WIDTH;
	   }
	   if (height <= 0) {
	    height = DEFAULT_IMAGE_HEIGHT;
	   }
	   tp.setBorder(eb);
	   tp.setContentType("text/html");
	   tp.setText(html);
	   int pageIndex = 1;
	   boolean bcontinue = true;
	   while (bcontinue) {
	    BufferedImage image = new java.awt.image.BufferedImage(width,
	      height, java.awt.image.BufferedImage.TYPE_INT_RGB);
	    Graphics g = image.getGraphics();
	    g.setClip(0, 0, width, height);
	    bcontinue = paintPage(g, height, pageIndex,tp);
	    g.dispose();
	    String path = toJpeg(image,local);
	    ret.add(path);
	    pageIndex++;
	   }
	  } catch (Exception ex) {
	   throw ex;
	  }
	  return ret;
	 }

	 /**
	  * ��һ��htmlת��ΪͼƬ �д�С
	  * @param bgColor
	  * @param html
	  * @param width
	  * @param height
	  * @return
	  * @throws Exception
	  */
	 public static ArrayList<String> toImages(Color bgColor, String html,
	   int width, int height,String local) throws Exception {
	  return html2jpeg(bgColor, html, width, height, new EmptyBorder(0, 0, 0,
	    0),local);
	 }

	 /**
	  * ��һ��htmlת��ΪͼƬ
	  * 
	  * @param htmls
	  * @return
	  * @throws Exception
	  */
	 public static ArrayList<String> toImages(String html,String local) throws Exception {
	  return html2jpeg(null, html, DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_WIDTH,
	    new EmptyBorder(0, 0, 0, 0),local);
	 }

	 /**
	  * ����ͼƬ�ķ���
	  * 
	  * @param g
	  * @param hPage
	  * @param pageIndex
	  * @return
	  */
	 public static boolean paintPage(Graphics g, int hPage, int pageIndex,JTextPane panel) {
	  Graphics2D g2 = (Graphics2D) g;
	  Dimension d = ((BasicEditorPaneUI) panel.getUI())
	    .getPreferredSize(panel);
	  double panelHeight = d.height;
	  double pageHeight = hPage;
	  int totalNumPages = (int) Math.ceil(panelHeight / pageHeight);
	  g2.translate(0f, -(pageIndex - 1) * pageHeight);
	  panel.paint(g2);
	  boolean ret = true;

	  if (pageIndex >= totalNumPages) {
	   ret = false;
	   return ret;
	  }
	  return ret;
	 }

	 /**
	  * ��ȡhtml�ļ�����
	  * @param path
	  * @return
	  * @throws Exception
	  */
	public static String getHtml(String path) throws Exception{
		String temp;
		StringBuffer sb = new StringBuffer();
		BufferedReader in = new BufferedReader(new FileReader(path));
		 while ((temp = in.readLine()) != null){
			 sb.append(temp);
		 }
		 in.close();
		 return sb.toString();
	}
}
