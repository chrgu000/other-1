package com.anosi.asset.util;

import com.anosi.asset.ExcelUtil.parser.ExcelParser;
import com.anosi.asset.ExcelUtil.parser.ExcelParser.ParserType;
import com.anosi.asset.exception.CustomRunTimeException;
import com.aspose.cells.SaveFormat;
import com.google.common.collect.Table;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/***
 * 主要用于读取excel，导出excel大部分情况下使用easypoi
 *
 * @author jinyao
 *
 */
public class ExcelUtil {

	/***
	 * 方法重载readExcelToText
	 *
	 * @param file
	 * @param sheetIndex
	 * @return
	 * @
	 */
	public static String readExcelToText(File file, Integer sheetIndex) throws Exception {
		return readExcelToText(new FileInputStream(file), sheetIndex);
	}

	/***
	 * 将excel读取string
	 *
	 * @param is
	 * @param sheetIndex
	 *            sheet的序号，从0开始,如果传入的是-1,表示所有sheet
	 * @return
	 * @throws
	 */
	public static String readExcelToText(InputStream is, Integer sheetIndex) throws Exception {
		return new ExcelParser().readExcelToString(is, sheetIndex);
	}

	/***
	 * 方法重载
	 *
	 * @param file
	 * @param sheetIndex
	 * @return
	 * @throws
	 */
	public static Table<Integer, String, Object> readExcel(File file, Integer sheetIndex) throws Exception {
		return readExcel(new FileInputStream(file), sheetIndex);
	}

	/**
	 * 读取excel
	 *
	 * @param is
	 * @param sheetIndex
	 *            sheet的序号，从0开始,如果为-1,代表遍历全部sheet
	 *
	 * @return 返回Guava的table类型
	 *
	 *         可以看用例 {@link com.anosi.asset.test.TestExcel#testReadExcel()}
	 */
	public static Table<Integer, String, Object> readExcel(InputStream is, Integer sheetIndex) throws Exception {
		return new ExcelParser().readExcel(is, sheetIndex, ParserType.SAX);
	}

	/***
	 * 将excel转为pdf
	 *
	 * @param inputStream
	 * @param outputStream
	 */
	public static void convert2PDF(InputStream inputStream, OutputStream outputStream) throws Exception {
		new com.aspose.cells.Workbook(inputStream).save(outputStream, SaveFormat.PDF);
	}

	public static void downLoad(String fileName, HttpServletResponse response, Workbook workbook, HttpServletRequest request) {
		String agent = request.getHeader("USER-AGENT");
		try {
			response.setCharacterEncoding("UTF-8");
			response.setHeader("content-Type", "application/vnd.ms-excel");
			fileName = fileName+ ".xls";
			// 火狐浏览器自己会对URL进行一次URL转码所以区别处理
			if (agent != null && agent.toLowerCase().indexOf("firefox") > 0) {
				fileName = new String(fileName.getBytes("GB2312"), StandardCharsets.ISO_8859_1);
			} else {
				fileName = URLEncoder.encode(fileName, "UTF-8");
			}
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			workbook.write(response.getOutputStream());
		} catch (IOException e) {
			throw new CustomRunTimeException("导出excel失败");
		}
	}
}
