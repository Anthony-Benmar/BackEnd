package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.JobBasicInfoMapper;
import com.bbva.dto.job.response.JobBasicInfoDtoResponse;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobBasicInfoDao {

    private static final Logger LOGGER = Logger.getLogger(JobBasicInfoDao.class.getName());
    private static JobBasicInfoDao instance = null;

    public static synchronized JobBasicInfoDao getInstance() {
        if (Objects.isNull(instance)) {
            instance = new JobBasicInfoDao();
        }
        return instance;
    }

    public List<JobBasicInfoDtoResponse> listAll() {
        List<JobBasicInfoDtoResponse> jobBasicInfoList = null;
        try{
            LOGGER.info("Listar JobBasicInfo en Mapper");
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                JobBasicInfoMapper mapper = session.getMapper(JobBasicInfoMapper.class);
                jobBasicInfoList = mapper.listAll();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return jobBasicInfoList;
    }
}
