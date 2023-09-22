package com.bbva.service.dictionary;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.ProjectDao;
import com.bbva.dao.dictionary.FieldDatumDao;
import com.bbva.dao.dictionary.GenerationDao;
import com.bbva.dao.dictionary.GenerationFieldDao;
import com.bbva.dao.dictionary.TemplateDao;
import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.dto.dictionary.parameter.GenerationSearchParameter;
import com.bbva.dto.dictionary.request.GenerationFinalizeRequest;
import com.bbva.dto.dictionary.request.GenerationSearchRequest;
import com.bbva.dto.dictionary.response.GenerationMasterResponse;
import com.bbva.dto.dictionary.response.GenerationResponse;
import com.bbva.entities.dictionary.FieldDatumEntity;
import com.bbva.entities.dictionary.GenerationEntity;
import com.bbva.entities.dictionary.GenerationFieldEntity;
import com.bbva.entities.dictionary.TemplateEntity;
import com.bbva.enums.dictionary.StatusGenerationFieldType;
import com.bbva.enums.dictionary.StatusGenerationType;
import com.bbva.enums.dictionary.TemplateType;
import com.bbva.service.dictionary.business.file.read.ReadDictumEngine;
import com.bbva.service.dictionary.business.file.read.ReadDictumResult;
import com.bbva.service.dictionary.business.file.write.WriteDictionaryEngine;
import com.bbva.service.dictionary.business.file.write.WriteDictionaryResult;
import com.bbva.service.dictionary.map.GenerationMap;
import com.bbva.util.exception.ApplicationException;
import com.bbva.util.session.UserInfo;
import com.bbva.util.types.FechaUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class GenerationService {
    
    private static final Logger LOGGER = Logger.getLogger(GenerationService.class.getName());

    private static GenerationService instance = null;
    
    public static synchronized GenerationService getInstance() {
        if (Objects.isNull(instance)) {
            instance = new GenerationService();
        }
        return instance;
    } 

    
    public IDataResult<GenerationResponse> crear(HttpServletRequest httpRequest, MultipartFormDataInput multipart){
        
        List<String> listSourceFields;
        TemplateEntity templateDictum;
        Integer projectId;
        String sourceId;
        String sourceName;

        /** 01 - Procesamiento Archivo **/
        try{
            
            InputStream inputStreamFile = multipart.getFormDataPart("file", InputStream.class, null);
            if(inputStreamFile == null){
                throw new ApplicationException("Archivo es requerido.");
            }
            String inputStringFileName = multipart.getFormDataPart("fileName", String.class, null);
            if(StringUtils.isBlank(inputStringFileName)){
                throw new ApplicationException("Nombre de Archivo es requerido.");
            }

            Integer inputIntegerProjectId = multipart.getFormDataPart("projectId", Integer.class, null);
            if(inputIntegerProjectId == null){
                throw new ApplicationException("Id Proyecto es requerido.");
            }

            String inputStringSourceId = multipart.getFormDataPart("sourceId", String.class, null);
            if(StringUtils.isBlank(inputStringSourceId)){
                throw new ApplicationException("Id Fuente es requerido.");
            }

            String inputStringSourceName = multipart.getFormDataPart("sourceName", String.class, null);
            if(StringUtils.isBlank(inputStringSourceName)){
                throw new ApplicationException("Nombre de fuente es requerido.");
            }

            projectId = inputIntegerProjectId;
            sourceId = inputStringSourceId;
            sourceName = inputStringSourceName;
            templateDictum = TemplateDao.getInstance().obtenerVigente(TemplateType.DICTAMEN.getCodigo());
            ReadDictumResult resultProcessDictum = ReadDictumEngine.getInstance(templateDictum).process(inputStreamFile);
            listSourceFields = resultProcessDictum.getListFields();
        
        }catch(ApplicationException aex){
            LOGGER.log(Level.SEVERE, StringUtils.EMPTY, aex);
            return new ErrorDataResult<>(aex.getMessage());
        }
        catch(Exception ex){
            LOGGER.log(Level.SEVERE, StringUtils.EMPTY, ex);
            return new ErrorDataResult<>("Error de Sistema.");
        }

        /** 02 - Persistencia de Datos **/
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();     
        try(SqlSession session = sqlSessionFactory.openSession()){
            boolean existenCamposEncontrados = (listSourceFields != null && !listSourceFields.isEmpty());
            if(existenCamposEncontrados){
                List<GenerationFieldEntity> listGenerationField = new ArrayList<>();
                GenerationFieldEntity generationFieldEntity;
                for(String sourceField : listSourceFields){   
                    generationFieldEntity = new GenerationFieldEntity();
                    generationFieldEntity.setFieldName(sourceField);
                    List<FieldDatumEntity> listaFieldDatum = FieldDatumDao.getInstance().filtrar(sourceField, session);
                    boolean existenCoincidencias = (listaFieldDatum != null && !listaFieldDatum.isEmpty());
                    boolean existeCoincidenciaUnica = (listaFieldDatum != null && !listaFieldDatum.isEmpty() && listaFieldDatum.size() == 1);

                    if(existenCoincidencias){
                        if(existeCoincidenciaUnica){
                            generationFieldEntity.setStatus(StatusGenerationFieldType.SIN_OBSERVACION.getCodigo());
                            GenerationFieldService.getInstance().copyFieldDatum(generationFieldEntity, listaFieldDatum.get(0));
                        }else{
                            generationFieldEntity.setStatus(StatusGenerationFieldType.OBSERVADO.getCodigo());
                        }
                    }else{
                        generationFieldEntity.setStatus(StatusGenerationFieldType.NO_ENCONTRADO.getCodigo());
                    }

                    listGenerationField.add(generationFieldEntity);
                }

                GenerationEntity generationEntity = GenerationEntity.builder().employeeId(UserInfo.getEmployeeId(httpRequest))
                                                                                .projectId(projectId)
                                                                                .sourceId(sourceId)
                                                                                .sourceName(sourceName)
                                                                                .status(StatusGenerationType.EN_PROGRESO.getCodigo())
                                                                                .generationDate(FechaUtil.ahora())
                                                                                .dictumTemplateId(templateDictum.getTemplateId()).build();
                GenerationDao.getInstance().insertar(generationEntity, session);
                

                listGenerationField.forEach(field -> {
                    field.setGenerationId(generationEntity.getGenerationId());
                    GenerationFieldDao.getInstance().insertar(field, session);
                });

                session.commit();
                generationEntity.setProjectName(new ProjectDao().findById(generationEntity.getProjectId()).getProject_name());
                return new SuccessDataResult<>(GenerationResponse.builder().master(GenerationMap.entityToDTO(generationEntity)).build(), "Generación de diccionario creada.");

            }else{
                throw new ApplicationException("No se encontraron campos para el procesamiento");
            }
            
        }catch(ApplicationException aex){
            LOGGER.log(Level.SEVERE, StringUtils.EMPTY, aex);
            return new ErrorDataResult<>(aex.getMessage());
        }
        catch(Exception ex){
            LOGGER.log(Level.SEVERE, StringUtils.EMPTY, ex);
            return new ErrorDataResult<>("Error de Sistema.");
        }
        
    }
    
    public IDataResult<GenerationMasterResponse> finalizar(GenerationFinalizeRequest request){

        try{

            GenerationEntity generationEntity = GenerationDao.getInstance().buscarPorId(request.getGenerationId());
            List<GenerationFieldEntity> listaGenerationFieldEntity = GenerationFieldDao.getInstance().buscar(request.getGenerationId());
            TemplateEntity templateDictionary = TemplateDao.getInstance().obtenerVigente(TemplateType.DICCIONARIO.getCodigo());

            int numeroObservados = (int)listaGenerationFieldEntity.stream().filter(field -> StatusGenerationFieldType.OBSERVADO == StatusGenerationFieldType.get(field.getStatus())).count();
            if(numeroObservados > 0){
                throw new ApplicationException("Campos pendientes de resolución");
            }

            WriteDictionaryResult result = WriteDictionaryEngine.getInstance(generationEntity, listaGenerationFieldEntity, templateDictionary).process();

            generationEntity.setDictionaryLogicalFileName(result.getDictionaryLogicalFileName());
            generationEntity.setStatus(StatusGenerationType.COMPLETADO.getCodigo());
            generationEntity.setGenerationCompleteDate(FechaUtil.ahora());
            generationEntity.setDictionaryTemplateId(templateDictionary.getTemplateId());
            generationEntity.setDictionaryFile(result.getBytesDictionary());
            GenerationDao.getInstance().finalizar(generationEntity);                                                                       
            return new SuccessDataResult<>(GenerationMap.entityToDTO(GenerationDao.getInstance().buscarPorId(request.getGenerationId())));
        }catch(ApplicationException aex){
            LOGGER.log(Level.SEVERE, StringUtils.EMPTY, aex);
            return new ErrorDataResult<>(aex.getMessage());
        }catch(Exception ex){
            LOGGER.log(Level.SEVERE, StringUtils.EMPTY, ex);
            return new ErrorDataResult<>("Error de Sistema.");
        } 
    }

    public IDataResult<GenerationResponse> buscar(Integer generationId){
        try{
            return new SuccessDataResult<>(GenerationResponse.builder().master(GenerationMap.entityToDTO(GenerationDao.getInstance().buscarPorId(generationId))).build());
        }catch(Exception ex){
            LOGGER.log(Level.SEVERE, StringUtils.EMPTY, ex);
            return new ErrorDataResult<>("Error de Sistema.");
        }    
    }

    public IDataResult<List<GenerationMasterResponse>> buscar(HttpServletRequest httpRequest, GenerationSearchRequest request){
        try{
            GenerationSearchParameter parameter = GenerationSearchParameter.builder().projectId(request.getProjectId())
                                                                                        .sourceId(request.getSourceId())
                                                                                        .sourceName(request.getSourceName())
                                                                                        .startDate(FechaUtil.convertStringToDate(request.getStartDate(), "dd/MM/yyyy"))
                                                                                        .endingDate(FechaUtil.convertStringToDate(request.getEndingDate(), "dd/MM/yyyy"))
                                                                                        .employeeId(request.getOwnerRecordOnly() != null && request.getOwnerRecordOnly().booleanValue() ?
                                                                                                        UserInfo.getEmployeeId(httpRequest) : StringUtils.EMPTY).build();

            return new SuccessDataResult<>(GenerationMap.listEntityToDTO(GenerationDao.getInstance().buscar(parameter)));
        }catch(Exception ex){
            LOGGER.log(Level.SEVERE, StringUtils.EMPTY, ex);
            return new ErrorDataResult<>("Error de Sistema.");
        } 
    }
    
    public Map<String, Object> descargarDiccionario(Integer generationId){

        try{
            GenerationEntity generationEntity = GenerationDao.getInstance().buscarPorId(generationId);
            Map<String, Object> result = new HashMap<>();
            result.put("fileData", generationEntity.getDictionaryFile()); 
            result.put("fileName", generationEntity.getDictionaryLogicalFileName());
            return result;
        }catch(Exception ex){
            LOGGER.log(Level.SEVERE, StringUtils.EMPTY, ex);
            throw new NotFoundException("Archivo no encontrado");
        }
    }

    public void desactivarAntiguos(){
        GenerationDao.getInstance().desactivarAntiguos(FechaUtil.ahora());
    }

}
