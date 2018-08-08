package com.tuitui.tool.file;


import com.alibaba.fastjson.JSON;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelImportUtil {

    private final static String excel2003L =".xls";    //2003- 版本的excel
    private final static String excel2007U =".xlsx";   //2007+ 版本的excel

    /**
     * POI:解析Excel文件中的数据并把每行数据封装成一个实体
     * @param fileName 文件名称
     * @param object 待解析对象
     * @param flag 处理标志
     * @return List<Object> Excel中数据封装实体的集合
     */
    public List<Object> importExcelX(InputStream in, String fileName, Object object, String flag) throws Exception{

        List<Object> objects = new ArrayList<>();
        Workbook wookbook = null;
        Cell cell = null;
        String json = "";

        //创建Excel工作簿
        wookbook = getWorkbook(in,fileName);

        if(null == wookbook){
            throw new Exception("创建Excel工作薄为空！");
        }

        //得到第一个工作表
        Sheet sheet = wookbook.getSheetAt(0);

        //获得表头
        Row rowHead = sheet.getRow(0);

        //得到表头列数
        int headSize =rowHead.getPhysicalNumberOfCells();

        //表头
        String[] headers = new String[headSize];
        //遍历表头存放在数组中
        for(int i = 0; i < headSize; i++){
            cell = rowHead.getCell(i);
            headers[i] = cell.getStringCellValue();
        }

        //获得数据的总行数
        int totalRowNum = sheet.getLastRowNum();

        Map<String, String> headerMap = null;

        //遍历表格数据,生成对象
        for (int i = 1; i <= totalRowNum; i++) {
            Row rowDate = sheet.getRow(i);
            for(int j = 0; j < headSize; j++){
                cell = rowDate.getCell(j);
                String value = getCellValue(cell);
                if(value == null){
                    value = "";
                }
                value = value.trim();
                //String value = cell.getStringCellValue();

                String head = headerMap.get(headers [j]);
                if(head == null){
                    throw new Exception("标题栏解析错误！");
                }
                if("user_info".equals(flag)){
                    value = getUserInfoValue(head, value);
                } else if("collect_device".equals(flag)){
                    value = getCollectDeviceValue(head, value);
                }

                if(j != headSize - 1){
                    json = json+"\""+head+"\""+":"+"\""+value+"\""+",";
                }
                if(j == headSize - 1){
                    json = json+"\""+head+"\""+":"+"\""+value+"\"";
                }
            }

            //生成单个json数据
            json = "{"+json+"}";
            try {
                object = JSON.parseObject(json, object.getClass());
                objects.add(object);
                json = "";
            }catch(Exception e){
                throw new Exception("数据解析异常！");
            }
        }
        return objects;
    }

    private String getUserInfoValue(String head, String value) throws Exception{

        if("role".equals(head)){
            if("初级".equals(value)){
                value = "10000";
            }else if("高级".equals(value)){
                value = "10001";
            }else{
                throw new Exception("角色填写错误！");
            }
        }
        return value;
    }

    private String getCollectDeviceValue(String head, String value) throws Exception{
        //todo excel的head列表值
        return value;
    }



    /**
     * 描述：根据文件后缀，自适应上传文件的版本
     * @param inStr,fileName
     * @return
     * @throws Exception
     */
    private Workbook getWorkbook(InputStream inStr, String fileName) throws Exception{
        Workbook wb = null;
        String fileType = fileName.substring(fileName.lastIndexOf("."));

        if (excel2003L.equals(fileType)) {
            //2003-
            wb = new HSSFWorkbook(inStr);
        } else if (excel2007U.equals(fileType)) {
            //2007+
            wb = new XSSFWorkbook(inStr);
        } else {
            throw new Exception("解析的文件格式有误！");
        }
        return wb;
    }

    //判断从Excel文件中解析出来数据的格式
    private String getCellValue(Cell cell){
        String value = null;
        //日期格式化
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(cell == null || "".equals(cell) || cell.getCellType() == HSSFCell.CELL_TYPE_BLANK){
            value = null;
            return value;
        }

        //简单的查检列类型
        switch(cell.getCellType())
        {
            //字符串
            case Cell.CELL_TYPE_STRING:
                value = cell.getRichStringCellValue().getString();
                //value = StringUtil.convertNull(value);
                break;
            //数字
            case Cell.CELL_TYPE_NUMERIC:
                //判断是否是日期类型
                if(DateUtil.isCellDateFormatted(cell)){
                    Date date =cell.getDateCellValue();
                    //格式化日期类型
                    value = sdf.format(date);
                    break;
                }
                long dd = (long)cell.getNumericCellValue();
                value = dd+"";
                break;
            case Cell.CELL_TYPE_BLANK:
                value = "";
                break;
            case Cell.CELL_TYPE_FORMULA:
                value = String.valueOf(cell.getCellFormula());
                break;
            //boolean型值
            case Cell.CELL_TYPE_BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_ERROR:
                value = String.valueOf(cell.getErrorCellValue());
                break;
            default:
                break;
        }
        return value;
    }

}
