package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.JobMapper;
import com.bbva.dto.job.request.JobBasicInfoFilterDtoRequest;
import com.bbva.dto.job.request.JobDTO;
import com.bbva.dto.job.response.JobBasicInfoDtoResponse;
import com.bbva.dto.job.response.JobBasicInfoFilterDtoResponse;
import com.bbva.dto.job.response.JobBasicInfoSelectDtoResponse;
import com.bbva.util.JSONUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class JobDao {

    private static final Logger LOGGER = Logger.getLogger(JobDao.class.getName());
    private static JobDao instance = null;

    public static synchronized JobDao getInstance() {
        if (Objects.isNull(instance)) {
            instance = new JobDao();
        }
        return instance;
    }

    public List<JobBasicInfoDtoResponse> listAll() {
        List<JobBasicInfoDtoResponse> jobBasicInfoList = null;
        try{
            LOGGER.info("Listar JobBasicInfo en Mapper");
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                JobMapper mapper = session.getMapper(JobMapper.class);
                jobBasicInfoList = mapper.listAll();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return jobBasicInfoList;
    }
    public JobBasicInfoFilterDtoResponse jobBasicInfoFilter(JobBasicInfoFilterDtoRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<JobBasicInfoSelectDtoResponse> lista;

        Integer recordsCount = 0;
        Integer pagesAmount = 0;

        var response = new JobBasicInfoFilterDtoResponse();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            JobMapper mapper = session.getMapper(JobMapper.class);
            lista = mapper.jobBasicInfoFilter(dto.domainId, dto.projectId, dto.jobDataprocFolderName
                                    , dto.classificationType, dto.invetoriedType);
        }

        recordsCount = (lista.size() > 0) ? (int) lista.stream().count() : 0;
        pagesAmount = dto.getRecords_amount() > 0 ? (int) Math.ceil(recordsCount.floatValue() / dto.getRecords_amount().floatValue()) : 1;

        if(dto.records_amount>0){
            lista = lista.stream()
                    .skip(dto.records_amount * (dto.page - 1))
                    .limit(dto.records_amount)
                    .collect(Collectors.toList());
        }

        /*for(JobBasicInfoSelectDtoResponse item : lista) {
            if(item.getCreateAuditDate() != null) {
                item.setCreateAuditDate_S(convertDateToString(item.getCreateAuditDate(),"dd/MM/yyyy HH:mm:ss"));
            }
            if(item.getUpdateAuditDate() != null) {
                item.setUpdateAuditDate_S(convertDateToString(item.getUpdateAuditDate(),"dd/MM/yyyy HH:mm:ss"));
            }

        }*/

        response.setCount(recordsCount);
        response.setPages_amount(pagesAmount);
        response.setData(lista);
        LOGGER.info(JSONUtils.convertFromObjectToJson(response.getData()));

        return response;
    }
    public JobDTO getJobById(int jobId) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            JobMapper mapper = session.getMapper(JobMapper.class);
            return mapper.getJobById(jobId);
        }
    }
}
