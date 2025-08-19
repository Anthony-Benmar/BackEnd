package com.bbva.service;

import com.bbva.common.HttpStatusCodes;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.ReliabilityDao;
import com.bbva.dto.catalog.response.DropDownDto;
import com.bbva.dto.reliability.request.*;
import com.bbva.dto.reliability.response.ExecutionValidationDtoResponse;
import com.bbva.dto.reliability.response.InventoryInputsFilterDtoResponse;
import com.bbva.dto.reliability.response.PendingCustodyJobsDtoResponse;
import com.bbva.dto.reliability.response.ProjectCustodyInfoDtoResponse;
import com.bbva.dto.reliability.response.*;
import com.bbva.util.policy.TransferStatusPolicy;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.bbva.util.policy.TransferStatusPolicy.Action;

public class ReliabilityService {
    private final ReliabilityDao reliabilityDao = new ReliabilityDao();
    private static final Logger log= Logger.getLogger(ReliabilityService.class.getName());
    private static final String ERROR = "ERROR DOCUMENTOSSERVICE: ";
    private static final String MSG_PACK_NOT_FOUND = "Pack no encontrado";
    private static final String MSG_ACCION_INVALIDA = "Acción inválida";
    private static final String CODE_500 = "500";
    private static final String CODE_404 = "404";
    private static final String CODE_409 = "409";

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

