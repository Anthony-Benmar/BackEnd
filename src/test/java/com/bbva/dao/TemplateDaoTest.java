package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.TemplateMapper;
import com.bbva.dto.template.request.PaginationDtoRequest;
import com.bbva.dto.template.response.TemplatePaginationDtoResponse;
import com.bbva.dto.template.response.TemplatePaginationResponse;
import com.bbva.entities.template.Template;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TemplateDaoTest {

    @Test
    void pagination_mapea_fase_y_subFase_en_respuesta() {
        SqlSessionFactory factory = mock(SqlSessionFactory.class);
        SqlSession session = mock(SqlSession.class);
        TemplateMapper mapper = mock(TemplateMapper.class);

        try (MockedStatic<MyBatisConnectionFactory> mocked = mockStatic(MyBatisConnectionFactory.class)) {
            mocked.when(MyBatisConnectionFactory::getInstance).thenReturn(factory);
            when(factory.openSession()).thenReturn(session);
            when(session.getMapper(TemplateMapper.class)).thenReturn(mapper);

            Template t1 = new Template();
            t1.setTemplate_id(1549);
            t1.setName("Validación de necesidad de Proyecto - [fuente]");
            t1.setLabel_one("DM1");
            t1.setOrden(1);
            t1.setFase("Data");
            t1.setSubFase("Mapeo");

            Template t2 = new Template();
            t2.setTemplate_id(1550);
            t2.setName("Identificación de fuentes TDS - [fuente]");
            t2.setLabel_one("DM2");
            t2.setOrden(1);
            t2.setFase("Data");
            t2.setSubFase("Mapeo");

            when(mapper.findActiveTemplatesByType("1023"))
                    .thenReturn(List.of(t1, t2));

            PaginationDtoRequest dto = new PaginationDtoRequest();
            dto.setType(1023);
            dto.setPage(1);
            dto.setRecords_amount(0);

            TemplateDao dao = new TemplateDao();
            TemplatePaginationDtoResponse out = dao.pagination(dto);

            assertNotNull(out);
            List<TemplatePaginationResponse> data = out.getData();
            assertNotNull(data);
            assertEquals(2, data.size());

            TemplatePaginationResponse r1 = data.get(0);
            assertEquals(1549, r1.getTemplateId());
            assertEquals("DM1", r1.getLabelOne());
            assertEquals("Data", r1.getFase());
            assertEquals("Mapeo", r1.getSubFase());

            TemplatePaginationResponse r2 = data.get(1);
            assertEquals(1550, r2.getTemplateId());
            assertEquals("DM2", r2.getLabelOne());
            assertEquals("Data", r2.getFase());
            assertEquals("Mapeo", r2.getSubFase());

            verify(mapper, times(1)).findActiveTemplatesByType("1023");
        }
    }
}
