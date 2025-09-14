package com.bbva.dto.pgc.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PreUploadRowUpdateRequestDto {
    private int rowIndex;
    private Map<String, String> rowData;
    private List<String> comments;
    private boolean valid;
    private boolean included;
    private String ModifiedBy;
    private Date ModifiedAt;
}