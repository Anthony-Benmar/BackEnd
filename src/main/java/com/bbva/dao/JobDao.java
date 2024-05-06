package com.bbva.dao;

import com.bbva.core.results.DataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.JobMapper;
import com.bbva.database.mappers.ProjectMapper;
import com.bbva.dto.job.request.*;
import com.bbva.dto.job.response.*;
import com.bbva.entities.InsertEntity;
import com.bbva.util.JSONUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class JobDao {

    private static final Logger LOGGER = Logger.getLogger(JobDao.class.getName());
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(JobDao.class);
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

        int recordsCount = 0;
        int pagesAmount = 0;
        var response = new JobBasicInfoFilterDtoResponse();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            JobMapper mapper = session.getMapper(JobMapper.class);
            lista = mapper.jobBasicInfoFilter(dto.domainId, dto.projectId, dto.jobDataprocFolderName
                                    , dto.classificationType, dto.invetoriedType);
        }

        recordsCount = (lista.size() > 0) ? (int) lista.stream().count() : 0;
        pagesAmount = dto.getRecords_amount() > 0 ? (int) Math.ceil((float) recordsCount / dto.getRecords_amount().floatValue()) : 1;

        long countConInventario = (lista.size() > 0) ? (int) lista.stream()
                .filter(job -> job.getInvetoriedType().equals("1"))
                .count() : 0;
        long countConJobs = (lista.size() > 0) ? (int) lista.size() : 0;
        long countConRutaCritica = (lista.size() > 0) ? (int) lista.stream()
                .filter(job -> job.getInvetoriedType() != null && job.getInvetoriedType().equals("1"))
                .count() : 0;

        if(dto.records_amount>0){
            lista = lista.stream()
                    .skip(dto.records_amount * (dto.page - 1))
                    .limit(dto.records_amount)
                    .collect(Collectors.toList());
        }

        response.setTotalJobs((int) countConJobs);
        response.setInventoriedJobs((int) countConInventario);
        response.setCriticalRouteJobs((int) countConRutaCritica);
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
            JobDTO job = mapper.getJobById(jobId);
            if (job != null && job.getCreateAuditDate() != null) {
            job.setCreateAuditDate_S(convertMillisToDate(job.getCreateAuditDate().getTime()));
            }
            if (job != null && job.getUpdateAuditDate() != null) {
                job.setUpdateAuditDate_S(convertMillisToDate(job.getUpdateAuditDate().getTime()));
            }
            return job;
        }
    }

    public JobBasicInfoByIdDtoResponse jobBasicDetail(Integer jobId) {
        JobBasicInfoByIdDtoResponse jobBasicInfo = null;
        try{
            LOGGER.info("Obtener JobBasicInfo por jobId en Mapper");
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                JobMapper mapper = session.getMapper(JobMapper.class);
                jobBasicInfo = mapper.jobBasicDetail(jobId);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return jobBasicInfo;
    }

    public JobAdditionalDtoResponse getAdditional(Integer jobId) {
        JobAdditionalDtoResponse jobAdditional = null;
        try{
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                JobMapper mapper = session.getMapper(JobMapper.class);
                jobAdditional = mapper.getAdditional(jobId);

            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return jobAdditional;
    }

    public DataResult<JobAdditionalDtoResponse> updateAdditional(JobAdditionalDtoRequest dto) {

        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                JobMapper mapper = session.getMapper(JobMapper.class);
                mapper.updateAdditional(dto);
                session.commit();
                return new SuccessDataResult(dto);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, "500", e.getMessage());
        }
    }

    public String convertMillisToDate(Long millis) {
        Date date = new Date(millis);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatter.format(date);
    }

    public List<JobMonitoringDtoResponse> getAllMonitoringRequest() {
        List<JobMonitoringDtoResponse> jobMonitoringList = null;
        try{
            LOGGER.info("Listar JobMonitoring en Mapper");
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                JobMapper mapper = session.getMapper(JobMapper.class);
                jobMonitoringList = mapper.getAllMonitoringRequest();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return jobMonitoringList;
    }

    public InsertEntity insertMonitoringRequest(JobMonitoringRequestInsertDtoRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            JobMapper mapper = session.getMapper(JobMapper.class);
            InsertEntity result = mapper.insertMonitoringRequest(dto);
            session.commit();
            return result;
        }
    }
    public void updateMonitoringRequest(JobMonitoringDtoRequest dto) {
        try {
            LOGGER.info("Actualizar JobMonitoring en Mapper");
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                JobMapper mapper = session.getMapper(JobMapper.class);
                mapper.updateMonitoringRequest(dto);
                session.commit();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void deleteMonitoringRequest(Integer monitoringRequestId) {
    }


}
