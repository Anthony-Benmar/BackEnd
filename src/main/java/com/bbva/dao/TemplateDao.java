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
            var templates = mapper.list();
            templates = templates.stream().filter(t->t.type_id.equals(dto.getType()) && t.status.equals(1))
                    .collect(Collectors.toList());

            var templatesResponse =  templates.stream().map(t -> new TemplatePaginationResponse(t.template_id,t.name,t.label_one,t.orden))
                    .collect(Collectors.toList());

            var counts = templates.stream().count();

            if (counts>0){
                templatesResponse = templatesResponse.stream().sorted(Comparator.comparing(TemplatePaginationResponse::getOrden))
                        .collect(Collectors.toList());
            }
            return new TemplatePaginationDtoResponse((int) counts,1,templatesResponse);
        }
    }
}