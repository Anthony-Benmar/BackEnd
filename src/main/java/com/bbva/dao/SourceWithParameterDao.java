package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.SourceWithParameterMapper;
import com.bbva.dto.source_with_parameter.request.SourceWithParameterPaginationDtoRequest;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterDataDtoResponse;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SourceWithParameterDao {
    private static final Logger log = Logger.getLogger(SourceWithParameterDao.class.getName());
    private final SqlSessionFactory sqlSessionFactory;

    public SourceWithParameterDao(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public List<SourceWithParameterDataDtoResponse> getSourceWithParameter(SourceWithParameterPaginationDtoRequest dto){
        List<SourceWithParameterDataDtoResponse> result = null;
        try(SqlSession session = sqlSessionFactory.openSession()) {
            SourceWithParameterMapper mapper = session.getMapper(SourceWithParameterMapper.class);
            result = mapper.getSourcesWithParameterWithFilters(dto);
        }catch (Exception e) {
            log.info("Error en getSourceWithParameter: "+ e.getMessage());
        }
        return result;
    }
    public int getSourceWithParameterTotalCount(SourceWithParameterPaginationDtoRequest dto) {
        int totalCount = 0;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SourceWithParameterMapper mapper = session.getMapper(SourceWithParameterMapper.class);
            totalCount = mapper.getSourcesWithParameterTotalCountWithFilters(dto);
        } catch (Exception e) {
            log.info("Error en getSourceWithParameterTotalCount: "+ e.getMessage());
        }
        return totalCount;
    }
    public SourceWithParameterDataDtoResponse getSourceWithParameterById(String singleId){
        SourceWithParameterDataDtoResponse result = null;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SourceWithParameterMapper mapper = session.getMapper(SourceWithParameterMapper.class);
            result = mapper.getSourceWithParameterById(singleId);
        } catch (Exception e) {
            log.info("Error en getSourceWithParameterById: "+ e.getMessage());
            return null;
        }
        return result;
    }
    public List<String> getDistinctStatuses() {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(SourceWithParameterMapper.class).getStatus();
        }
    }
    public List<String> getDistinctOriginTypes() {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(SourceWithParameterMapper.class).getOriginType();
        }
    }
    public List<String> getDistinctTdsOpinionDebts() {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(SourceWithParameterMapper.class).getTdsOpinionDebt();
        }
    }
    public List<String> getDistinctEffectivenessDebts() {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(SourceWithParameterMapper.class).getEffectivenessDebt();
        }
    }
    public boolean update(SourceWithParameterDataDtoResponse dto) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SourceWithParameterMapper mapper = session.getMapper(SourceWithParameterMapper.class);
            int rows = mapper.updateSource(dto);
            session.commit();
            return rows > 0;
        } catch (Exception e) {
            log.severe("Error en update: " + e.getMessage());
            return false;
        }
    }
    public List<String> getCommentsBySourceIdAndType(String sourceId, String commentType) {
        List<String> comments = new ArrayList<>();
        String column;

        if ("Deuda de dictamen".equalsIgnoreCase(commentType)) {
            column = "opinion_debt_comments";
        } else if ("Sustento TDS".equalsIgnoreCase(commentType)) {
            column = "tds_comments";
        } else {
            throw new IllegalArgumentException("Tipo de comentario inválido: " + commentType);
        }

        String sqlCommentSources = "SELECT " + column + " FROM comment_sources WHERE id_sources = ?";
        String sqlSource = "SELECT " + column + " FROM sources WHERE id = ?";


        try (SqlSession session = sqlSessionFactory.openSession();
             java.sql.Connection conn = session.getConnection()) {

            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlCommentSources)) {
                ps.setString(1, sourceId);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String comment = rs.getString(1);
                        comments.add(comment);

                    }
                }
            }

            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlSource)) {
                ps.setString(1, sourceId);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String comment = rs.getString(1);
                        comments.add(comment);

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return comments;
    }



    public void saveCommentBySourceIdAndType(String sourceId, String commentType, String comment) {
        boolean success = false;
        String column;

        if ("Deuda de dictamen".equalsIgnoreCase(commentType)) {
            column = "opinion_debt_comments";
        } else if ("Sustento TDS".equalsIgnoreCase(commentType)) {
            column = "tds_comments";
        } else {
            throw new IllegalArgumentException("Tipo de comentario inválido: " + commentType);
        }

        String sql = "INSERT INTO comment_sources (id_sources, " + column + ") VALUES (?, ?)";

        try (SqlSession session = sqlSessionFactory.openSession()) {
            java.sql.Connection conn = session.getConnection();

            try (java.sql.PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, sourceId);
                ps.setString(2, comment);

                int rows = ps.executeUpdate();
                session.commit();
                success = rows > 0;

                try (java.sql.ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long generatedId = generatedKeys.getLong(1);

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void insertModifyHistory(SourceWithParameterDataDtoResponse dto) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            java.sql.Connection conn = session.getConnection();

            String selectSQL = "SELECT * FROM sources WHERE id = ?";
            Map<String, Object> oldValues = new HashMap<>();
            try (PreparedStatement ps = conn.prepareStatement(selectSQL)) {
                ps.setString(1, dto.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ResultSetMetaData meta = rs.getMetaData();
                        int colCount = meta.getColumnCount();

                        for (int i = 1; i <= colCount; i++) {
                            String colName = meta.getColumnName(i);
                            Object colVal = rs.getObject(i);
                            oldValues.put(colName.toUpperCase(), colVal);
                        }
                    }
                }
            }

            Map<String, String> newValues = new LinkedHashMap<>();
            newValues.put("tds_Description", dto.getTdsDescription());
            newValues.put("tds_Source", dto.getTdsSource());
            newValues.put("source_Origin", dto.getSourceOrigin());
            newValues.put("origin_Type", dto.getOriginType());
            newValues.put("status", dto.getStatus());
            newValues.put("replacement_Id", dto.getReplacementId());
            newValues.put("model_Owner", dto.getModelOwner());
            newValues.put("master_Registered_Board", dto.getMasterRegisteredBoard());
            newValues.put("dataLake_Layer", dto.getDataLakeLayer());
            newValues.put("uuaa_Raw", dto.getUuaaRaw());
            newValues.put("uuaa_Master", dto.getUuaaMaster());
            newValues.put("tds_Opinion_Debt", dto.getTdsOpinionDebt());
            newValues.put("debt_Level", dto.getDebtLevel());
            newValues.put("inherited_Source_Id", dto.getInheritedSourceId());
            newValues.put("opinion_Debt_Comments", dto.getOpinionDebtComments());
            newValues.put("missing_Certification", dto.getMissingCertification());
            newValues.put("missing_Field_Profiling", dto.getMissingFieldProfiling());
            newValues.put("incomplete_Opinion", dto.getIncompleteOpinion());
            newValues.put("pdco_Processing_Use", dto.getPdcoProcessingUse());
            newValues.put("effectiveness_Debt", dto.getEffectivenessDebt());
            newValues.put("ingestion_Type", dto.getIngestionType());
            newValues.put("ingestion_Layer", dto.getIngestionLayer());
            newValues.put("datio_Download_Type", dto.getDatioDownloadType());
            newValues.put("processing_Input_Table_Ids", dto.getProcessingInputTableIds());
            newValues.put("periodicity", dto.getPeriodicity());
            newValues.put("periodicity_Detail", dto.getPeriodicityDetail());
            newValues.put("folder_Url", dto.getFolderUrl());
            newValues.put("typology", dto.getTypology());
            newValues.put("critical_Table", dto.getCriticalTable());
            newValues.put("critical_Table_Owner", dto.getCriticalTableOwner());
            newValues.put("l1t", dto.getL1t());
            newValues.put("hem", dto.getHem());
            newValues.put("his", dto.getHis());
            newValues.put("err", dto.getErr());
            newValues.put("log", dto.getLog());
            newValues.put("mlg", dto.getMlg());
            newValues.put("quality", dto.getQuality());
            newValues.put("tag1", dto.getTag1());
            newValues.put("tag2", dto.getTag2());
            newValues.put("tag3", dto.getTag3());
            newValues.put("tag4", dto.getTag4());
            newValues.put("raw_Path", dto.getRawPath());

            String insertSQL = "INSERT INTO sidedb.modify_source_history " +
                    "(source_id, user_id, user_name, field_name, old_value, new_value, date) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(insertSQL)) {
                for (Map.Entry<String, String> entry : newValues.entrySet()) {
                    String field = entry.getKey();
                    String newVal = entry.getValue();
                    Object oldVal = oldValues.get(field.toUpperCase());

                    String oldValStr = (oldVal == null ? null : oldVal.toString());

                    if (!Objects.equals(oldValStr, newVal)) {


                        ps.setString(1, dto.getId());
                        ps.setString(2, dto.getUserId());
                        ps.setString(3, dto.getUserName());
                        ps.setString(4, field);
                        ps.setString(5, oldValStr);
                        ps.setString(6, newVal);
                        ps.setTimestamp(7, new java.sql.Timestamp(System.currentTimeMillis()));
                        ps.addBatch();
                    }
                }
                ps.executeBatch();
            }

            session.commit();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean insert(SourceWithParameterDataDtoResponse dto) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SourceWithParameterMapper mapper = session.getMapper(SourceWithParameterMapper.class);
            int rows = mapper.insertSource(dto);
            boolean success = rows > 0;
            if (success && dto.getReplacementId() != null && !dto.getReplacementId().isEmpty()) {
                String oldReplacementIds = mapper.getReplacementIds(dto.getReplacementId());
                String newReplacementIds = (oldReplacementIds == null || oldReplacementIds.isEmpty())
                        ? dto.getId()
                        : oldReplacementIds + "," + dto.getId();

                mapper.updateReplacementId(newReplacementIds, dto.getReplacementId());
            }
            session.commit();
            return success;
        }
    }
    public String getMaxSourceId() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SourceWithParameterMapper mapper = session.getMapper(SourceWithParameterMapper.class);
            return mapper.getMaxSourceId();
        }
    }
    public boolean existsReplacementId(String replacementId) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SourceWithParameterMapper mapper = session.getMapper(SourceWithParameterMapper.class);
            return mapper.countById(replacementId) > 0;
        }
    }
    public String getStatusById(String sourceId) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SourceWithParameterMapper mapper = session.getMapper(SourceWithParameterMapper.class);
            return mapper.getStatusById(sourceId);
        }
    }
}
