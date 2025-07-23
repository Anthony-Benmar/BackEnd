package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.SourceWithParameterDao;
import com.bbva.dto.source_with_parameter.request.SourceWithParameterPaginationDtoRequest;
import com.bbva.dto.source_with_parameter.request.SourceWithReadyOnlyDtoRequest;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterPaginatedResponseDTO;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterDataDtoResponse;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterReadOnlyDtoResponse;

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
        // Implementa la lógica de detalle si la necesitas
        SourceWithParameterDataDtoResponse data = sourceWithParameterDao.getSourceWithParameterById(request.getSourceWithParameterId());
        SourceWithParameterReadOnlyDtoResponse response = new SourceWithParameterReadOnlyDtoResponse();
        // Map fields if data is not null
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
    // Métodos para combos
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
}
