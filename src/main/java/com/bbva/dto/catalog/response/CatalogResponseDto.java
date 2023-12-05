package com.bbva.dto.catalog.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
public class CatalogResponseDto {
    private Integer id;
    private String name;
    private String description;
    private List<ElementsDto> elements;
}
