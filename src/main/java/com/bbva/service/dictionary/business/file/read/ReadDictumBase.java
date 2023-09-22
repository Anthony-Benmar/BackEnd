package com.bbva.service.dictionary.business.file.read;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.bbva.util.exception.ApplicationException;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ReadDictumBase {
    
    private static final Logger LOGGER = Logger.getLogger(ReadDictumBase.class.getName());

    public ReadDictumResult process(InputStream inputStreamFile) throws IOException{
        
        ReadDictumResult result = new ReadDictumResult();
        byte[] bytesInputFile = IOUtils.toByteArray(inputStreamFile);

        try(Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bytesInputFile))){
            if(wb instanceof XSSFWorkbook){

                try(XSSFWorkbook wbXLSX = (XSSFWorkbook)wb){
                    result.setListFields(this.readFields(wbXLSX));
                }

            } else {
                throw new ApplicationException("El archivo no cuenta con el formato XLSX correspondiente.");
            }

        } catch(IOException | EncryptedDocumentException | OldExcelFormatException pe){
            LOGGER.log(Level.SEVERE, StringUtils.EMPTY, pe);
            throw new ApplicationException("El archivo no cuenta con el formato XLSX correspondiente.");
        }
        
        return result;

    }

    protected abstract List<String> readFields(XSSFWorkbook wbXLSX);

}
