package com.bbva.dto.bui.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadOnlyDtoResponse {
    private ReadOnlyRoadmapDtoResponse roadmap;
    private ReadOnlyDictamenResponse dictamen;
    private ReadOnlyTdsDictaminadaResponse tdsDictaminada;
    private ReadOnlyIngestaResponse ingesta;
}