    public IDataResult<List<DropDownDto>> getSn2Options(Integer sn1) {
        try {
            List<RawSn2DtoResponse> raws = reliabilityDao.fetchRawSn2BySn1(sn1);
            var opts = raws.stream()
                    .map(r -> {
                        String d = r.getRawDesc();
                        int i1 = d.indexOf('-');
                        int i2 = d.lastIndexOf('-');
                        String label;
                        if (i1 >= 0 && i2 > i1) {
                            label = d.substring(i1 + 1, i2).trim();
                        } else {
                            label = d;
                        }
                        return new DropDownDto(r.getValue(), label);
                    })
                    .toList();
            return new SuccessDataResult<>(opts);
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Error fetching SN2 options", ex);
            return new ErrorDataResult<>(null, "500", ex.getMessage());
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

    public IDataResult<PaginationReliabilityPackResponse> getReliabilityPacksAdvanced(
            ReliabilityPackInputFilterRequest dto) {
        try {
            String statusCsv = TransferStatusPolicy.toCsv(dto.getRole(), dto.getTab());

            var lista = reliabilityDao.listTransfersByStatus(
                    safe(dto.getDomainName()),
                    safe(dto.getUseCase()),
                    statusCsv
            );

            String role = norm(dto.getRole());
            String tab  = norm(dto.getTab());

            applyEditFlags(lista, role, tab);

            int size = Optional.ofNullable(dto.getRecordsAmount()).orElse(10);
            int page = Optional.ofNullable(dto.getPage()).orElse(1);

            var res = buildPagedResponse(lista, size, page);
            return new SuccessDataResult<>(res);
        } catch (Exception e) {
            return new ErrorDataResult<>(null, "500", e.getMessage());
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String norm(String s) {
        return (s == null) ? "" : s.trim().toUpperCase(Locale.ROOT);
    }

    /** Marca el flag de edición por fila (SM sólo en Devueltos; en APROBADOS siempre 0). */
    private static void applyEditFlags(List<ReliabilityPacksDtoResponse> lista, String role, String tab) {
        boolean readOnly = "APROBADOS".equals(tab);
        for (var row : lista) {
            int can = readOnly ? 0 : TransferStatusPolicy.canEdit(role, row.getStatusId());
            row.setCambiedit(can);
        }
    }

    /** Construye la respuesta paginada sin ramificar en el método principal. */
    private static PaginationReliabilityPackResponse buildPagedResponse(
            List<ReliabilityPacksDtoResponse> full, int size, int page) {

        int recordsCount = full.size();
        int pages = size > 0 ? (int) Math.ceil(recordsCount / (double) size) : 1;

        List<ReliabilityPacksDtoResponse> pageData = (size > 0)
                ? full.stream()
                .skip((long) size * (Math.max(page, 1) - 1))
                .limit(size)
                .toList()
                : full;

        var res = new PaginationReliabilityPackResponse();
        res.setCount(recordsCount);
        res.setPagesAmount(pages);
        res.setData(pageData);
        return res;
    }

    public IDataResult<TransferStatusChangeResponse> changeTransferStatus(String pack, TransferStatusChangeRequest req) {
        try {
            Integer oldSt = reliabilityDao.getPackCurrentStatus(pack);
            if (oldSt == null) {
                return new ErrorDataResult<>(null, CODE_404, MSG_PACK_NOT_FOUND);
            }

            Action action = validateAction(req.getAction());
            if (action == null) {
                return new ErrorDataResult<>(null, "400", MSG_ACCION_INVALIDA);
            }

            // Calcula el nuevo estado con la policy y actualiza con el DAO (firma existente)
            int newSt = TransferStatusPolicy.computeNextStatusOrThrow(req.getActorRole(), oldSt, action);
            reliabilityDao.changeTransferStatus(pack, newSt);

            var resp = TransferStatusChangeResponse.builder()
                    .pack(pack).oldStatus(oldSt).newStatus(newSt).build();
            return new SuccessDataResult<>(resp, "Estado actualizado");

        } catch (IllegalArgumentException iae) {
            return new ErrorDataResult<>(null, CODE_409, iae.getMessage());
        } catch (Exception e) {
            log.severe(ERROR + e.getMessage());
            return new ErrorDataResult<>(null, CODE_500, e.getMessage());
        }
    }

    private Action validateAction(String actionStr) {
        try {
            return Action.valueOf(actionStr.toUpperCase(Locale.ROOT));
        } catch (Exception ex) {
            return null;
        }
    }

    public IDataResult<Void> updateJobBySm(UpdateJobDtoRequest dto) {
        try {
            Integer st = reliabilityDao.getPackCurrentStatus(dto.getPack());
            if (st == null) {
                return new ErrorDataResult<>(null, CODE_404, MSG_PACK_NOT_FOUND);
            }

            // (La verificación de permisos la puedes dejar como la tenías si quieres)
            if (TransferStatusPolicy.canEdit(dto.getActorRole(), st) != 1) {
                return new ErrorDataResult<>(null, CODE_409, "Solo se puede editar cuando fue devuelto");
            }

            // Usa el método real del DAO
            reliabilityDao.updateJobByPackAndName(dto);
            return new SuccessDataResult<>(null, "Job actualizado");

        } catch (ReliabilityDao.PersistenceException pe) {
            return new ErrorDataResult<>(null, CODE_404, pe.getMessage());
        } catch (Exception e) {
            log.severe(ERROR + e.getMessage());
            return new ErrorDataResult<>(null, CODE_500, e.getMessage());
        }
    }

    public IDataResult<Void> updateCommentsForPack(String pack, String role, String comments) {
        try {
            Integer st = reliabilityDao.getPackCurrentStatus(pack);
            if (st == null) {
                return new ErrorDataResult<>(null, CODE_404, MSG_PACK_NOT_FOUND);
            }

            if (TransferStatusPolicy.canWriteGeneralComment(role, st) != 1) {
                return new ErrorDataResult<>(null, CODE_409, "No tienes permisos para comentar en este estado");
            }

            reliabilityDao.updatePackComments(pack, comments);
            return new SuccessDataResult<>(null, "Comentarios actualizados");

        } catch (ReliabilityDao.PersistenceException pe) {
            return new ErrorDataResult<>(null, CODE_404, pe.getMessage());
        } catch (Exception e) {
            log.severe(ERROR + e.getMessage());
            return new ErrorDataResult<>(null, CODE_500, e.getMessage());
        }
    }

    public IDataResult<TransferDetailResponse> getTransferDetail(String pack) {
        try {
            var detail = reliabilityDao.getTransferDetail(pack);
            if (detail == null) return new ErrorDataResult<>(null, "404", "Pack no encontrado");
            return new SuccessDataResult<>(detail);
        } catch (Exception e) {
            return new ErrorDataResult<>(null, "500", e.getMessage());
        }
    }

}
