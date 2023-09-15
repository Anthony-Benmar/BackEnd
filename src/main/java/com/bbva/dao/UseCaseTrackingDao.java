package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.BatchMapper;
import com.bbva.database.mappers.UseCaseTrackingMapper;
import com.bbva.dto.batch.request.JobExecutionFilterRequestDTO;
import com.bbva.dto.batch.response.JobExecutionFilterData;
import com.bbva.dto.batch.response.JobExecutionFilterResponseDTO;
import com.bbva.dto.usecasetracking.GetUseCaseTrackingJobsRequestDto;
import com.bbva.dto.usecasetracking.GetUseTrackingJobsSPResponse;
import com.bbva.dto.usecasetracking.UseCaseDto;
import com.bbva.util.JSONUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UseCaseTrackingDao {
    private static final Logger log = Logger.getLogger(UseCaseTrackingDao.class.getName());

    public List<GetUseTrackingJobsSPResponse> getUseCaseTrackingJobs(GetUseCaseTrackingJobsRequestDto dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<GetUseTrackingJobsSPResponse> lista;

        try (SqlSession session = sqlSessionFactory.openSession()) {
            UseCaseTrackingMapper mapper = session.getMapper(UseCaseTrackingMapper.class);
            lista = mapper.getUseCaseTrackingJobs(
                    dto.getPage(),
                    dto.getRecords_amount(),
                    dto.getUseCaseId(),
                    dto.getOrderDate()
            );
        }
        return lista;
    }

    public List<UseCaseDto> getUseCaseByDomainId(Integer domainId) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UseCaseTrackingMapper mapper = session.getMapper(UseCaseTrackingMapper.class);
            return mapper.getUseCaseByDomainId(domainId);
        }
        catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }
}
