package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.PgcConsultDao;
import com.bbva.dto.pgc.response.PgcDocumentListItem;
import com.bbva.dto.pgc.response.PgcConceptLisItem;

import java.util.List;


public class PgcConsultService {

    private final PgcConsultDao dao = new PgcConsultDao();

    public IDataResult<List<PgcDocumentListItem>> getProcessedForList() {
        try {
            List<PgcDocumentListItem> list = dao.getProcessedDocumentsForList();
            return new SuccessDataResult<>(list);
        } catch (Exception e) {
            return new ErrorDataResult<>(null, "500", "Error al listar documentos procesados (SP): " + e.getMessage());
        }
    }

    public IDataResult<List<PgcConceptLisItem>> getListByDocumentId(int documentId) {
        try {
            List<PgcConceptLisItem> list = dao.getConceptsByDocumentForList(documentId);
            return new SuccessDataResult<>(list);
        } catch (Exception e) {
            return new ErrorDataResult<>(null, "500", "Error al listar conceptos (SP): " + e.getMessage());
        }
    }

}
