package com.bbva.dao;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.PgcConsultMapper;
import com.bbva.dto.pgc.response.PgcDocumentListItem;
import com.bbva.dto.pgc.response.PgcConceptLisItem;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PgcConsultDao {

    private static final Logger log = Logger.getLogger(PgcConsultDao.class.getName());

    // CONSULT-PGC
    public List<PgcConceptLisItem> getConceptsByDocumentForList(int documentId) {
        SqlSessionFactory factory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = factory.openSession()) {
            return session.getMapper(PgcConsultMapper.class)
                    .getConceptsByDocument(documentId);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error listing concepts by documentId via SP", e);
            throw e;
        }
    }

    // CONSULT-PGC
    public List<PgcDocumentListItem> getProcessedDocumentsForList() {
        SqlSessionFactory factory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = factory.openSession()) {
            return session.getMapper(PgcConsultMapper.class)
                    .getProcessedDocumentsForList();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error listing processed documents via SP", e);
            throw e;
        }
    }

}
