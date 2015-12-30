package com.nsc.base.excel;

/**

 * ���ÿ�Դ���POI3.0.2��̬����EXCEL�ĵ�

 * ת��ʱ�뱣��������Ϣ��ע��������

 * @author leno

 * @version v1.0

 * @param <T> Ӧ�÷��ͣ���������һ������javabean������

 * ע������Ϊ�˼������boolean�͵�����xxx��get����ʽΪgetXxx(),������isXxx()

 * byte[]��jpg��ʽ��ͼƬ����

 */
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.directwebremoting.util.Logger;

public class ExportUtil<T> {

	public void exportExcel(Collection<T> dataset, OutputStream out) {
		exportExcel("��־����", null, dataset, out, "yyyy-MM-dd");
	}

	public void exportExcel(String[] headers, Collection<T> dataset,OutputStream out) {
		exportExcel("��־����", headers, dataset, out, "yyyy-MM-dd");
	}

	public void exportExcel(String[] headers, Collection<T> dataset,OutputStream out, String pattern) {
		exportExcel("��־����", headers, dataset, out, pattern);
	}

	/**
	 * ����һ��ͨ�õķ�����������JAVA�ķ�����ƣ����Խ�������JAVA�����в��ҷ���һ��������������EXCEL ����ʽ�����ָ��IO�豸��
	 * @param title
	 *            ��������
	 * @param headers
	 *            ���������������
	 * @param dataset
	 *            ��Ҫ��ʾ�����ݼ���,������һ��Ҫ���÷���javabean������Ķ��󡣴˷���֧�ֵ�
	 *            javabean���Ե����������л����������ͼ�String,Date,byte[](ͼƬ����)
	 * @param out
	 *            ������豸�����������󣬿��Խ�EXCEL�ĵ������������ļ�����������
	 * @param pattern
	 *            �����ʱ�����ݣ��趨�����ʽ��Ĭ��Ϊ"yyy-MM-dd"
	 */

