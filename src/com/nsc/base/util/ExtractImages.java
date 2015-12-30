package com.nsc.base.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import com.nsc.dem.bean.archives.TDoc;

/**
 * pdf��ʽתjpgͼƬ
 * @author Administrator
 *
 */
@SuppressWarnings("unchecked")
public class ExtractImages 
{
	
	public static String toImages(String path) throws IOException {
		
		File targetFile=new File(path);
		File encryptFolder=targetFile.getParentFile();
		
		if(!encryptFolder.isDirectory())
			encryptFolder.mkdirs();
		
        PDDocument doc = PDDocument.load(path);
        //int pageCount = doc.getPageCount(); 
        List pages = doc.getDocumentCatalog().getAllPages(); 
        
        //�ҵ�һ�������ݵ�ҳ��
        int pageNum = pages.size()>8?7:pages.size()>5?4:pages.size()>3?2:pages.size()>1?1:0;
        
        PDPage page = (PDPage)pages.get(pageNum);
        BufferedImage image = page.convertToImage();
        Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("jpg");
        ImageWriter writer = (ImageWriter)iter.next();
        
        path = path.substring(0, path.lastIndexOf("."))+".jpg";
        File outFile = new File(path);
        FileOutputStream out = new FileOutputStream(outFile);
        ImageOutputStream outImage = ImageIO.createImageOutputStream(out);
        writer.setOutput(outImage);
        writer.write(new IIOImage(image,null,null));
        
        outImage.close();
        out.close();
        doc.close();
        
        return path;
    }
	
	/**
	 * ͼƬ�ϴ� ��FTP
	 * 
	 *  �÷���������ͼ·�����õ�����TDoc������
	 * @param tdoc
	 * @param imagePath
	 *            ����·��
	 * @param ftpImagePath
	 *            ftp·��
	 */
	public static void uploadImage(TDoc tdoc, String imagePath, String ftpImagePath) {

		try {
			File file = new File(imagePath);
			if(!file.exists()){
				Logger.getLogger(ExtractImages.class).warn("������ͼ�����ڣ�"+imagePath);
				return;
			}
			tdoc.setPreviewPath(ftpImagePath);
			UploadStatus status = ContinueFTP.getInstance().upload(imagePath,
					ftpImagePath);
			Logger.getLogger(ExtractImages.class).info("�ϴ�ͼƬ:" + status);
		} catch (Exception e) {
			tdoc.setPreviewPath("");
			Logger.getLogger(ExtractImages.class).warn("�ϴ�����ͼʧ�ܣ�" + e.getMessage());
		}

	}
	
	@SuppressWarnings("deprecation")
    public static void main(String[] args) throws IOException { 
        PDDocument doc = PDDocument.load("C:\\test\\��Ŀ����֪ʶ��ϵָ��.PMBOK2008CN.pdf");
        int pageCount = doc.getPageCount(); 
        List pages = doc.getDocumentCatalog().getAllPages(); 
//        for(int i=0;i<pages.size();i++){
//            PDPage page = (PDPage)pages.get(i); 
//            BufferedImage image = page.convertToImage(); 
//            Iterator iter = ImageIO.getImageWritersBySuffix("jpg"); 
//            ImageWriter writer = (ImageWriter)iter.next(); 
//            File outFile = new File("C:\\test\\"+i+".jpg"); 
//            FileOutputStream out = new FileOutputStream(outFile); 
//            ImageOutputStream outImage = ImageIO.createImageOutputStream(out); 
//            writer.setOutput(outImage); 
//            writer.write(new IIOImage(image,null,null)); 
//        }
        PDPage page = (PDPage)pages.get(pages.size()-1);
        BufferedImage image = page.convertToImage();
        Iterator iter = ImageIO.getImageWritersBySuffix("jpg");
        ImageWriter writer = (ImageWriter)iter.next();
        File outFile = new File("C:\\test\\"+(pages.size()-1)+".jpg");
        FileOutputStream out = new FileOutputStream(outFile);
        ImageOutputStream outImage = ImageIO.createImageOutputStream(out);
        writer.setOutput(outImage);
        writer.write(new IIOImage(image,null,null));
        
        doc.close();
    }

}

