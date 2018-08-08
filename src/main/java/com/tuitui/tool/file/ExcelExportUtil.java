package com.tuitui.tool.file;


import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by rocky on 2018/1/2.
 */
public class ExcelExportUtil {

    private final Logger logger = LoggerFactory.getLogger(ExcelExportUtil.class);

    private static ExcelExportUtil excelExport = new ExcelExportUtil();

    public static ExcelExportUtil getInstance() {
        return excelExport;
    }

    private ExcelExportUtil() {
    }

    public String export(String[][] headersAndValues, String title, String fileName, String rootPath) {
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth((short) 25);
        // 生成一个样式
        HSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式

        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.BLACK.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        style.setFont(font);
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(HSSFColor.WHITE.index);
        style2.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        // 生成另一个字体
        HSSFFont font2 = workbook.createFont();
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style2.setFont(font2);
        // 产生表格标题行
        HSSFRow row = sheet.createRow(0);
        for (short i = 0; i < headersAndValues[0].length; i++)
        {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(headersAndValues[0][i]);
            cell.setCellValue(text);
        }

        for (int i = 1; i < headersAndValues.length; i++) {
            HSSFRow rows = sheet.createRow(i);
            for (int j = 0; j < headersAndValues[i].length; j++) {
                HSSFCell cells = rows.createCell(j);
                cells.setCellStyle(style2);
                cells.setCellValue(headersAndValues[i][j]);
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            workbook.write(baos);
        } catch (IOException e) {
            logger.warn(e.getLocalizedMessage(), null, "Export Excel error", null, null);
        }

        return save2file(baos.toByteArray(), fileName ,rootPath);
    }
    private String save2file(byte[] contentInBytes ,String basePath , String rootPath){
        FileOutputStream fop = null;
        File file = null;
        try {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            rootPath = rootPath + year;
            File root = new File(rootPath);
            if (!root.exists()) {
                root.mkdirs();
            }

            String filePath = rootPath + File.separator + basePath;
            basePath = year + File.separator + basePath;
            file = new File(filePath);

            if (!file.exists()) {
                file.createNewFile();
            }
            fop = new FileOutputStream(file);
            fop.write(contentInBytes);
            fop.flush();
            fop.close();
        } catch (IOException e) {
            logger.warn(e.getLocalizedMessage(), null, "create file error", null, null);
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {

            }
        }
        return basePath;
    }

}
