package com.bbva.entities.dictionary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class TemplateEntity {
    
    private Integer templateId;
    private String type;
    private String version;
    private Boolean current;
    private String fileName;

}
