package com.bbva.service.dictionary.business.file.write;

import java.util.List;

import com.bbva.entities.dictionary.GenerationEntity;
import com.bbva.entities.dictionary.GenerationFieldEntity;
import com.bbva.entities.dictionary.TemplateEntity;
import com.bbva.enums.dictionary.TemplateVersionType;
import com.bbva.util.exception.ApplicationException;

public final class WriteDictionaryEngine {
    
    private WriteDictionaryEngine(){}

    public static WriteDictionaryBase getInstance(GenerationEntity generationEntity, 
                                                    List<GenerationFieldEntity> listaGenerationFieldEntity, 
                                                    TemplateEntity templateEntity){
        WriteDictionaryBase writeDictionaryBase;
        if(TemplateVersionType.V_01 == TemplateVersionType.get(templateEntity.getVersion())){
            writeDictionaryBase = new WriteDictionaryVersion01();
        }else{
            throw new ApplicationException("Opción no implementada");
        }
        writeDictionaryBase.setGenerationEntity(generationEntity);
        writeDictionaryBase.setListaGenerationFieldEntity(listaGenerationFieldEntity);
        writeDictionaryBase.setTemplateEntity(templateEntity);
        return writeDictionaryBase;
    }

}
