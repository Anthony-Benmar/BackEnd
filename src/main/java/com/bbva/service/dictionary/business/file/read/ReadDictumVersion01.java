package com.bbva.service.dictionary.business.file.read;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.bbva.util.exception.ApplicationException;

public class ReadDictumVersion01 extends ReadDictumBase{

    @Override
    protected List<String> readFields(XSSFWorkbook wbXLSX) {
        List<String> listFields = new ArrayList<>();

        if(wbXLSX.getNumberOfSheets() < 3){
            throw new ApplicationException("Se requiere la tercera hoja: Estructura DTS");
        }
        XSSFSheet sheet = wbXLSX.getSheetAt(2);
        int indexRowInicio = 4;
        int indexCellColumnFields = 1;
        for (int i = indexRowInicio; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            if(row != null) {
                XSSFCell cell = row.getCell(indexCellColumnFields);
                if(cell != null && CellType.STRING == cell.getCellType()){
                    listFields.add(cell.getStringCellValue());
                }else{
                    break;
                }                               
            }
        }
        
        return listFields;
    }
    
}
