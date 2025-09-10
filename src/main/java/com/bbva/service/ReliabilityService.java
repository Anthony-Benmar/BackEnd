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
    private static final Logger log = Logger.getLogger(ReliabilityService.class.getName());
    private static final String ERROR = "ERROR DOCUMENTOSSERVICE: ";
    private static final String PACK_NOT_FOUND = "ERROR PACK";

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
            if (dto.getProductOwnerEmail() == null) {
                return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "ProductOwnerEmail must not be null or empty");
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

    public IDataResult<List<String>> listActiveSdatools() {
        try {
            var list = reliabilityDao.listActiveSdatools();
            return new SuccessDataResult<>(list);
        } catch (Exception e) {
            return new ErrorDataResult<>(null, "500", e.getMessage());
        }
    }

    public IDataResult<Void> updateJobSdatool(String jobName, String newSdatoolId) {
        try {
            if (jobName == null || jobName.isBlank() || newSdatoolId == null || newSdatoolId.isBlank())
                return new ErrorDataResult<>(null, "400", "jobName y newSdatoolId son obligatorios");
            reliabilityDao.updateJobSdatool(jobName, newSdatoolId);
            return new SuccessDataResult<>(null, "SDATOOL de job actualizado");
        } catch (ReliabilityDao.PersistenceException pe) {
            return new ErrorDataResult<>(null, "404", pe.getMessage());
        } catch (Exception e) {
            return new ErrorDataResult<>(null, "500", e.getMessage());
        }
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
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
            ReliabilityPackInputFilterRequest dto, String requesterEmail) {
        try {
            java.util.function.UnaryOperator<String> lowerOp = s -> s == null ? "" : s.trim().toLowerCase(java.util.Locale.ROOT);
            java.util.function.UnaryOperator<String> upperOp = s -> s == null ? "" : s.trim().toUpperCase(java.util.Locale.ROOT);
            java.util.function.UnaryOperator<String> nzOp    = s -> s == null ? "" : s;

            final String statusCsv  = TransferStatusPolicy.toCsv(dto.getRole(), dto.getTab());
            final String domainCsv  = nzOp.apply(dto.getDomainName());
            final String useCaseCsv = nzOp.apply(dto.getUseCase());

            List<ReliabilityPacksDtoResponse> lista =
                    reliabilityDao.listTransfersByStatus(domainCsv, useCaseCsv, statusCsv);

            final String role  = upperOp.apply(dto.getRole());
            final String email = lowerOp.apply(requesterEmail);

            java.util.Set<String> kmAllowed =
                    "KM".equals(role)
                            ? new java.util.HashSet<>(reliabilityDao.getKmAllowedDomainNames(email))
                            : java.util.Set.of();

            java.util.function.Predicate<ReliabilityPacksDtoResponse> filter =
                    TransferStatusPolicy.buildPacksFilter(role, email, kmAllowed);

            lista = lista.stream().filter(filter).toList();

            final boolean readOnly = "APROBADOS".equalsIgnoreCase(dto.getTab());
            for (var row : lista) {
                row.setCambiedit(TransferStatusPolicy.computeCambieditFlag(readOnly, role, row.getStatusId()));
            }

            int size = (dto.getRecordsAmount() == null) ? 10 : dto.getRecordsAmount();
            int records = lista.size();
            if (size <= 0) size = (records == 0) ? 1 : records;

            int page = (dto.getPage() == null) ? 1 : dto.getPage();
            int safePage = Math.max(page, 1);
            int pages = Math.max(1, (int) Math.ceil(records / (double) size));

            List<ReliabilityPacksDtoResponse> pageData =
                    lista.stream().skip((long) size * (safePage - 1)).limit(size).toList();

            var res = new PaginationReliabilityPackResponse();
            res.setCount(records);
            res.setPagesAmount(pages);
            res.setData(pageData);
            return new SuccessDataResult<>(res);
        } catch (Exception e) {
            return new ErrorDataResult<>(null, "500", e.getMessage());
        }
    }

    public IDataResult<TransferStatusChangeResponse> changeTransferStatus(String pack, TransferStatusChangeRequest req) {
        try {
            Integer oldSt = reliabilityDao.getPackCurrentStatus(pack);
            if (oldSt == null) {
                return new ErrorDataResult<>(null, "404", PACK_NOT_FOUND);
            }
            Action action = parseAction(req);
            if (action == null) {
                return new ErrorDataResult<>(null, "400", "Acción inválida");
            }
            String actorRole = (req.getActorRole() == null) ? "" : req.getActorRole();
            int newSt = TransferStatusPolicy.computeNextStatusOrThrow(actorRole, oldSt, action);
            reliabilityDao.changeTransferStatus(pack, newSt);
            var resp = TransferStatusChangeResponse.builder()
                    .pack(pack).oldStatus(oldSt).newStatus(newSt).build();
            return new SuccessDataResult<>(resp, "Estado actualizado");
        } catch (IllegalArgumentException iae) {
            return new ErrorDataResult<>(null, "409", iae.getMessage());
        } catch (Exception e) {
            log.severe(ERROR + e.getMessage());
            return new ErrorDataResult<>(null, "500", e.getMessage());
        }
    }

    private Action parseAction(TransferStatusChangeRequest req) {
        if (req == null) return null;
        String raw = req.getAction();
        if (raw == null) return null;
        String norm = raw.trim().toUpperCase(Locale.ROOT);
        try {
            return Action.valueOf(norm);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public IDataResult<Void> updateJobBySm(UpdateJobDtoRequest dto) {
        try {
            Integer st = reliabilityDao.getPackCurrentStatus(dto.getPack());
            if (st == null) return new ErrorDataResult<>(null, "404", PACK_NOT_FOUND);
            boolean hasNonCommentChanges =
                    dto.getComponentName() != null ||
                            dto.getFrequencyId()  != null || dto.getInputPaths()   != null ||
                            dto.getOutputPath()   != null || dto.getJobTypeId()    != null ||
                            dto.getUseCaseId()    != null || dto.getIsCritical()   != null ||
                            dto.getDomainId()     != null || dto.getBitBucketUrl() != null ||
                            dto.getResponsible()  != null || dto.getJobPhaseId()   != null ||
                            dto.getOriginTypeId() != null || dto.getException()    != null;
            boolean commentsOnly = dto.getComments() != null && !hasNonCommentChanges;
            if (commentsOnly) {
                if (TransferStatusPolicy.canWriteJobComment(dto.getActorRole(), st) != 1) {
                    return new ErrorDataResult<>(null, "409", "No puedes comentar este job en este estado");
                }
                reliabilityDao.updateJobComment(dto.getPack(), dto.getJobName(), dto.getComments());
                return new SuccessDataResult<>(null, "Comentario del job actualizado");
            }
            if (TransferStatusPolicy.canEdit(dto.getActorRole(), st) != 1) {
                return new ErrorDataResult<>(null, "409", "Solo se puede editar cuando fue devuelto");
            }
            reliabilityDao.updateJobByPackAndName(dto);
            return new SuccessDataResult<>(null, "Job actualizado");
        } catch (ReliabilityDao.PersistenceException pe) {
            return new ErrorDataResult<>(null, "404", pe.getMessage());
        } catch (Exception e) {
            log.severe(ERROR + e.getMessage());
            return new ErrorDataResult<>(null, "500", e.getMessage());
        }
    }

    public IDataResult<Void> updateCommentsForPack(String pack, String role, String comments) {
        try {
            Integer st = reliabilityDao.getPackCurrentStatus(pack);
            if (st == null) {
                return new ErrorDataResult<>(null, "404", PACK_NOT_FOUND);
            }
            if (TransferStatusPolicy.canWriteGeneralComment(role, st) != 1) {
                return new ErrorDataResult<>(null, "409", "No tienes permisos para comentar en este estado");
            }
            reliabilityDao.updatePackComments(pack, comments);
            return new SuccessDataResult<>(null, "Comentarios actualizados");
        } catch (ReliabilityDao.PersistenceException pe) {
            return new ErrorDataResult<>(null, "404", pe.getMessage());
        } catch (Exception e) {
            log.severe(ERROR + e.getMessage());
            return new ErrorDataResult<>(null, "500", e.getMessage());
        }
    }

    public IDataResult<TransferDetailResponse> getTransferDetail(String pack) {
        try {
            var detail = reliabilityDao.getTransferDetail(pack);
            if (detail == null) return new ErrorDataResult<>(null, "404", PACK_NOT_FOUND);

            if (detail.getJobs() != null) {
                for (var j : detail.getJobs()) {
                    if (j.getFrequencyChanged() == null) {
                        Integer orig = j.getOriginalFrequencyId();
                        j.setFrequencyChanged(orig != null && !Objects.equals(orig, j.getFrequencyId()));
                    }
                }
            }
            return new SuccessDataResult<>(detail);
        } catch (Exception e) {
            log.severe(e.getMessage());
            return new ErrorDataResult<>(null, "500", e.getMessage());
        }
    }

    public IDataResult<TransferDetailResponse> updateTransferDetail(
            String pack, String role, TransferDetailUpdateRequest dto) {
        try {
            Integer st = reliabilityDao.getPackCurrentStatus(pack);
            if (st == null) return new ErrorDataResult<>(null, "404", PACK_NOT_FOUND);

            String r = role == null ? "" : role.trim().toUpperCase(java.util.Locale.ROOT);

            if (st == TransferStatusPolicy.DESESTIMADO) {
                return new ErrorDataResult<>(null, "409",
                        "No se puede editar el detalle cuando el pack está desestimado");
            }

            java.util.function.Predicate<TransferDetailUpdateRequest.Job> jobOnlyComment = j ->
                    j != null &&
                            j.getJobName() != null && j.getComments() != null &&
                            j.getComponentName() == null && j.getFrequencyId() == null &&
                            j.getInputPaths() == null && j.getOutputPath() == null &&
                            j.getJobTypeId() == null && j.getUseCaseId() == null &&
                            j.getIsCritical() == null && j.getDomainId() == null &&
                            j.getBitBucketUrl() == null && j.getResponsible() == null &&
                            j.getJobPhaseId() == null && j.getOriginTypeId() == null &&
                            j.getException() == null;

            boolean headerOnlyComment =
                    dto.getHeader() == null || (dto.getHeader().getComments() != null
                            && dto.getHeader().getDomainId() == null
                            && dto.getHeader().getUseCaseId() == null);

            boolean jobsOnlyComments =
                    dto.getJobs() == null || dto.getJobs().isEmpty() ||
                            dto.getJobs().stream().allMatch(jobOnlyComment);

            boolean onlyComments = headerOnlyComment && jobsOnlyComments;

            if (st == TransferStatusPolicy.EN_PROGRESO && "SM".equals(r) && !onlyComments) {
                return new ErrorDataResult<>(null, "409",
                        "En EN_PROGRESO solo se permiten comentarios (general y por job)");
            }

            if (st == TransferStatusPolicy.APROBADO_PO && "KM".equals(r) && !onlyComments) {
                return new ErrorDataResult<>(null, "409",
                        "KM solo puede enviar comentarios (general o por job) en APROBADO_PO");
            }

            reliabilityDao.updateTransferDetail(pack, dto);
            var snapshot = reliabilityDao.getTransferDetail(pack);
            return new SuccessDataResult<>(snapshot, "Detalle actualizado");
        } catch (ReliabilityDao.PersistenceException pe) {
            return new ErrorDataResult<>(null, "404", pe.getMessage());
        } catch (Exception e) {
            log.severe(ERROR + e.getMessage());
            return new ErrorDataResult<>(null, "500", e.getMessage());
        }
    }

    public ServicePermissionResponse getServicePermissionByName(String serviceName) {
        return reliabilityDao.getServicePermissionByName(serviceName);
    }
}