package com.bbva.dto.pgc.response;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PgcDocumentListItem {
    private Integer id;
    private String domainName;
    private String sdatool;
    private String projectName;
    private Date uploadedAt;
    private Date modificationDate;
    private String qRegistro;
}