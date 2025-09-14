package com.bbva.dto.pgc.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CommitRequestDto {
    private String preUploadId;
    private List<PreUploadRowUpdateRequestDto> rows;
}