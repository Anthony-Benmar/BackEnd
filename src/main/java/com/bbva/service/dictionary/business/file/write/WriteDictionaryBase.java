package com.bbva.service.dictionary.business.file.write;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.bbva.dao.ProjectDao;
import com.bbva.entities.dictionary.GenerationEntity;
import com.bbva.entities.dictionary.GenerationFieldEntity;
import com.bbva.entities.dictionary.TemplateEntity;
import com.bbva.enums.dictionary.FolderType;
import com.bbva.util.enums.FileFormatType;
import com.bbva.util.exception.ApplicationException;
import com.bbva.util.types.FechaUtil;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class WriteDictionaryBase {
    
    private static final Logger LOGGER = Logger.getLogger(WriteDictionaryBase.class.getName());

    @Setter @Getter(value = AccessLevel.PROTECTED) protected TemplateEntity templateEntity;
    @Setter @Getter(value = AccessLevel.PROTECTED) protected GenerationEntity generationEntity;
    @Setter @Getter(value = AccessLevel.PROTECTED) protected List<GenerationFieldEntity> listaGenerationFieldEntity;

    public WriteDictionaryResult process() throws IOException{
        
        String nombreProyecto = new ProjectDao().projectById(generationEntity.getProjectId()).getProjectName();

        WriteDictionaryResult result = new WriteDictionaryResult();
        result.setDictionaryLogicalFileName(String.format("%s - %s - %s.%s", nombreProyecto, 
                                                                                    generationEntity.getSourceId(), 
                                                                                    generationEntity.getSourceName(), 
                                                                                    FileFormatType.XLSX.getExtension()));

                  
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        byte[] bytesPlantillaDiccionario = IOUtils.toByteArray(loader.getResourceAsStream(String.format("templates/%s", templateEntity.getFileName())));

        try(XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytesPlantillaDiccionario));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();){
            this.writeContent(workbook);
            workbook.write(baos);
            result.setBytesDictionary(baos.toByteArray());
        }catch(IOException | EncryptedDocumentException | OldExcelFormatException pe){
            LOGGER.log(Level.SEVERE, StringUtils.EMPTY, pe);
            throw new ApplicationException("El archivo no cuenta con el formato XLSX correspondiente.");
        }
               
        return result;

    }

    protected abstract void writeContent(XSSFWorkbook wbXLSX);

}
