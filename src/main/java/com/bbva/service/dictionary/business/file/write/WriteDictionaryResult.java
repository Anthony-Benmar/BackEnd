package com.bbva.service.dictionary.business.file.write;

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
public class WriteDictionaryResult {
    private String dictionaryLogicalFileName;
    private byte[] bytesDictionary;
}
