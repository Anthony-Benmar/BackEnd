package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.SourceWithParameterDao;
import com.bbva.dto.source_with_parameter.request.SourceWithParameterPaginationDtoRequest;
import com.bbva.dto.source_with_parameter.request.SourceWithReadyOnlyDtoRequest;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterPaginatedResponseDTO;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterDataDtoResponse;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterReadOnlyDtoResponse;
import com.bbva.core.results.ErrorDataResult;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;

import static com.bbva.database.MyBatisConnectionFactory.getSqlSessionFactory;

public class SourceWithParameterService {
    private  final SourceWithParameterDao sourceWithParameterDao;

    public SourceWithParameterService(SourceWithParameterDao sourceWithParameterDao) {
        this.sourceWithParameterDao = sourceWithParameterDao;
    }
    public SourceWithParameterService() {
        this.sourceWithParameterDao = new SourceWithParameterDao(getSqlSessionFactory());
    }
    public IDataResult<SourceWithParameterPaginatedResponseDTO> getSourceWithParameter(SourceWithParameterPaginationDtoRequest dto) {
        List<SourceWithParameterDataDtoResponse> result = sourceWithParameterDao.getSourceWithParameter(dto);
        int totalCount = sourceWithParameterDao.getSourceWithParameterTotalCount(dto);
        SourceWithParameterPaginatedResponseDTO response = new SourceWithParameterPaginatedResponseDTO();
        response.setData(result);
        response.setTotalCount(totalCount);
        return new SuccessDataResult<>(response);
    }
    public IDataResult<SourceWithParameterReadOnlyDtoResponse> readOnly(SourceWithReadyOnlyDtoRequest request) {
        SourceWithParameterDataDtoResponse data = sourceWithParameterDao.getSourceWithParameterById(request.getSourceWithParameterId());
        SourceWithParameterReadOnlyDtoResponse response = new SourceWithParameterReadOnlyDtoResponse();
        if (data != null) {
            response.setId(data.getId());
            response.setTdsDescription(data.getTdsDescription());
            response.setTdsSource(data.getTdsSource());
            response.setSourceOrigin(data.getSourceOrigin());
            response.setOriginType(data.getOriginType());
            response.setStatus(data.getStatus());
            response.setReplacementId(data.getReplacementId());
            response.setModelOwner(data.getModelOwner());
            response.setMasterRegisteredBoard(data.getMasterRegisteredBoard());
            response.setDataLakeLayer(data.getDataLakeLayer());
            response.setUuaaRaw(data.getUuaaRaw());
            response.setUuaaMaster(data.getUuaaMaster());
            response.setTdsOpinionDebt(data.getTdsOpinionDebt());
            response.setDebtLevel(data.getDebtLevel());
            response.setInheritedSourceId(data.getInheritedSourceId());
            response.setOpinionDebtComments(data.getOpinionDebtComments());
            response.setMissingCertification(data.getMissingCertification());
            response.setMissingFieldProfiling(data.getMissingFieldProfiling());
            response.setIncompleteOpinion(data.getIncompleteOpinion());
            response.setPdcoProcessingUse(data.getPdcoProcessingUse());
            response.setEffectivenessDebt(data.getEffectivenessDebt());
            response.setIngestionType(data.getIngestionType());
            response.setIngestionLayer(data.getIngestionLayer());
            response.setDatioDownloadType(data.getDatioDownloadType());
            response.setProcessingInputTableIds(data.getProcessingInputTableIds());
            response.setPeriodicity(data.getPeriodicity());
            response.setPeriodicityDetail(data.getPeriodicityDetail());
            response.setFolderUrl(data.getFolderUrl());
            response.setTypology(data.getTypology());
            response.setCriticalTable(data.getCriticalTable());
            response.setCriticalTableOwner(data.getCriticalTableOwner());
            response.setL1t(data.getL1t());
            response.setHem(data.getHem());
            response.setHis(data.getHis());
            response.setErr(data.getErr());
            response.setLog(data.getLog());
            response.setMlg(data.getMlg());
            response.setQuality(data.getQuality());
            response.setTag1(data.getTag1());
            response.setTag2(data.getTag2());
            response.setTag3(data.getTag3());
            response.setTag4(data.getTag4());
            response.setRawPath(data.getRawPath());
        }
        return new SuccessDataResult<>(response);
    }
    public List<String> getDistinctStatuses() {
        return sourceWithParameterDao.getDistinctStatuses();
    }
    public List<String> getDistinctOriginTypes() {
        return sourceWithParameterDao.getDistinctOriginTypes();
    }
    public List<String> getDistinctTdsOpinionDebts() {
        return sourceWithParameterDao.getDistinctTdsOpinionDebts();
    }
    public List<String> getDistinctEffectivenessDebts() {
        return sourceWithParameterDao.getDistinctEffectivenessDebts();
    }

    public IDataResult<Boolean> updateSourceWithParameter(SourceWithParameterDataDtoResponse dto) {
        try {
            boolean success = sourceWithParameterDao.update(dto);
            if (success) {
                return new SuccessDataResult<>(true, "Exitoso");
            } else {
                return new ErrorDataResult<>(false, "500", "Error");
            }
        } catch (Exception e) {
            Logger log = Logger.getLogger(SourceWithParameterService.class.getName());
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(false, "500", e.getMessage());
        }
    }
    public List<String> exportCommentsBySourceId(String sourceId, String commentType) {
        if (sourceId == null || sourceId.isEmpty()) {
            return Collections.emptyList();
        }
        return sourceWithParameterDao.getCommentsBySourceIdAndType(sourceId, commentType);
    }

    public void saveComment(String sourceId, String commentType, String comment) {
        if (sourceId == null || sourceId.isEmpty()) {
            throw new IllegalArgumentException("El ID de la fuente no puede estar vacío");
        }
        if (comment == null || comment.isEmpty()) {
            throw new IllegalArgumentException("El comentario no puede estar vacío");
        }
        sourceWithParameterDao.saveCommentBySourceIdAndType(sourceId, commentType, comment);
    }

    public void saveModifyHistory(SourceWithParameterDataDtoResponse dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El objeto DTO no puede ser nulo");
        }
        if (dto.getId() == null || dto.getId().isEmpty()) {
            throw new IllegalArgumentException("El ID de la fuente no puede estar vacío");
        }
        if (dto.getUserId() == null || dto.getUserId().isEmpty()) {
            throw new IllegalArgumentException("El ID de usuario no puede estar vacío");
        }
        if (dto.getUserName() == null || dto.getUserName().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
        }

        sourceWithParameterDao.insertModifyHistory(dto);
    }

    public boolean insertSource(SourceWithParameterDataDtoResponse dto) {
        try {
            return sourceWithParameterDao.insert(dto);
        } catch (Exception e) {
            Logger log = Logger.getLogger(SourceWithParameterService.class.getName());
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    public String getMaxSourceId() {
        return sourceWithParameterDao.getMaxSourceId();
    }

    public boolean existsReplacementId(String replacementId) {
        try {
            return sourceWithParameterDao.existsReplacementId(replacementId);
        } catch (Exception e) {
            Logger log = Logger.getLogger(SourceWithParameterService.class.getName());
            log.log(Level.SEVERE, "Error verificando replacementId: " + e.getMessage(), e);
            return false;
        }
    }

    public String getStatusById(String sourceId) {
        return sourceWithParameterDao.getStatusById(sourceId);
    }
    
}
