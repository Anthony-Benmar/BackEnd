package com.bbva.dao;

import java.util.List;
import java.util.logging.Logger;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.bbva.database.mappers.VisaSourcesMapper;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterDataDtoResponse;
import com.bbva.dto.visa_sources.request.ApproveVisaSourceDtoRequest;
import com.bbva.dto.visa_sources.request.RegisterVisaSourceDtoRequest;
import com.bbva.dto.visa_sources.request.VisaSourcePaginationDtoRequest;
import com.bbva.dto.visa_sources.response.VisaSourceApproveDtoResponse;
import com.bbva.dto.visa_sources.response.VisaSourceValidateExistDtoResponse;
import com.bbva.dto.visa_sources.response.VisaSourcesDataDtoResponse;
import com.bbva.entities.InsertEntity;
import com.bbva.entities.UpdateEntity;
import com.bbva.util.JSONUtils;

public class VisaSourcesDao {
    private static final Logger log = Logger.getLogger(VisaSourcesDao.class.getName());
    private final SqlSessionFactory sqlSessionFactory;

    public VisaSourcesDao(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public List<VisaSourcesDataDtoResponse> getVisaSources(VisaSourcePaginationDtoRequest dto) {
        List<VisaSourcesDataDtoResponse> result = null;
        try(SqlSession session = sqlSessionFactory.openSession()) {
            VisaSourcesMapper mapper = session.getMapper(VisaSourcesMapper.class);
            result = mapper.getVisaSourceWithFilters(dto);
        } catch (Exception e) {
            log.info("Error en getVisaSource: " + e.getMessage());
        }
        return result;
    }
    public int getVisaSourcesTotalCount(VisaSourcePaginationDtoRequest dto) {
        int totalCount = 0;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            VisaSourcesMapper mapper = session.getMapper(VisaSourcesMapper.class);
            totalCount = mapper.getVisaSourcesTotalCountWithFilters(dto);
        } catch (Exception e) {
            log.info("Error en getVisaSourcesTotalCount: "+ e.getMessage());
        }
        return totalCount;
    }
    public InsertEntity registerVisaSource(RegisterVisaSourceDtoRequest dto) {
        InsertEntity result = new InsertEntity();
        try(SqlSession session = sqlSessionFactory.openSession()) {
            VisaSourcesMapper mapper = session.getMapper(VisaSourcesMapper.class);
            result = mapper.insertVisaSourceEntity(dto);
        } catch (Exception e) {
            log.info("Error en registerVisaSources: " + e.getMessage());
        }
        return result;
    }
    public UpdateEntity updateVisaSource(RegisterVisaSourceDtoRequest dto) {
        UpdateEntity result = new UpdateEntity();
        try(SqlSession session = sqlSessionFactory.openSession()) {
            VisaSourcesMapper mapper = session.getMapper(VisaSourcesMapper.class);
            result = mapper.updateVisaSourceEntity(dto);
        } catch (Exception e) {
            log.info("Error en updateVisaSource: " + e.getMessage());
        }
        return result;
    }
    public VisaSourceApproveDtoResponse approveVisaSource(ApproveVisaSourceDtoRequest dto) {
        VisaSourceApproveDtoResponse result = new VisaSourceApproveDtoResponse();
        try(SqlSession session = sqlSessionFactory.openSession()) {
            VisaSourcesMapper mapper = session.getMapper(VisaSourcesMapper.class);
            result = mapper.approveVisaSource(dto);
        } catch (Exception e){
            log.info("Error en approveVisaSource : " + JSONUtils.convertFromObjectToJson(e.getMessage()));
        }
        return result;
    }
    public VisaSourceValidateExistDtoResponse validateSourceIds(String ids) {
        VisaSourceValidateExistDtoResponse response = new VisaSourceValidateExistDtoResponse();
        try(SqlSession session = sqlSessionFactory.openSession()) {
            VisaSourcesMapper mapper = session.getMapper(VisaSourcesMapper.class);
            List<SourceWithParameterDataDtoResponse> result = mapper.validateSourceIds(ids);
            String[] listIds = ids.split("\\s*,\\s*");
            response.setValidated(result.size() == listIds.length);
            if(listIds.length > 1) {
                response.setMultipleValidation(true);
            } else {
                response.setMultipleValidation(false);
                response.setReplacementId(result.get(0).getReplacementId());
            }
            return response;
        } catch (Exception e) {
            log.info("Error en validateSourceIds: " + JSONUtils.convertFromObjectToJson(e.getMessage()));
        }
        return response;
    }
}
