package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.AdaMapper;
import com.bbva.dto.ada.request.AdaJobExecutionFilterRequestDTO;
import com.bbva.dto.ada.response.AdaJobExecutionFilterData;
import com.bbva.dto.ada.response.AdaJobExecutionFilterResponseDTO;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

public class AdaDao {

    public AdaJobExecutionFilterResponseDTO filter(AdaJobExecutionFilterRequestDTO dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<AdaJobExecutionFilterData> list;

        Integer recordsCount;
        Integer pagesAmount;

        AdaJobExecutionFilterResponseDTO response = new AdaJobExecutionFilterResponseDTO();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            AdaMapper mapper = session.getMapper(AdaMapper.class);
            list = mapper.filter(dto);
        }
        recordsCount = (!list.isEmpty()) ? list.get(0).getRecordsCount() : 0;
        pagesAmount = dto.getRecords_amount() > 0 ? (int) Math.ceil(recordsCount.floatValue() / dto.getRecords_amount().floatValue()) : 1;

        response.setCount(recordsCount);
        response.setPagesAmount(pagesAmount);
        response.setData(list);
        return response;
    }
}
