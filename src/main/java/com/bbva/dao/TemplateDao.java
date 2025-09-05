package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.TemplateMapper;
import com.bbva.dto.template.request.PaginationDtoRequest;
import com.bbva.dto.template.response.TemplatePaginationResponse;
import com.bbva.dto.template.response.TemplatePaginationDtoResponse;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Comparator;
import java.util.stream.Collectors;

public class TemplateDao {
    public TemplatePaginationDtoResponse pagination(PaginationDtoRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            TemplateMapper mapper = session.getMapper(TemplateMapper.class);
            var templates = mapper.findActiveTemplatesByType(String.valueOf(dto.getType()));

            var templatesResponse = templates.stream()
                    .map(t -> new TemplatePaginationResponse(t.template_id, t.name, t.label_one, t.orden, t.getFase(), t.getSubFase()))
                    .collect(Collectors.toList());

            var counts = templates.stream().count();

            return new TemplatePaginationDtoResponse((int) counts,1,templatesResponse);
        }
    }
}