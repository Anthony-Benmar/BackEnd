package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.GovernmentDao;
import com.bbva.dto.government.response.SourceDefinitionDTOResponse;
import com.bbva.entities.government.SourceConceptEntity;
import com.bbva.entities.government.SourceDefinitionEntity;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GovernmentService {

    private final GovernmentDao governmentDao = new GovernmentDao();
    private static final Logger log = Logger.getLogger(GovernmentService.class.getName());


    public IDataResult<List<SourceDefinitionDTOResponse>> listSourceDefinition(int projectId) {
        var result = governmentDao.listSourceDefinition(projectId);
        return new SuccessDataResult(result, "Listado de fuentes por proyecto.");
    }

    public IDataResult<SourceDefinitionDTOResponse> getSourceById(int sourceId) {
        var result = governmentDao.getSourceById(sourceId);
        return new SuccessDataResult(result, "Detalle fuentes y concepto por proyecto.");
    }

    public IDataResult<List<SourceConceptEntity>> listSourceConceptEntity(int sourceId) {
        var result = governmentDao.listSourceConceptEntity(sourceId);
        return new SuccessDataResult(result, "Listado de conceptos por fuente.");
    }


    public IDataResult<SourceDefinitionEntity> insertSourceDef(SourceDefinitionEntity sourceDef) {
        try {
            var resultSource = governmentDao.insertSourceDef(sourceDef);
            if (resultSource.success) {
                int sourceId = sourceDef.getUc_source_id();
                sourceDef.getSourceConceptEntityList().forEach(item -> {
                    SourceConceptEntity sourceConcept = item;
                    sourceConcept.setUc_source_id(sourceId);
                    governmentDao.insertConcept(sourceConcept);
                });
            } else {
                return new ErrorDataResult(null,"500", "Error");
            }
            return new SuccessDataResult(sourceDef);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null,"500", e.getMessage());
        }
    }

    public IDataResult<SourceDefinitionEntity> updateSourceDef(SourceDefinitionEntity sourceDef)
            throws ExecutionException, InterruptedException {

        try {
            if (sourceDef.getUc_source_id() == 0) {
                return new ErrorDataResult(null,"500", "SourceDef must to be not null");
            }
            var result = governmentDao.updateSourceDef(sourceDef);

            if (result.success) {
                int ucSourceId = sourceDef.getUc_source_id();
                sourceDef.getSourceConceptEntityList().forEach(item -> {
                    SourceConceptEntity concept = item;
                    concept.setUc_source_id(ucSourceId);
                    if (item.getUc_data_id().equals(0)) {
                        governmentDao.insertConcept(concept);
                    } else {
                        governmentDao.updateConcept(concept);
                    }
                });
            } else {
                return new ErrorDataResult(null,"500", result.message);
            }

            return new SuccessDataResult(sourceDef);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(e.getMessage(),"500", e.getMessage());
        }
    }

    public IDataResult<SourceConceptEntity> deleteConcept(int uc_data_id)
            throws ExecutionException, InterruptedException {
        try {
            var res = governmentDao.deleteConcept(uc_data_id);

            if (res.success) {
                return new SuccessDataResult(uc_data_id);
            } else {
                return new ErrorDataResult(null,"500", res.message);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null,"500", e.getMessage());
        }
    }

    public IDataResult<SourceConceptEntity> deleteMapDependency(int dependencyId) {
        try {
            var res = governmentDao.deleteMapDependency(dependencyId);

            if (res.success) {
                return new SuccessDataResult(dependencyId);
            } else {
                return new ErrorDataResult(null,"500", res.message);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null,"500", e.getMessage());
        }
    }

}