	@SuppressWarnings("unchecked")
	public void exportExcel(String title, String[] headers,Collection<T> dataset, 
			OutputStream out, String pattern) {

		// ����һ��������
		HSSFWorkbook workbook = new HSSFWorkbook();
		// ����һ�����
		HSSFSheet sheet=workbook.createSheet(title);
		// ���ñ��Ĭ���п��Ϊ15���ֽ�
		sheet.setDefaultColumnWidth(15);
		
		// ����һ����ʽ
		HSSFCellStyle style = getStyle1(workbook);
		// ����һ������
		HSSFFont font = getFont1(workbook);
		// ������Ӧ�õ���ǰ����ʽ
		style.setFont(font);

		// ���ɲ�������һ����ʽ
		HSSFCellStyle style2 = getStyle2(workbook);
		// ������һ������
		HSSFFont font2 = getFont2(workbook);
		// ������Ӧ�õ���ǰ����ʽ
		style2.setFont(font2);

		// ����һ����ͼ�Ķ���������
		HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		// ����ע�͵Ĵ�С��λ��,����ĵ�
		HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0,
				0, 0, 0, (short) 4, 2, (short) 6, 5));

		// ����ע������
		comment.setString(new HSSFRichTextString("��־����"));
		// ����ע�����ߣ�������ƶ�����Ԫ�����ǿ�����״̬���п���������.
		comment.setAuthor("admin");

		// ������������
		HSSFRow row = sheet.createRow(0);

		for (int i = 0; i < headers.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(style);
			HSSFRichTextString text = new HSSFRichTextString(headers[i]);
			cell.setCellValue(text);
		}
		
		HSSFFont font3 = workbook.createFont();
		// �����������ݣ�����������
		Iterator<T> it = dataset.iterator();
		int index = 0;
		while (it.hasNext()) {
			index++;
			row = sheet.createRow(index);
			T t = (T) it.next();

			// ���÷��䣬����javabean���Ե��Ⱥ�˳�򣬶�̬����getXxx()�����õ�����ֵ
			Field[] fields = t.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				HSSFCell cell = row.createCell(i);
				cell.setCellStyle(style2);
				Field field = fields[i];
				String fieldName = field.getName();
				String getMethodName = "get"
				+ fieldName.substring(0, 1).toUpperCase()
				+ fieldName.substring(1);

				try {
					Class tCls = t.getClass();
					Method getMethod = tCls.getMethod(getMethodName,new Class[] {});
					Object value = getMethod.invoke(t, new Object[] {});

					// �ж�ֵ�����ͺ����ǿ������ת��
					String textValue = null;
					if (value instanceof Boolean) {
						boolean bValue = (Boolean) value;
						textValue = "true";
						if (!bValue) {
							textValue = "false";
						}
					} else if (value instanceof Date) {
						Date date = (Date) value;
						SimpleDateFormat sdf = new SimpleDateFormat(pattern);
						textValue = sdf.format(date);
					} else if (value instanceof byte[]) {
						// ��ͼƬʱ�������и�Ϊ60px;
						row.setHeightInPoints(60);
						// ����ͼƬ�����п��Ϊ80px,ע�����ﵥλ��һ������
						sheet.setColumnWidth(i, (short) (35.7 * 80));
						// sheet.autoSizeColumn(i);
						byte[] bsValue = (byte[]) value;
						HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0,
						1023, 255, (short) 6, index, (short) 6, index);
						anchor.setAnchorType(2);
						patriarch.createPicture(anchor, workbook.addPicture(
						bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));
					} else {
						// �����������Ͷ������ַ����򵥴���
						textValue = value.toString();
					}

					// �������ͼƬ���ݣ�������������ʽ�ж�textValue�Ƿ�ȫ�����������
					if (textValue != null) {
						Pattern p = Pattern.compile("^//d+(//.//d+)?$");
						Matcher matcher = p.matcher(textValue);
						if (matcher.matches()) {
							// �����ֵ���double����
							cell.setCellValue(Double.parseDouble(textValue));
						} else {
							HSSFRichTextString richString = new HSSFRichTextString(
									textValue);
							font3.setColor(HSSFColor.BLUE.index);
							richString.applyFont(font3);
							cell.setCellValue(richString);
						}
					}
					
				} catch (SecurityException e) {
					Logger.getLogger(ExportUtil.class).warn(e.getMessage());
				} catch (NoSuchMethodException e) {
					Logger.getLogger(ExportUtil.class).warn(e.getMessage());
				} catch (IllegalArgumentException e) {
					Logger.getLogger(ExportUtil.class).warn(e.getMessage());
				} catch (IllegalAccessException e) {
					Logger.getLogger(ExportUtil.class).warn(e.getMessage());
				} catch (InvocationTargetException e) {
					Logger.getLogger(ExportUtil.class).warn(e.getMessage());
				} finally {
				}
			}
		}
		try {
			workbook.write(out);
			out.flush();
			out.close();
		} catch (IOException e) {
			Logger.getLogger(ExportUtil.class).warn(e.getMessage());
		}
	}

	public static HSSFCellStyle getStyle1(HSSFWorkbook book) {
		// ����һ����ʽ
		HSSFCellStyle style = book.createCellStyle();
		// ������Щ��ʽ
		style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		
		return style;
	}
	
	public static HSSFFont getFont1(HSSFWorkbook book) {
		// ����һ������
		HSSFFont font = book.createFont();
		// ������Щ����
		font.setColor(HSSFColor.VIOLET.index);
		font.setFontHeightInPoints((short) 12);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		return font;
	}
	
	
	public static HSSFCellStyle getStyle2(HSSFWorkbook book) {
		// ���ɲ�������һ����ʽ
		HSSFCellStyle style = book.createCellStyle();
		style.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		
		return style;
	}
	
	public static HSSFFont getFont2(HSSFWorkbook book) {
		// ����һ������
		HSSFFont font = book.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		return font;
	}
}