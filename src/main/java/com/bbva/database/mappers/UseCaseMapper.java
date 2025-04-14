package com.bbva.database.mappers;

import com.bbva.entities.usecase.UseCaseEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UseCaseMapper {
    @Select({"<script>" +
            "SELECT use_case_id as useCaseId,use_case_name as useCaseName,use_case_description as useCaseDescription FROM use_case " +
            "where use_case_id != 9999 " +
            "</script>"})
    List<UseCaseEntity> listAllUseCases();
}
