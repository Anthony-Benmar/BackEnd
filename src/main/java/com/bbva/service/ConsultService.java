package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.dao.PgcConsultDao;
import com.bbva.dto.pgc.response.PgcDocumentLisItem;
import com.bbva.dto.pgc.response.PgcConceptLisItem;
import com.bbva.entities.pgc.PgcDocument;
import com.bbva.entities.pgc.PgcConcept;

import java.util.List;


public class ConsultService {
    private final PgcConsultDao docDao = new PgcConsultDao();

    public IDataResult<List<PgcDocumentLisItem>> getProcessedForList() {
        try {
            List<PgcDocumentLisItem> list = docDao.getProcessedDocumentsForList();
            return new SuccessDataResult<>(list);
        } catch (Exception e) {
            return new ErrorDataResult<>(null, "500", "Error al listar documentos procesados (SP): " + e.getMessage());
        }
    }

    public IDataResult<List<PgcConceptLisItem>> getListByDocumentId(int documentId) {
        try {
            List<PgcConceptLisItem> list = docDao.getConceptsByDocumentForList(documentId);
            return new SuccessDataResult<>(list);
        } catch (Exception e) {
            return new ErrorDataResult<>(null, "500", "Error al listar conceptos (SP): " + e.getMessage());
        }
    }

}
