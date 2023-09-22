package com.bbva.service.dictionary.business.file.read;

import com.bbva.entities.dictionary.TemplateEntity;
import com.bbva.enums.dictionary.TemplateVersionType;
import com.bbva.util.exception.ApplicationException;

public final class ReadDictumEngine {
    
    private ReadDictumEngine(){}

    public static ReadDictumBase getInstance(TemplateEntity templateEntity){
        ReadDictumBase readDictumBase;
        if(TemplateVersionType.V_01 == TemplateVersionType.get(templateEntity.getVersion())){
            readDictumBase = new ReadDictumVersion01();
        }else{
            throw new ApplicationException("Opción no implementada");
        }
        return readDictumBase;
    }

}
