package com.bbva.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.dictionary.request.GenerationFieldChooseRequest;
import com.bbva.dto.dictionary.request.GenerationFinalizeRequest;
import com.bbva.dto.dictionary.request.GenerationSearchRequest;
import com.bbva.dto.dictionary.response.GenerationDetailResponse;
import com.bbva.dto.dictionary.response.GenerationMasterResponse;
import com.bbva.dto.dictionary.response.GenerationResponse;
import com.bbva.entities.dictionary.FieldDatumEntity;
import com.bbva.service.dictionary.FieldDatumService;
import com.bbva.service.dictionary.GenerationFieldService;
import com.bbva.service.dictionary.GenerationService;
import com.bbva.util.enums.FileFormatType;

@Path("/dictionary/generation")
public class DictionaryGenerationResources {
    
    @POST
	@Path("/create")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public IDataResult<GenerationResponse> crear(@Context HttpServletRequest httpRequest, MultipartFormDataInput multipart) {
		return GenerationService.getInstance().crear(httpRequest, multipart);
	}

	@POST
	@Path("/finalize")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public IDataResult<GenerationMasterResponse> crear(GenerationFinalizeRequest request) {
		return GenerationService.getInstance().finalizar(request);
	}

	@GET
	@Path("/search/{generationId}")
	@Produces(MediaType.APPLICATION_JSON)
	public IDataResult<GenerationResponse> buscar(@PathParam("generationId") Integer generationId) {
        return GenerationService.getInstance().buscar(generationId);
	}

	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public IDataResult<List<GenerationMasterResponse>> buscar(@Context HttpServletRequest httpRequest,
																@QueryParam("projectId") Integer projectId,
																@QueryParam("sourceId") @DefaultValue("0") String sourceId,
																@QueryParam("sourceName") @DefaultValue("") String sourceName,
																@QueryParam("startDate") @DefaultValue("") String startDate,
																@QueryParam("endingDate") @DefaultValue("") String endingDate,
																@QueryParam("ownerRecordOnly") @DefaultValue("false") Boolean ownerRecordOnly) {
        return GenerationService.getInstance().buscar(httpRequest, GenerationSearchRequest.builder()
																							.projectId(projectId)
																							.sourceId(sourceId)
																							.sourceName(sourceName)
																							.startDate(startDate)
																							.endingDate(endingDate)
																							.ownerRecordOnly(ownerRecordOnly).build());
	}

	@GET
	@Path("/field/search/{generationId}")
	@Produces(MediaType.APPLICATION_JSON)
	public IDataResult<List<GenerationDetailResponse>> buscarDetalle(@PathParam("generationId") Integer generationId) {
        return GenerationFieldService.getInstance().buscar(generationId);
	}

	@GET
	@Path("/field/datum/filter")
	@Produces(MediaType.APPLICATION_JSON)
	public IDataResult<List<FieldDatumEntity>> filtrarDatum(@QueryParam("physicalFieldName") @DefaultValue("") String physicalFieldName) {
        return FieldDatumService.getInstance().filtrar(physicalFieldName);
	}

	@POST
	@Path("/field/choose")
	@Produces(MediaType.APPLICATION_JSON)
	public IDataResult<GenerationFieldChooseRequest> seleccionarDatum(GenerationFieldChooseRequest request) {
        return GenerationFieldService.getInstance().escogerDatum(request);
	}

	@GET
    @Path("/download/{generationId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response descargarDiccionario(@PathParam("generationId") Integer generationId) throws IOException {
        Map<String, Object> result = GenerationService.getInstance().descargarDiccionario(generationId);
		Response.ResponseBuilder response = Response.ok(new ByteArrayInputStream((byte[]) result.get("fileData")), FileFormatType.XLSX.getTipo());
		response.header("Content-Disposition", String.format("attachment; filename=\"%s\"", (String)result.get("fileName")));
		response.header("Content-Type", FileFormatType.XLSX.getTipo());
		response.header("Access-Control-Expose-Headers", "*");
		return response.build();
    }

}
