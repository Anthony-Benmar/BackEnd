package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.DataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.GovernmentDao;
import com.bbva.dto.government.request.*;
import com.bbva.dto.government.response.FilterSourceResponseDTO;
import com.bbva.dto.government.response.SourceDefinitionDTOResponse;
import com.bbva.entities.InsertEntity;
import com.bbva.entities.government.SourceConceptEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GovernmentService {

    private final GovernmentDao governmentDao = new GovernmentDao();
    private static final Logger log = Logger.getLogger(GovernmentService.class.getName());


    public IDataResult<FilterSourceResponseDTO> filterSource(FilterSourceRequestDTO dto) {
        var result = governmentDao.filterSource(dto);
        return new SuccessDataResult(result);
    }

    public IDataResult<SourceDefinitionDTOResponse> getSourceById(int sourceId) {
        var result = governmentDao.getSourceById(sourceId);
        return new SuccessDataResult(result, "Detalle fuentes y concepto por proyecto.");
    }

    public IDataResult<List<SourceConceptEntity>> listSourceConceptEntity(int sourceId) {
        var result = governmentDao.listSourceConceptEntity(sourceId);
        return new SuccessDataResult(result, "Listado de conceptos por fuente.");
    }


    public IDataResult<InsertSourceRequestDTO> insertSourceDef(InsertSourceRequestDTO sourceDef) {
        try {
            var resultSource = governmentDao.insertSourceDef(sourceDef);

            if (resultSource.success && resultSource.data.getNew_register() == 1) {
                int sourceId = resultSource.data.getLast_insert_id();
                sourceDef.setUc_source_id(sourceId);

                List<DataResult<InsertEntity>> results = new ArrayList<>();
                for (InsertConceptRequestDTO sourceConcept : sourceDef.getConcepts()) {
                    sourceConcept.setUc_source_id(sourceId);
                    var resultConcept = governmentDao.insertConcept(sourceConcept);
                    results.add(resultConcept);
                    if (!resultConcept.success) {
                        break;
                    }
                }

                if (results.stream().filter(x -> !x.success).count() > 0) {
                    return new ErrorDataResult<>(null, "500", "Error al insertar los conceptos.");
                }
            } else {
                return new ErrorDataResult(null,"500", "Error al insertar las fuentes.");
            }
            return new SuccessDataResult(sourceDef);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null,"500", e.getMessage());
        }
    }

    public IDataResult<InsertConceptRequestDTO> insertConcept(Integer ucSourceId,
                                                              InsertConceptRequestDTO dto) {
        try {
            dto.setUc_source_id(ucSourceId);
            var resultSource = governmentDao.insertConcept(dto);
            if (resultSource.success && resultSource.data.getNew_register() == 1) {
                dto.setUc_data_id(resultSource.data.getLast_insert_id());
                return new SuccessDataResult(dto);
            } else {
                return new ErrorDataResult(null,"500", "Error al insertar el concepto.");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null,"500", e.getMessage());
        }
    }

    public IDataResult<UpdateSourceRequestDTO> updateSourceDef(
            int ucSourceId, UpdateSourceRequestDTO sourceDef) {
        try {
            sourceDef.setUc_source_id(ucSourceId);
            var result = governmentDao.updateSourceDef(sourceDef);
            if (result.success) {
                return new SuccessDataResult(sourceDef);
            } else {
                return new ErrorDataResult(null,"500", "Error al actualizar una fuente.");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(e.getMessage(),"500", e.getMessage());
        }
    }

    public IDataResult<UpdateSourceRequestDTO> updateConcept(
            int ucSourceId, int ucDataId, UpdateConceptRequestDTO concept) {
        try {
            concept.setUc_source_id(ucSourceId);
            concept.setUc_data_id(ucDataId);

            var result = governmentDao.updateConcept(concept);
            if (result.success) {
                return new SuccessDataResult(concept);
            } else {
                return new ErrorDataResult(null,"500", "Error al actualizar el concepto.");
            }
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

    public IDataResult<SourceConceptEntity> deleteSource(int uc_source_id)
            throws ExecutionException, InterruptedException {
        try {
            var res = governmentDao.deleteSource(uc_source_id);

            if (res.success) {
                return new SuccessDataResult(uc_source_id);
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
