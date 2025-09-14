package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.ConsultPgcMapper;
import com.bbva.dto.pgc.response.PgcDocumentLisItem;
import com.bbva.dto.pgc.response.PgcConceptLisItem;
import com.bbva.entities.pgc.PgcConcept;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PgcConsultDao {
    private static final Logger log = Logger.getLogger(PgcConsultDao.class.getName());

    // CONSULT-PGC
    public List<PgcDocumentLisItem> getProcessedDocumentsForList() {
        SqlSessionFactory factory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = factory.openSession()) {
            return session.getMapper(ConsultPgcMapper.class)
                    .getProcessedDocumentsForList();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error listing processed documents via SP", e);
            throw e;
        }
    }

    // CONSULT-PGC
    public List<PgcConceptLisItem> getConceptsByDocumentForList(int documentId) {
        SqlSessionFactory factory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = factory.openSession()) {
            return session.getMapper(ConsultPgcMapper.class)
                    .getConceptsByDocument(documentId);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error listing concepts by documentId via SP", e);
            throw e;
        }
    }

    /*
    public void updatePgcConcept(PgcConcept concept) {
        SqlSessionFactory factory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = factory.openSession()) {
            ConsultPgcMapper mapper = session.getMapper(ConsultPgcMapper.class);
            mapper.updatePgcConcept(concept);
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException( "Error al actualizar el PgcConcept" + e.getMessage());
        }
    }*/
}
