package com.bbva.dao;

import com.bbva.database.mappers.VisaSourcesMapper;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterDataDtoResponse;
import com.bbva.dto.visa_sources.request.*;
import com.bbva.dto.visa_sources.response.*;
import com.bbva.entities.InsertEntity;
import com.bbva.entities.UpdateEntity;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VisaSourcesDaoTest {

    @Mock
    private SqlSessionFactory sqlSessionFactory;
    @Mock
    private SqlSession sqlSession;
    @Mock
    private VisaSourcesMapper mapper;

    @InjectMocks
    private VisaSourcesDao dao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(sqlSessionFactory.openSession()).thenReturn(sqlSession);
        when(sqlSession.getMapper(VisaSourcesMapper.class)).thenReturn(mapper);
    }

    @Test
    void testGetVisaSources() {
        VisaSourcePaginationDtoRequest dto = new VisaSourcePaginationDtoRequest();
        List<VisaSourcesDataDtoResponse> expected = Collections.singletonList(new VisaSourcesDataDtoResponse());
        when(mapper.getVisaSourceWithFilters(dto)).thenReturn(expected);

        List<VisaSourcesDataDtoResponse> result = dao.getVisaSources(dto);

        assertEquals(expected, result);
        verify(mapper).getVisaSourceWithFilters(dto);
    }

    @Test
    void testGetVisaSourcesTotalCount() {
        VisaSourcePaginationDtoRequest dto = new VisaSourcePaginationDtoRequest();
        when(mapper.getVisaSourcesTotalCountWithFilters(dto)).thenReturn(5);

        int result = dao.getVisaSourcesTotalCount(dto);

        assertEquals(5, result);
        verify(mapper).getVisaSourcesTotalCountWithFilters(dto);
    }

    @Test
    void testRegisterVisaSource() {
        RegisterVisaSourceDtoRequest dto = new RegisterVisaSourceDtoRequest();
        InsertEntity insertEntity = new InsertEntity();
        when(mapper.insertVisaSourceEntity(dto)).thenReturn(insertEntity);

        InsertEntity result = dao.registerVisaSource(dto);

        assertEquals(insertEntity, result);
        verify(mapper).insertVisaSourceEntity(dto);
    }

    @Test
    void testUpdateVisaSource() {
        RegisterVisaSourceDtoRequest dto = new RegisterVisaSourceDtoRequest();
        UpdateEntity updateEntity = new UpdateEntity();
        when(mapper.updateVisaSourceEntity(dto)).thenReturn(updateEntity);

        UpdateEntity result = dao.updateVisaSource(dto);

        assertEquals(updateEntity, result);
        verify(mapper).updateVisaSourceEntity(dto);
    }

    @Test
    void testApproveVisaSource() {
        ApproveVisaSourceDtoRequest dto = new ApproveVisaSourceDtoRequest();
        VisaSourceApproveDtoResponse response = new VisaSourceApproveDtoResponse();
        when(mapper.approveVisaSource(dto)).thenReturn(response);

        VisaSourceApproveDtoResponse result = dao.approveVisaSource(dto);

        assertEquals(response, result);
        verify(mapper).approveVisaSource(dto);
    }

    @Test
    void testValidateSourceIdsSingle() {
        String ids = "1";
        SourceWithParameterDataDtoResponse source = new SourceWithParameterDataDtoResponse();
        source.setReplacementId("99");
        when(mapper.validateSourceIds(ids)).thenReturn(Collections.singletonList(source));

        VisaSourceValidateExistDtoResponse result = dao.validateSourceIds(ids);

        assertTrue(result.isValidated());
        assertFalse(result.isMultipleValidation());
        assertEquals("99", result.getReplacementId());
    }

    @Test
    void testValidateSourceIdsMultiple() {
        String ids = "1,2";
        SourceWithParameterDataDtoResponse source1 = new SourceWithParameterDataDtoResponse();
        SourceWithParameterDataDtoResponse source2 = new SourceWithParameterDataDtoResponse();
        when(mapper.validateSourceIds(ids)).thenReturn(Arrays.asList(source1, source2));

        VisaSourceValidateExistDtoResponse result = dao.validateSourceIds(ids);

        assertTrue(result.isValidated());
        assertTrue(result.isMultipleValidation());
    }

    @Test
    void testGetVisaSources_Exception() {
        VisaSourcePaginationDtoRequest dto = new VisaSourcePaginationDtoRequest();
        when(mapper.getVisaSourceWithFilters(dto)).thenThrow(new RuntimeException("DB error"));

        List<VisaSourcesDataDtoResponse> result = dao.getVisaSources(dto);

        assertNull(result);
        verify(sqlSession).close();
    }

    @Test
    void testGetVisaSourcesTotalCount_Exception() {
        VisaSourcePaginationDtoRequest dto = new VisaSourcePaginationDtoRequest();
        when(mapper.getVisaSourcesTotalCountWithFilters(dto)).thenThrow(new RuntimeException("DB error"));

        int result = dao.getVisaSourcesTotalCount(dto);

        assertEquals(0, result);
        verify(sqlSession).close();
    }

    @Test
    void testRegisterVisaSource_Exception() {
        RegisterVisaSourceDtoRequest dto = new RegisterVisaSourceDtoRequest();
        when(mapper.insertVisaSourceEntity(dto)).thenThrow(new RuntimeException("DB error"));

        InsertEntity result = dao.registerVisaSource(dto);

        assertNotNull(result);
        verify(sqlSession).close();
    }

    @Test
    void testUpdateVisaSource_Exception() {
        RegisterVisaSourceDtoRequest dto = new RegisterVisaSourceDtoRequest();
        when(mapper.updateVisaSourceEntity(dto)).thenThrow(new RuntimeException("DB error"));

        UpdateEntity result = dao.updateVisaSource(dto);

        assertNotNull(result);
        verify(sqlSession).close();
    }

    @Test
    void testApproveVisaSource_Exception() {
        ApproveVisaSourceDtoRequest dto = new ApproveVisaSourceDtoRequest();
        when(mapper.approveVisaSource(dto)).thenThrow(new RuntimeException("DB error"));

        VisaSourceApproveDtoResponse result = dao.approveVisaSource(dto);

        assertNotNull(result);
        verify(sqlSession).close();
    }

    @Test
    void testValidateSourceIds_NoMatch() {
        String ids = "1,2";
        when(mapper.validateSourceIds(ids)).thenReturn(Collections.singletonList(new SourceWithParameterDataDtoResponse()));

        VisaSourceValidateExistDtoResponse result = dao.validateSourceIds(ids);

        assertFalse(result.isValidated());
        assertTrue(result.isMultipleValidation());
    }

    @Test
    void testValidateSourceIds_Exception() {
        String ids = "@";
        when(mapper.validateSourceIds(ids)).thenThrow(new RuntimeException("DB error"));

        VisaSourceValidateExistDtoResponse result = dao.validateSourceIds(ids);

        assertNotNull(result);
        assertFalse(result.isValidated());
    }
}