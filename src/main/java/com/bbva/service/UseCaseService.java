package com.bbva.service;

import com.bbva.common.HttpStatusCodes;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.UseCaseReliabilityDao;
import com.bbva.dto.use_case.request.UpdateOrInsertUseCaseDtoRequest;
import com.bbva.dto.use_case.request.UseCaseInputsFilterDtoRequest;
import com.bbva.dto.use_case.response.UpdateOrInsertDtoResponse;
import com.bbva.dto.use_case.response.UseCaseInputsDtoResponse;
import com.bbva.dto.use_case.response.UseCaseInputsFilterDtoResponse;
import com.bbva.entities.use_case.UseCaseEntity;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UseCaseService {
    private final UseCaseReliabilityDao useCaseReliabilityDao = new UseCaseReliabilityDao();
    private static final Logger log= Logger.getLogger(UseCaseService.class.getName());

    public IDataResult<List<UseCaseEntity>> listUseCases() {
        try {
            var result = useCaseReliabilityDao.listAllUseCases();
            return new SuccessDataResult<>(result);
        }catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public IDataResult<UpdateOrInsertDtoResponse> updateOrInsertUseCase(UpdateOrInsertUseCaseDtoRequest dto) {
        try {
            IDataResult<UpdateOrInsertDtoResponse> validationResult = validateRequest(dto);
            if (validationResult != null) {
                return validationResult;
            }

            boolean isInsert = isInsertOperation(dto);
            var result = useCaseReliabilityDao.updateOrInsertUseCase(dto);
            return new SuccessDataResult<>(result, HttpStatusCodes.HTTP_OK,
                    isInsert ? "Use case inserted successfully." : "Use case updated successfully.");
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    boolean isInsertOperation(UpdateOrInsertUseCaseDtoRequest dto) {
        return dto.getUseCaseId() == null || dto.getUseCaseId().equals(0);
    }

    IDataResult<UpdateOrInsertDtoResponse> validateRequest(UpdateOrInsertUseCaseDtoRequest dto) {
        if (!isInsertOperation(dto) && (dto.getUseCaseId() == null || dto.getUseCaseId().equals(0))) {
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "UseCaseId is required for update.");
        }

        if (isNullOrBlank(dto.getUseCaseName())) {
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "UseCaseName must not be null or empty");
        }

        if (isNullOrBlank(dto.getUseCaseDescription())) {
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "UseCaseDescription must not be null or empty");
        }

        if (isNullOrZero(dto.getDomainId())) {
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "DomainId must not be null or 0");
        }

        if (isNullOrZero(dto.getDeliveredPiId())) {
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "DeliveredPiId must not be null or 0");
        }

        if (isNullOrZero(dto.getCritical())) {
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "Critical must not be null or 0");
        }

        if (dto.getIsRegulatory() == null) {
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "Regulatory must not be null");
        }

        if (isNullOrZero(dto.getUseCaseScope())) {
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "UseCaseScope must not be null or 0");
        }

        if (dto.getOperativeModel() == null) {
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "OperativeModel must not be null");
        }

        return null;
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean isNullOrZero(Integer value) {
        return value == null || value.equals(0);
    }

    public IDataResult<UseCaseInputsFilterDtoResponse> getFilteredUseCases(UseCaseInputsFilterDtoRequest dto) {
        try {
            var result = useCaseReliabilityDao.getFilteredUseCases(dto);
            return new SuccessDataResult<>(result);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public byte[] generateDocumentUseCases(UseCaseInputsFilterDtoRequest dto) {
        List<UseCaseInputsDtoResponse> rows = useCaseReliabilityDao.listAllFilteredUseCases(dto);

        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Casos de Uso");
            String[] cols = {
                    "DOMINIO", "NOMBRE", "DESCRIPCIÓN", "NRO PROYECTOS", "PROYECTOS ASOCIADOS",
                    "TRIMESTRE", "CRITICIDAD", "REGULATORIO", "GLOBAL / LOCAL", "MODELO OPERATIVO SDM"
            };

            Row header = sheet.createRow(0);
            for (int i = 0; i < cols.length; i++) {
                header.createCell(i).setCellValue(cols[i]);
            }

            int r = 1;
            for (UseCaseInputsDtoResponse u : rows) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(nullSafe(u.getDomainName()));
                row.createCell(1).setCellValue(nullSafe(u.getUseCaseName()));
                row.createCell(2).setCellValue(nullSafe(u.getUseCaseDescription()));
                row.createCell(3).setCellValue(u.getProjectCount() == null ? 0 : u.getProjectCount());

                Cell c4 = row.createCell(4);
                if (u.getProjects() != null) {
                    c4.setCellValue(u.getProjects());
                    CellStyle wrap = wb.createCellStyle();
                    wrap.setWrapText(true);
                    c4.setCellStyle(wrap);
                } else {
                    c4.setCellValue("");
                }

                row.createCell(5).setCellValue(nullSafe(u.getPiLargeName()));
                row.createCell(6).setCellValue(nullSafe(u.getCriticalDesc()));
                row.createCell(7).setCellValue(
                        u.getIsRegulatory() != null && u.getIsRegulatory() == 1 ? "SI" : "NO"
                );
                row.createCell(8).setCellValue(nullSafe(u.getUseCaseScopeDesc()));
                row.createCell(9).setCellValue(
                        u.getOperativeModel() != null && u.getOperativeModel() == 1 ? "SI" : "NO"
                );
            }

            for (int i = 0; i < cols.length; i++) {
                sheet.autoSizeColumn(i);
            }

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.severe("Error generando Excel Casos de Uso: " + e.getMessage());
            return new byte[0];
        }
    }
    private String nullSafe(String v) {
        return v == null ? "" : v;
    }
}
