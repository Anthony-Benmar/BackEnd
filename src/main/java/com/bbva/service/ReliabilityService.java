package com.bbva.service;

import com.bbva.common.HttpStatusCodes;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.ReliabilityDao;
import com.bbva.dto.catalog.response.DropDownDto;
import com.bbva.dto.reliability.request.InventoryInputsFilterDtoRequest;
import com.bbva.dto.reliability.request.InventoryJobUpdateDtoRequest;
import com.bbva.dto.reliability.request.ReliabilityPackInputFilterRequest;
import com.bbva.dto.reliability.response.ExecutionValidationDtoResponse;
import com.bbva.dto.reliability.response.InventoryInputsFilterDtoResponse;
import com.bbva.dto.reliability.response.PendingCustodyJobsDtoResponse;
import com.bbva.dto.reliability.response.ProjectCustodyInfoDtoResponse;
import com.bbva.dto.reliability.request.TransferInputDtoRequest;
import com.bbva.dto.reliability.response.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReliabilityService {
    private final ReliabilityDao reliabilityDao = new ReliabilityDao();
    private static final Logger log= Logger.getLogger(ReliabilityService.class.getName());
    private static final String ERROR = "ERROR DOCUMENTOSSERVICE: ";
    public IDataResult<InventoryInputsFilterDtoResponse> inventoryInputsFilter(InventoryInputsFilterDtoRequest dto) {
        var result = reliabilityDao.inventoryInputsFilter(dto);
        return new SuccessDataResult<>(result);
    }

    public IDataResult<Void> updateInventoryJobStock(InventoryJobUpdateDtoRequest dto) {
        try {
            reliabilityDao.updateInventoryJobStock(dto);
            return new SuccessDataResult<>(null, "Job stock updated successfully");
        } catch (Exception e) {
            log.severe("Error updating job stock: " + e.getMessage());
            return new ErrorDataResult<>(null, "500", e.getMessage());
        }
    }
    public IDataResult<List<PendingCustodyJobsDtoResponse>> getPendingCustodyJobs(String sdatoolId) {
        try {
            var result = reliabilityDao.getPendingCustodyJobs(sdatoolId);
            return new SuccessDataResult<>(result);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public IDataResult<List<JobExecutionHistoryDtoResponse>> getJobExecutionHistory(String jobName) {
        try {
            var history = reliabilityDao.getJobExecutionHistory(jobName);
            return new SuccessDataResult<>(history);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in getJobExecutionHistory", e);
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public IDataResult<List<ProjectCustodyInfoDtoResponse>> getProjectCustodyInfo(String sdatoolId) {
        try {
            var result = reliabilityDao.getProjectCustodyInfo(sdatoolId);
            return new SuccessDataResult<>(result);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public IDataResult<ExecutionValidationDtoResponse> getExecutionValidation(String jobName) {
        try {
            var result = reliabilityDao.getExecutionValidation(jobName);
            return new SuccessDataResult<>(result);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public IDataResult<List<ExecutionValidationAllDtoResponse>> getExecutionValidationAll(List<String> jobsNames) {
        try {
            var result = reliabilityDao.getExecutionValidationAll(jobsNames);
            return new SuccessDataResult<>(result);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public IDataResult<Void> insertTransfer(TransferInputDtoRequest dto) {
        try {
            if (dto.getPack() == null || dto.getPack().trim().isEmpty()) {
                return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "Pack must not be null or empty");
            }
            if (dto.getDomainId() == null) {
                return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "DomainId must not be null");
            }
            if (dto.getProductOwnerUserId() == null) {
                return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "ProductOwnerUserId must not be null");
            }
            if (dto.getUseCaseId() == null) {
                return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "UseCaseId must not be null");
            }
            reliabilityDao.insertTransfer(dto);
            return new SuccessDataResult<>(null, "Transfer insert successfully");
        } catch (Exception e) {
            log.severe("Error insert transfer: " + e.getMessage());
            return new ErrorDataResult<>(null, "500", e.getMessage());
        }
    }

    public byte[] generateDocumentInventory(InventoryInputsFilterDtoRequest dto) {
        List<InventoryInputsDtoResponse> lista = reliabilityDao.listinventory(dto);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Inventario");

            String[] columns = {"DOMINIO", "CASO DE USO", "ORIGEN", "JOB CONTROL-M", "COMPONENTE", "TIPO JOB",
                    "RUTA CRITICA", "FRECUENCIA", "INSUMOS", "SALIDA", "PACK"};

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (InventoryInputsDtoResponse inventory : lista) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(nullSafe(inventory.getDomainName()));
                row.createCell(1).setCellValue(nullSafe(inventory.getUseCase()));
                row.createCell(2).setCellValue(nullSafe(inventory.getOrigin()));
                row.createCell(3).setCellValue(nullSafe(inventory.getJobName()));
                row.createCell(4).setCellValue(nullSafe(inventory.getComponentName()));
                row.createCell(5).setCellValue(nullSafe(inventory.getJobType()));
                row.createCell(6).setCellValue(nullSafe(inventory.getIsCritical()));
                row.createCell(7).setCellValue(nullSafe(inventory.getFrequency()));
                Cell insumosCell = row.createCell(8);
                if (inventory.getInputPaths() != null) {
                    insumosCell.setCellValue(inventory.getInputPaths());
                    CellStyle multiLineStyle = workbook.createCellStyle();
                    multiLineStyle.setWrapText(true);
                    insumosCell.setCellStyle(multiLineStyle);
                } else {
                    insumosCell.setCellValue("");
                }
                row.createCell(9).setCellValue(nullSafe(inventory.getOutputPath()));
                row.createCell(10).setCellValue(nullSafe(inventory.getPack()));
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            log.info(ERROR + e);
            return new byte[0];
        }
    }

    public IDataResult<List<DropDownDto>> getOriginTypes() {
        try {
            List<DropDownDto> lista = reliabilityDao.getOriginTypes();
            return new SuccessDataResult<>(lista);
        } catch (Exception e) {
            log.severe("Error al obtener tipos de origen: " + e.getMessage());
            return new ErrorDataResult<>(null, "500", e.getMessage());
        }
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }

    public IDataResult<PaginationReliabilityPackResponse> getReliabilityPacks(ReliabilityPackInputFilterRequest dto) {
        try {
            var result = reliabilityDao.getReliabilityPacks(dto);
            return new SuccessDataResult<>(result);
        } catch (Exception e) {
            log.severe("Error get reliability stock: " + e.getMessage());
            return new ErrorDataResult<>(null, "500", e.getMessage());
        }
    }

    public IDataResult<Void> updateStatusReliabilityPacksJobStock(List<String> packs) {
        try {
            reliabilityDao.updateStatusReliabilityPacksJobStock(packs);
            return new SuccessDataResult<>(null, "ReliabilityPacks and JobStock updated successfully");
        } catch (Exception e) {
            log.severe("Error updating reliability packs and Job stock: " + e.getMessage());
            return new ErrorDataResult<>(null, "500", e.getMessage());
        }
    }
}
