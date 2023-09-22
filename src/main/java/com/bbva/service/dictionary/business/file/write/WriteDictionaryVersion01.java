package com.bbva.service.dictionary.business.file.write;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.bbva.entities.dictionary.GenerationFieldEntity;

public class WriteDictionaryVersion01 extends WriteDictionaryBase{

    @Override
    protected void writeContent(XSSFWorkbook wbXLSX) {
        XSSFSheet detalleXSSFSheet = wbXLSX.getSheetAt(1);
        int columnaPhysicalNameField = 8;
        int columnaLogicalNameField = 9;
        int columnaSimpleFieldDescription = 10;
        int columnaSourceField = 22;
        int rowEdicion = 1;
        for(GenerationFieldEntity generationFieldEntity : super.getListaGenerationFieldEntity()){
            XSSFRow row = detalleXSSFSheet.getRow(++rowEdicion);
            row.getCell(columnaPhysicalNameField).setCellValue(generationFieldEntity.getPhysicalFieldName());
            row.getCell(columnaLogicalNameField).setCellValue(generationFieldEntity.getLogicalFieldName());
            row.getCell(columnaSimpleFieldDescription).setCellValue(generationFieldEntity.getDescriptionFieldDesc());
            row.getCell(columnaSourceField).setCellValue(generationFieldEntity.getFieldName());
        }
    }
    
}
