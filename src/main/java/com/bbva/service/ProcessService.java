package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.*;
import com.bbva.dto.map_dependency.request.MapDependencyDTORequest;
import com.bbva.dto.map_dependency.response.MapDependencyListByProjectResponse;
import com.bbva.entities.map_dependecy.MapDependencyEntity;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcessService {
    private final MapDependencyDao mapDependencyDao = new MapDependencyDao();
    private static final Logger log = Logger.getLogger(ProcessService.class.getName());

    public IDataResult<MapDependencyListByProjectResponse> insert(MapDependencyDTORequest dto) {
        try {
            UseCaseDefinitionDao useCaseDefinitionDao = new UseCaseDefinitionDao();
            var listaUseCase = useCaseDefinitionDao.listForProjectID(dto.getProjectId());

            if (!listaUseCase.isEmpty()) {

                int useCaseDefinitionId = listaUseCase.get(0).getUseCaseId();

                MapDependencyEntity mapDependency = new MapDependencyEntity(
                        0,
                        useCaseDefinitionId,
                        dto.getKeyDataProcessType(),
                        dto.getProcessName(),
                        dto.getSloOwnerId(),
                        dto.getArisCode(),
                        dto.getDependencyMapLink()
                );

                var result = mapDependencyDao.insert(mapDependency);

                if (result.success) {
                    return new SuccessDataResult(null);
                } else {
                    return new ErrorDataResult(null,"500", result.message);
                }
            }
            else {
                return new ErrorDataResult(null,"500", "Lista de casos de uso vacia.");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult( e.getMessage());
        }
    }

    public IDataResult<MapDependencyListByProjectResponse> update(MapDependencyDTORequest dto) {
        try {
            UseCaseDefinitionDao useCaseDefinitionDao = new UseCaseDefinitionDao();
            var listaUseCase = useCaseDefinitionDao.listForProjectID(dto.getProjectId());

            if (!listaUseCase.isEmpty()) {

                int useCaseDefinitionId = listaUseCase.get(0).getUseCaseId();

                MapDependencyEntity mapDependency = new MapDependencyEntity(
                        dto.getMapDependencyId(),
                        useCaseDefinitionId,
                        dto.getKeyDataProcessType(),
                        dto.getProcessName(),
                        dto.getSloOwnerId(),
                        dto.getArisCode(),
                        dto.getDependencyMapLink()
                );

                var result = mapDependencyDao.update(mapDependency);

                if (result.success) {
                    return new SuccessDataResult(null);
                } else {
                    return new ErrorDataResult(null,"500", result.message);
                }
            }
            else {
                return new ErrorDataResult(null,"500", "Lista de casos de uso vacia.");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult( e.getMessage());
        }
    }
}