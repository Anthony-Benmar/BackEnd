package com.bbva.dao;

import com.bbva.core.results.DataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.BatchMapper;
import com.bbva.dto.batch.request.InsertReliabilityIncidenceDTO;
import com.bbva.dto.batch.request.JobExecutionFilterRequestDTO;
import com.bbva.dto.batch.response.JobExecutionFilterData;
import com.bbva.dto.batch.response.JobExecutionFilterResponseDTO;
import com.bbva.util.JSONUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BatchDao {

    private static final Logger log = Logger.getLogger(BatchDao.class.getName());

    public JobExecutionFilterResponseDTO filter(JobExecutionFilterRequestDTO dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<JobExecutionFilterData> lista;

        Integer recordsCount = 0;
        Integer pagesAmount = 0;

        JobExecutionFilterResponseDTO response = new JobExecutionFilterResponseDTO();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            BatchMapper mapper = session.getMapper(BatchMapper.class);
            lista = mapper.filter(
                    dto.getPage(),
                    dto.getRecords_amount(),
                    dto.getJobName(),
                    dto.getStartDate(),
                    dto.getEndDate(),
                    dto.getFolder(),
                    dto.getDataproc(),
                    dto.getOrderId(),
                    dto.getProjectName(),
                    dto.getSdatoolId(),
                    dto.getDomain()
            );
        }
        log.info(JSONUtils.convertFromObjectToJson(response.getData()));
        recordsCount = (lista.size() > 0) ? lista.get(0).getRecordsCount() : 0;
        pagesAmount = dto.getRecords_amount() > 0 ? (int) Math.ceil(recordsCount.floatValue() / dto.getRecords_amount().floatValue()) : 1;

        response.setCount(recordsCount);
        response.setPages_amount(pagesAmount);
        response.setData(lista);
        return response;
    }

    public DataResult<JobExecutionFilterRequestDTO> insertReliabilityIncidence(InsertReliabilityIncidenceDTO dto) {
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                BatchMapper batchMapper = session.getMapper(BatchMapper.class);
                batchMapper.insertReliabilityIncidence(dto);
                session.commit();
                return new SuccessDataResult(dto);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, "500",e.getMessage());
        }
    }
}
