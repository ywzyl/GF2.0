package com.dje.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelDataProvider implements Iterator<Object[]>{
	/**
     * example，测试使用：
     */
    public static void main(String[] args) throws EncryptedDocumentException, InvalidFormatException, IOException {
        ExcelDataProvider dataProvider = new ExcelDataProvider("002","testCase1data.xls");
        while(dataProvider.hasNext()) {
            dataProvider.next();
        }
    }
    
    
    
    Iterator<Row> itRow;
    Row currentRow ;    
    Cell currentCell;
    String[] cloumnName;
    
    

    /**
     *该构造方法，通过指定的excel和该excel的指定sheet初始化列名数组 初始化行迭代器
     * @param index
     */
    public ExcelDataProvider(String sheetName,String fileName)  {
        InputStream inp = ExcelDataProvider.class.getResourceAsStream("/testData/"+fileName+".xls");

        Workbook workbook =null;
        try {
            workbook  = WorkbookFactory.create(inp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Sheet sheet = workbook.getSheet(sheetName);
        
        
        itRow = sheet.iterator();
        if (itRow.hasNext()) {
            //获取到第一行
            currentRow = itRow.next();
            int length = currentRow.getLastCellNum();
            cloumnName=new String[length];
            
            /*//获取到第一行的每个单元格内容
            for (int i = 0; i < cloumnName.length; i++) {
                cloumnName[i] = currentRow.getCell(i+1).getStringCellValue();
            }*/
            
            //得到第一行的迭代器
            Iterator<Cell> cellIt = currentRow.iterator();
            //将第一行的数据，填充到列名数组中
            for (int i = 0; cellIt.hasNext(); i++) {
                currentCell=cellIt.next();
                cloumnName[i]=currentCell.getStringCellValue();
            }
            System.out.println(Arrays.toString(cloumnName));
            
        }                            
    }
    
    /**
     * 通过行迭代器判断是否还有下一行
     */
	public boolean hasNext() {
		if (itRow.hasNext()) {
            return true;
        }
        return false;
	}
	
	/**
     * 通过行迭代器获取到下一行，遍历下一行的所有单元格，将数据放到map中，再将map包装成object的数组
     */
	public Object[] next() {
		//指向下一行
        currentRow = itRow.next();
        //遍历该行的单元格，并将单元格数据填充到map中
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < cloumnName.length; i++) {
            currentCell = currentRow.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
            currentCell.setCellType(CellType.STRING);
            map.put(cloumnName[i], currentCell.getStringCellValue());
        }
        Object[] objects = {map};
        
        System.out.println(map);
        return objects;
	}

}
