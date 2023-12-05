package com.bbva.dto.catalog.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
public class ElementsDto {
    private Integer id;
    private String name;
    private String description;
}
