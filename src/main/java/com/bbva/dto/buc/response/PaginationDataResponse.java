package com.bbva.dto.buc.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginationDataResponse {
    private Integer id;
    private Integer folioId;
    private String folioCodigo;
    private Integer proyectoId;
    private String sdatool;
    private String proyectoNombre;
    private Integer casoUsoId;
    private String casoUsoCodigo;
    private String casoUsoNombre;
    private Integer tipoEstadoId;
    private String tipoEstadoNombre;
    private List<PaginationBucFuenteCampoResponse> bucFuenteCampo;
}
