package com.bbva.service.metaknight;

import com.bbva.core.HandledException;
import com.bbva.dto.metaknight.request.IngestaRequestDto;
import com.bbva.service.IssueTicketService;
import com.bbva.util.metaknight.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class IngestaService {

    private static final String HAMMURABI_BLOCK = "hammurabi {\n";
    private static final String FALSE = "false";
    private static final String CREATED_WITH_METAKNIGHT = " created with Metaknight.";
    private static final String DESCRIPTION = "description";
    private static final String PROCESSING = "processing";
    private static final String CONFIG_URL = "configUrl";
    private static final String REPO_URL_BASE = "\"${repository.endpoint.vdc}/${repository.repo.schemas.dq}/data-quality-configs/${repository.env.dq}/per/";
    private static final String DQ_CONF_VERSION = "/${dq.conf.version}/";
    private static final String CONF_SUFFIX = "-01.conf\"";
    private static final String SPARK_HISTORY_ENABLED = "sparkHistoryEnabled";
    private static final String PARAMS = "params";
    private static final String HAMMURABI_LTS = "hammurabi-lts";
    private static final String RUNTIME = "runtime";
    private static final String STREAMING = "streaming";
    private static final String ID_BASE = "xxxxxxxx";
    private static final String CONF_EXTENSION = ".conf";
    private static final String JSON_EXTENSION = ".json";
    private static final String FIXED = "fixed";
    private static final String RULES_N = "rules = [\n";

    private SchemaProcessor schemaProcessor = new SchemaProcessor();

    private BaseFunctions baseFunctions = new BaseFunctions();

    private Rules rules = new Rules();

    private DocumentGenerator documentGenerator = new DocumentGenerator();

    private ZipGenerator zipGenerator = new ZipGenerator();

    private IssueTicketService issueTicketService = new IssueTicketService();

    private MallaGeneratorService mallaGeneratorService = new MallaGeneratorService();
    public byte[] procesarIngesta(IngestaRequestDto request) throws Exception {

        validarRequest(request);

        validarSchemas(request);

        List<Map<String, Object>> rawData = parsearCsvDesdeBase64(request.getSchemaRawBase64());
        List<Map<String, Object>> masterData = parsearCsvDesdeBase64(request.getSchemaMasterBase64());

        schemaProcessor.initialize(rawData, masterData, request);

        Map<String, Object> archivosGenerados = new HashMap<>();

        Map<String, String> hammurabiFiles = generarConfiguracionesHammurabi(request);
        archivosGenerados.putAll(hammurabiFiles);

        Map<String, String> kirbyFiles = generarConfiguracionesKirby(request);
        archivosGenerados.putAll(kirbyFiles);

        byte[] hammurabiC204 = documentGenerator.generarDocumentoC204Hammurabi(request, schemaProcessor);
        byte[] kirbyC204 = documentGenerator.generarDocumentoC204Kirby(request, schemaProcessor);

        if (request.isTieneL1T()) {
            Map<String, String> l1tFiles = generarConfiguracionesL1T(request);
            archivosGenerados.putAll(l1tFiles);
        }

        if (request.isGenerarMallas()) {
            try {
                Map<String, String> mallasXml = mallaGeneratorService.generarMallasXml(request, schemaProcessor);
                archivosGenerados.putAll(mallasXml);
            } catch (HandledException e) {
                throw new HandledException("MALLA_GENERATION_FAILED",
                        "Error generando mallas XML: " + e.getMessage(), e);
            }
        }

        Map<String, byte[]> archivosBytes = new HashMap<>();

        for (Map.Entry<String, Object> entry : archivosGenerados.entrySet()) {
            if (entry.getValue() instanceof String valor) {
                archivosBytes.put(entry.getKey(), valor.getBytes(StandardCharsets.UTF_8));
            }
        }

        archivosBytes.put("hammurabi_C204.docx", hammurabiC204);
        archivosBytes.put("kirby_C204.docx", kirbyC204);

        //MOMENTÁNEO
        try {
            issueTicketService.addLabelToIssue(request.getUsername(), request.getToken(), request.getTicketJira(), "Metaknight");
        } catch (HandledException ex) {
            if ("302".equals(ex.getCode()) || ex.getMessage().contains("TOKEN_EXPIRED")) {
                throw ex;
            }
        } catch (Exception ex) {
            // Continuar con el proceso
        }
        return zipGenerator.crearZip(archivosBytes);
    }
    private void validarSchemas(IngestaRequestDto request) {
        if (request.getSchemaRawBase64() == null || request.getSchemaRawBase64().isEmpty()) {
            throw new IllegalArgumentException("Schema Raw es requerido");
        }
        if (request.getSchemaMasterBase64() == null || request.getSchemaMasterBase64().isEmpty()) {
            throw new IllegalArgumentException("Schema Master es requerido");
        }
    }

    private List<Map<String, Object>> parsearCsvDesdeBase64(String csvBase64) throws HandledException {
        try{
            byte[] csvBytes = Base64.getDecoder().decode(csvBase64);
            String csvContent = new String(csvBytes, StandardCharsets.UTF_8);

            CSVFormat format = CSVFormat.Builder.create()
                    .setDelimiter(';')
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setQuote('"')
                    .setTrim(true)
                    .build();

            CSVParser csvParser = CSVParser.parse(csvContent, format);

            List<Map<String, Object>> records = new ArrayList<>();
            for (CSVRecord csvRecord : csvParser) {
                Map<String, Object> row = new HashMap<>();
                for (String header : csvParser.getHeaderNames()) {
                    row.put(header.trim(), csvRecord.get(header));
                }
                records.add(row);
            }
            csvParser.close();
            return records;
        }catch (Exception e){
            throw new HandledException("CSV_PARSING_ERROR", "Error parseando CSV desde Base64", e);
        }
    }

    private Map<String, String> generarConfiguracionesHammurabi(IngestaRequestDto request) {
        Map<String, String> archivos = new HashMap<>();

        String stagingConf = generarStagingHammurabi(request);
        String stagingJson = generarStagingJson(request);

        archivos.put("qlt/staging/" + schemaProcessor.getDfMasterName() + CONF_EXTENSION, stagingConf);
        archivos.put("qlt/staging/" + schemaProcessor.getDfMasterName() + JSON_EXTENSION, stagingJson);

        String rawConf = generarRawHammurabi(request);
        String rawJson = generarRawJson(request);
        archivos.put("qlt/raw/" + schemaProcessor.getDfMasterName() + CONF_EXTENSION, rawConf);
        archivos.put("qlt/raw/" + schemaProcessor.getDfMasterName() + JSON_EXTENSION, rawJson);

        String masterConf = generarMasterHammurabi(request);
        String masterJson = generarMasterJson(request);
        archivos.put("qlt/master/" + schemaProcessor.getDfMasterName() + CONF_EXTENSION, masterConf);
        archivos.put("qlt/master/" + schemaProcessor.getDfMasterName() + JSON_EXTENSION, masterJson);

        return archivos;
    }

    private Map<String, String> generarConfiguracionesKirby(IngestaRequestDto request) {
        Map<String, String> archivos = new HashMap<>();

        String rawConf = generarKirbyRaw(request);
        String rawJson = generarKirbyRawJson(request);
        archivos.put("kirby/raw/" + schemaProcessor.getDfMasterName() + CONF_EXTENSION, rawConf);
        archivos.put("kirby/raw/" + schemaProcessor.getDfMasterName() + JSON_EXTENSION, rawJson);

        String masterConf = generarKirbyMaster(request);
        String masterJson = generarKirbyMasterJson(request);
        archivos.put("kirby/master/" + schemaProcessor.getDfMasterName() + CONF_EXTENSION, masterConf);
        archivos.put("kirby/master/" + schemaProcessor.getDfMasterName() + JSON_EXTENSION, masterJson);

        return archivos;
    }

    private Map<String, String> generarConfiguracionesL1T(IngestaRequestDto request) {
        Map<String, String> archivos = new HashMap<>();

        String hammurabiL1TConf = generarHammurabiL1T(request);
        String hammurabiL1TJson = generarHammurabiL1TJson(request);
        archivos.put("qlt/l1t/" + schemaProcessor.getDfMasterName() + "_l1t.conf", hammurabiL1TConf);
        archivos.put("qlt/l1t/" + schemaProcessor.getDfMasterName() + "_l1t.json", hammurabiL1TJson);

        // L1T Kirby
        String kirbyL1TConf = generarKirbyL1T(request);
        String kirbyL1TJson = generarKirbyL1TJson(request);
        archivos.put("kirby/l1t/" + schemaProcessor.getDfMasterName() + "_l1t.conf", kirbyL1TConf);
        archivos.put("kirby/l1t/" + schemaProcessor.getDfMasterName() + "_l1t.json", kirbyL1TJson);

        return archivos;
    }

    private String generarStagingHammurabi(IngestaRequestDto request) {
        StringBuilder config = new StringBuilder();

        config.append(HAMMURABI_BLOCK);

        Map<String, Object> dataFrameInfo = new HashMap<>();
        dataFrameInfo.put("targetPathName", schemaProcessor.getDfRawPath());
        dataFrameInfo.put("physicalTargetName", schemaProcessor.getDfRawName());
        dataFrameInfo.put("uuaa", request.getUuaaMaster());

        config.append(baseFunctions.convertStagingJsonToSelectedFormat(dataFrameInfo));
        config.append("\n");

        Map<String, Object> options = new HashMap<>();
        options.put("delimiter", request.getDelimitador());
        options.put("header", FALSE);
        options.put("castMode", "notPermissive");
        options.put("charset", "UTF-8");

        Map<String, Object> schema = new HashMap<>();
        schema.put("path", schemaProcessor.getArtifactoryPath());

        String inputType = "host".equals(request.getTipoArchivo()) ? FIXED : request.getTipoArchivo();

        Map<String, Object> inputData = new HashMap<>();
        inputData.put("options", options);
        inputData.put("paths", Arrays.asList(schemaProcessor.getDfStagingPath()));
        inputData.put("schema", schema);
        inputData.put("type", inputType);

        Map<String, Object> inputWrapper = new HashMap<>();
        inputWrapper.put("input", inputData);

        config.append(baseFunctions.convertStagingInputToSelectedFormat(inputWrapper));
        config.append("\n");

        config.append(generarReglasStagingConRules());

        config.append("\n}");

        return config.toString();
    }
    private String generarReglasStagingConRules() {
        StringBuilder rulesConfig = new StringBuilder();
        rulesConfig.append(RULES_N);

        rulesConfig.append(baseFunctions.convertToCustomFormat(rules.stagingRule("staging")));
        rulesConfig.append(",\n");

        for (String key : schemaProcessor.getKeys()) {
            Map<String, Object> rule = rules.applyValidityRule(key, ID_BASE);
            rulesConfig.append(baseFunctions.convertToCustomFormat(rule));
            rulesConfig.append(",\n");
        }

        for (String key : schemaProcessor.getKeys()) {
            String regularExp = baseFunctions.getRegularExpression(schemaProcessor.getKeysDict().get(key));
            Map<String, Object> rule = rules.applyFormatRule(key, regularExp, ID_BASE);
            rulesConfig.append(baseFunctions.convertToCustomFormat(rule));
            rulesConfig.append(",\n");
        }

        Map<String, Object> duplicateRule = rules.applyDuplicateRule(schemaProcessor.getKeys(), ID_BASE);
        rulesConfig.append(baseFunctions.convertToCustomFormat(duplicateRule));
        rulesConfig.append("]");

        return rulesConfig.toString();
    }

    private String generarStagingJson(IngestaRequestDto request) {
        Map<String, Object> json = new HashMap<>();
        json.put("_id", schemaProcessor.getIdJsonStaging());
        json.put(DESCRIPTION, "Job " + schemaProcessor.getIdJsonStaging() + CREATED_WITH_METAKNIGHT);
        json.put("kind", PROCESSING);

        Map<String, Object> params = new HashMap<>();
        params.put(CONFIG_URL, REPO_URL_BASE +
                request.getUuaaMaster() + "/staging/" + schemaProcessor.getDfRawName() + DQ_CONF_VERSION +
                schemaProcessor.getDfRawName() + CONF_SUFFIX);
        params.put(SPARK_HISTORY_ENABLED, FALSE);

        json.put(PARAMS, params);
        json.put(RUNTIME, HAMMURABI_LTS);
        json.put("size", "M");
        json.put(STREAMING, false);

        return baseFunctions.convertFinalJsonToSelectedFormat(json);
    }

    private String generarRawHammurabi(IngestaRequestDto request) {
        StringBuilder config = new StringBuilder();
        config.append(HAMMURABI_BLOCK);

        config.append(String.format("""
            dataFrameInfo {
                cutoffDate = ${?CUTOFF_DATE}
                targetPathName = "%s"
                physicalTargetName = "%s"
                subset = "%s"
                uuaa = "%s"
            }
            """,
                schemaProcessor.getDfRawPath(),
                schemaProcessor.getDfRawName(),
                schemaProcessor.getSubset(),
                request.getUuaaMaster()
        ));

        // Input
        config.append(String.format("""
            input {
                applyConversions = false
                paths = [
                    "%s"
                ]
                schema {
                    path = %s
                }
                type = "avro"
            }
            """,
                schemaProcessor.getDfRawPath(),
                schemaProcessor.getArtifactoryPath()
        ));

        Map<String, Object> rawRule = rules.rawCompletenessRule(
                schemaProcessor.getDfStagingPath(),
                schemaProcessor.getArtifactoryPath(),
                "288735c36b"
        );

        config.append(RULES_N);
        config.append(baseFunctions.convertToCustomFormat(rawRule));
        config.append("\n]");

        config.append("\n}");
        return config.toString();
    }
    private String generarRawJson(IngestaRequestDto request) {
        Map<String, Object> json = new HashMap<>();
        json.put("_id", schemaProcessor.getIdJsonRaw());
        json.put(DESCRIPTION, "Job " + schemaProcessor.getIdJsonRaw() + CREATED_WITH_METAKNIGHT);
        json.put("kind", PROCESSING);

        Map<String, Object> params = new HashMap<>();

        params.put(CONFIG_URL, REPO_URL_BASE +
                request.getUuaaMaster() + "/rawdata/" + schemaProcessor.getDfRawName() + DQ_CONF_VERSION+
                schemaProcessor.getDfRawName() + CONF_SUFFIX);

        params.put(SPARK_HISTORY_ENABLED, FALSE);
        json.put(PARAMS, params);

        json.put(RUNTIME, HAMMURABI_LTS);
        json.put("size", "M");
        json.put(STREAMING, false);

        return baseFunctions.convertFinalJsonToSelectedFormat(json);
    }

    private String generarMasterHammurabi(IngestaRequestDto request) {
        StringBuilder config = new StringBuilder();
        config.append(HAMMURABI_BLOCK);

        // DataFrameInfo
        config.append(String.format("""
            dataFrameInfo {
                cutoffDate = ${?REPROCESS_DATE}
                targetPathName = "%s"
                physicalTargetName = "%s"
                subset = "%s"
                uuaa = "%s"
            }
            """,
                schemaProcessor.getDfMasterPath(),
                schemaProcessor.getDfMasterName(),
                schemaProcessor.getSubset(),
                request.getUuaaMaster()
        ));

        // Input
        config.append(String.format("""
            input {
                options {
                    includeMetadataAndDeleted = true
                    overrideSchema = true
                }
                paths = [
                    "%s"
                ]
                schema {
                    path = %s
                }
                type = "parquet"
            }
            """,
                schemaProcessor.getDfMasterPath(),
                schemaProcessor.getMasterArtifactoryPath()
        ));

        Map<String, Object> masterRule = rules.masterCompletenessRule(
                schemaProcessor.getDfRawPath(),
                schemaProcessor.getArtifactoryPath(),
                schemaProcessor.getSubset(),
                ID_BASE
        );

        config.append(RULES_N);
        config.append(baseFunctions.convertToCustomFormat(masterRule));
        config.append("\n]");

        config.append("\n}");
        return config.toString();
    }
    private String generarMasterJson(IngestaRequestDto request) {
        Map<String, Object> json = new HashMap<>();
        json.put("_id", schemaProcessor.getIdJsonMaster());
        json.put(DESCRIPTION, "Job " + schemaProcessor.getIdJsonMaster() + CREATED_WITH_METAKNIGHT);
        json.put("kind", PROCESSING);

        Map<String, Object> params = new HashMap<>();
        params.put(CONFIG_URL, REPO_URL_BASE +
                request.getUuaaMaster() + "/masterdata/" + schemaProcessor.getDfRawName() + DQ_CONF_VERSION +
                schemaProcessor.getDfRawName() + CONF_SUFFIX);
        params.put(SPARK_HISTORY_ENABLED, "true");
        json.put(PARAMS, params);

        json.put(RUNTIME, HAMMURABI_LTS);
        json.put("size", "M");
        json.put(STREAMING, false);

        return baseFunctions.convertFinalJsonToSelectedFormat(json);
    }

    private String generarKirbyRaw(IngestaRequestDto request) {
        StringBuilder config = new StringBuilder();
        config.append("kirby {\n");

        String inputType = request.getTipoArchivo();

        if ("host".equals(inputType)) {
            inputType = FIXED;
        }

        String options = generarKirbyRawOptions(inputType, request.getDelimitador());

        config.append(String.format("""
            input {
                %s
                paths=[
                    "%s"
                ]
                schema {
                    path=%s
                }
                type="%s"
            }
            """,
                    options,
                    schemaProcessor.getDfStagingPath(),
                    schemaProcessor.getRawArtifactoryPath(),
                    inputType
        ));

        String partitionFilter = request.isTieneCompactacion() ?
                "partitionDiscovery = true" :
                "partitionsFilter = \"" + schemaProcessor.getSubset() + "\"";

        config.append(String.format("""
            output {
                mode = "overwrite"
                compact = ${COMPACT_VALUE}
                compactConfig {
                    forceTargetPathRemove = true
                    report = true
                    %s
                }
                force = true
                options {
                    partitionOverwriteMode = "dynamic"
                }
                partition = [
                    %s
                ]
                path="%s"
                schema {
                    path=%s
                }
                type="avro"
            }
            """,
                partitionFilter,
                schemaProcessor.getPartitionList(),
                schemaProcessor.getDfRawPath(),
                schemaProcessor.getRawArtifactoryPath()
        ));

        // Transformations
        config.append("""
            transformations = [
                {
                    type = "literal"
                    field = "cutoff_date"
                    default = ${?DATE}
                    defaultType = "string"
                },
                {
                    type = "setCurrentDate"
                    field = "audtiminsert_date"
                },
                {
                    type = "formatter"
                    field = "audtiminsert_date"
                    typeToCast = "string"
                }
            ]
            """);

        config.append("\n}");
        return config.toString();
    }

    private String generarKirbyRawOptions(String inputType, String delimiter) {
        if (FIXED.equals(inputType)) {
            return """
            options {
                castMode = "notPermissive"
                charset = "UTF-8"
                header = "false"
            }
            """;
        } else if ("csv".equals(inputType)) {
            return String.format("""
            options {
                delimiter = "%s"
                header = "True"
                castMode = "notPermissive"
                charset = "UTF-8"
                includeMetadataAndDeleted = "true"
            }
            """, delimiter);
        } else {
            return """
            options {
                castMode = "notPermissive"
                charset = "UTF-8"
                header = "false"
            }
            """;
        }
    }

    private String generarKirbyRawJson(IngestaRequestDto request) {
        return String.format("""
            {
                "_id" : "%s-pe-krb-inr-%sp-01",
                "description" : "Job %s-pe-krb-inr-%sp-01 created with Metaknight.",
                "kind" : "processing",
                "params" : {
                    "configUrl" : "${repository.endpoint.vdc}/${repository.repo.schemas}/kirby/pe/%s/raw/%s/${version}/%s.conf",
                    "sparkHistoryEnabled" : "false"
                },
                "env" : {
                    "COMPACT_VALUE": "true"
                },
                "runtime" : "kirby3-lts",
                "size" : "M",
                "streaming" : false
            }
            """,
                request.getUuaaMaster(),
                schemaProcessor.getTag(),
                request.getUuaaMaster(),
                schemaProcessor.getTag(),
                request.getUuaaMaster(),
                schemaProcessor.getDfRawName(),
                schemaProcessor.getDfRawName()
        );
    }

    private String generarKirbyMaster(IngestaRequestDto request) {
        StringBuilder config = new StringBuilder();
        config.append("kirby {\n");

        // Input
        config.append(String.format("""
            input {
                applyConversions = false
                options {
                    includeMetadataAndDeleted = true
                    overrideSchema = true
                }
                paths=[
                    "%s"
                ]
                schema {
                    path= %s
                }
                type=avro
            }
            """,
                schemaProcessor.getDfRawPath(),
                schemaProcessor.getRawArtifactoryPath()
        ));

        // Output
        String partitionFilter = request.isTieneCompactacion() ?
                "partitionDiscovery = true" :
                "partitionsFilter = \"" + schemaProcessor.getSubset() + "\"";

        config.append(String.format("""
            output {
                mode = overwrite
                force = true
                options {
                    partitionOverwriteMode = "dynamic"
                    keepPermissions = true
                }
                partition=[
                    %s
                ]
                path="%s"
                schema {
                    path=%s
                }
                compact = ${COMPACT_VALUE}
                compactConfig {
                    forceTargetPathRemove = true
                    report = true
                    %s
                }
                type=parquet
                dropLeftoverFields=true
            }
            """,
                schemaProcessor.getPartitionList(),
                schemaProcessor.getDfMasterPath(),
                schemaProcessor.getMasterArtifactoryPath(),
                partitionFilter
        ));

        // Transformations
        config.append(generarTransformacionesKirbyMaster());

        config.append("\n}");
        return config.toString();
    }

    private String generarTransformacionesKirbyMaster() {
        StringBuilder transformations = new StringBuilder();
        transformations.append("transformations=[\n");

        // SQL Filter
        transformations.append(String.format("""
            {
                type = "sqlFilter"
                filter = "%s"
            },
            """, schemaProcessor.getSubset()));

        // Trim
        transformations.append(String.format("""
            {
                field = %s
                regex = true
                trimType = "both"
                type = "trim"
            },
            """, schemaProcessor.getTrimAllColumns()));

        // Audit fields
        transformations.append("""
            {
                type = "literal"
                field = "cutoff_date"
                default = ${?CUTOFF_ODATE}
                defaultType = "string"
            },
            {
                field = "cutoff_date"
                type = "dateformatter"
                format = "yyyyMMdd"
            },
            {
                type = "formatter"
                field = "cutoff_date"
                regex = true
                replacements = []
                typeToCast = "date"
            },
            {
                type = "setCurrentDate"
                field = "audtiminsert_date"
            },
            {
                type = "formatter"
                field = "audtiminsert_date"
                replacements = []
                typeToCast = "timestamp"
            },
            """);

        // Date fields
        for (String column : schemaProcessor.getRawDateColumns()) {
            transformations.append(String.format("""
                {
                   field="%s"
                   type = "dateformatter"
                   format = "yyyy-MM-dd"
                   operation = "parse"
                   castMode = notPermissive
                },
                """, column));
        }

        // Timestamp fields
        for (String column : schemaProcessor.getRawTimestampColumns()) {
            transformations.append(String.format("""
                {
                    field="%s"
                    type="dateformatter"
                    format="yyyy-MM-dd-HH.mm.ss.SSSSSS"
                    reformat="yyyy-MM-dd HH:mm:ss.SSSSSS"
                    operation = parseTimestamp
                    locale = es
                    castMode = notPermissive
                },
                """, column));
        }

        transformations.append("{\n    type : \"renamecolumns\"\n    columnsToRename : {\n");
        for (List<String> field : schemaProcessor.getMasterFieldWithOriginList()) {
            if (!"Calculated".equals(field.get(1))) {
                transformations.append(String.format(
                        "         %s = \"%s\",%n", field.get(1), field.get(0))
                );
            }
        }
        transformations.setLength(transformations.length() - 2); // Remove last comma
        transformations.append("\n    }\n},\n");

        transformations.append("{\n    type = \"selectcolumns\"\n    columnsToSelect = [\n");
        for (String field : schemaProcessor.getMasterFieldList()) {
            transformations.append(String.format("         \"%s\",%n", field));
        }
        transformations.setLength(transformations.length() - 2);
        transformations.append("\n    ]\n}\n");

        transformations.append("]\n");

        return transformations.toString();
    }

    private String generarKirbyMasterJson(IngestaRequestDto request) {
        return String.format("""
            {
                "_id" : "%s-pe-krb-inm-%sp-01",
                "description" : "Job %s-pe-krb-inm-%sp-01 created with Metaknight.",
                "kind" : "processing",
                "params" : {
                    "configUrl" : "${repository.endpoint.vdc}/${repository.repo.schemas}/kirby/pe/%s/master/%s/${version}/%s.conf",
                    "sparkHistoryEnabled" : "false"
                },
                "env" : {
                    "COMPACT_VALUE": "true"
                },
                "runtime" : "kirby3-lts",
                "size" : "M",
                "streaming" : false
            }
            """,
                request.getUuaaMaster(),
                schemaProcessor.getTag(),
                request.getUuaaMaster(),
                schemaProcessor.getTag(),
                request.getUuaaMaster(),
                schemaProcessor.getDfMasterName(),
                schemaProcessor.getDfMasterName()
        );
    }

    private String generarKirbyL1T(IngestaRequestDto request) {
        return String.format("""
            kirby {
                input {
                    options {
                        includeMetadataAndDeleted = "true"
                        overrideSchema = "true"
                    }
                    paths=[
                        "%s"
                    ]
                    schema {
                        path = ${ARTIFACTORY_UNIQUE_CACHE}"/artifactory/"${SCHEMAS_REPOSITORY}"/schemas/pe/%s/master/%s/latest/%s.output.schema"
                    }
                    type=parquet
                }
                output {
                    force = "true"
                    mode= "overwrite"
                    options {
                        partitionOverwriteMode= "dynamic"
                    }
                    partition=[
                        %s
                    ]
                    dropLeftoverFields = "true"
                    path="%s_l1t"
                    schema {
                        path = ${ARTIFACTORY_UNIQUE_CACHE}"/artifactory/"${SCHEMAS_REPOSITORY}"/schemas/pe/%s/master/%s_l1t/latest/%s_l1t.output.schema"
                    }
                    type=parquet
                }
                transformations=[
                    {
                        type = "sqlFilter"
                        filter = "%s"
                    }
                ]
            }
            """,
                schemaProcessor.getDfMasterPath(),
                request.getUuaaMaster(),
                schemaProcessor.getDfMasterName(),
                schemaProcessor.getDfMasterName(),
                schemaProcessor.getPartitionList(),
                schemaProcessor.getDfMasterPath(),
                request.getUuaaMaster(),
                schemaProcessor.getDfMasterName(),
                schemaProcessor.getDfMasterName(),
                schemaProcessor.getSubset()
        );
    }

    private String generarKirbyL1TJson(IngestaRequestDto request) {
        return String.format("""
            {
                "_id" : "%s-pe-krb-inm-%sl1tp-01",
                "description" : "Job %s-pe-krb-inm-%sl1tp-01 created with Metaknight.",
                "kind" : "processing",
                "params" : {
                    "configUrl" : "${repository.endpoint.vdc}/${repository.repo.schemas}/kirby/pe/%s/master/%s_l1t/${version}/%s_l1t.conf",
                    "sparkHistoryEnabled" : "false"
                },
                "runtime" : "kirby3-lts",
                "size" : "M",
                "streaming" : false
            }
            """,
                request.getUuaaMaster(),
                schemaProcessor.getTag(),
                request.getUuaaMaster(),
                schemaProcessor.getTag(),
                request.getUuaaMaster(),
                schemaProcessor.getDfMasterName(),
                schemaProcessor.getDfMasterName()
        );
    }

    private void validarRequest(IngestaRequestDto request) throws IllegalArgumentException {
        List<String> errores = new ArrayList<>();

        if (request == null) {
            throw new IllegalArgumentException("Request no puede ser nulo");
        }

        if (request.getUuaaMaster() == null || request.getUuaaMaster().trim().isEmpty()) {
            errores.add("UUAA Master es requerido");
        }

        if (request.getDelimitador() == null || request.getDelimitador().trim().isEmpty()) {
            errores.add("Delimitador es requerido");
        }

        if (request.getTipoArchivo() == null || request.getTipoArchivo().trim().isEmpty()) {
            errores.add("Tipo de archivo es requerido");
        }

        if (request.getParticiones() == null || request.getParticiones().trim().isEmpty()) {
            errores.add("Particiones son requeridas");
        }

        if (!errores.isEmpty()) {
            throw new IllegalArgumentException("Errores de validación: " + String.join(", ", errores));
        }
    }

    private String generarHammurabiL1T(IngestaRequestDto request) {
        StringBuilder config = new StringBuilder();
        config.append(HAMMURABI_BLOCK);

        config.append(String.format("""
        dataFrameInfo {
            cutoffDate = ${?DATE}
            targetPathName = "%s_l1t"
            physicalTargetName = "%s_l1t"
            uuaa = "%s"
            subset = "%s"
        }
        """,
                schemaProcessor.getDfMasterPath(),
                schemaProcessor.getDfMasterName(),
                request.getUuaaMaster(),
                schemaProcessor.getSubset()
        ));

        config.append(String.format("""
        input {
            options {
                includeMetadataAndDeleted = "true"
                overrideSchema = "true"
            }
            paths = [
                "%s_l1t"
            ]
            schema {
                path=${ARTIFACTORY_UNIQUE_CACHE}"/artifactory/"${SCHEMAS_REPOSITORY}"/schemas/pe/%s/master/%s_l1t/latest/%s_l1t.output.schema"
            }
            type = "parquet"
        }
        """,
                schemaProcessor.getDfMasterPath(),
                request.getUuaaMaster(),
                schemaProcessor.getDfMasterName(),
                schemaProcessor.getDfMasterName()
        ));

        Map<String, Object> l1tRule = rules.hammurabiL1TRule(
                schemaProcessor.getDfMasterPath(),
                request.getUuaaMaster(),
                schemaProcessor.getDfMasterName(),
                schemaProcessor.getSubset(),
                ID_BASE
        );

        config.append(RULES_N);
        config.append(baseFunctions.convertToCustomFormat(l1tRule));
        config.append("\n]");

        config.append("\n}");
        return config.toString();
    }
    private String generarHammurabiL1TJson(IngestaRequestDto request) {
        return String.format("""
        {
            "_id" : "%s-pe-spk-qlt-%sl1tm-01",
            "description" : "Job %s-pe-hmm-spk-%sl1tm-01 created with Metaknight.",
            "kind" : "processing",
            "params" : {
                "configUrl" : "${repository.endpoint.vdc}/${repository.repo.schemas.dq}/data-quality-configs/${repository.env.dq}/per/%s/masterdata/%s_l1t/${dq.conf.version}/%s_l1t.conf",
                "sparkHistoryEnabled" : "false"
            },
            "runtime" : "hammurabi-lts",
            "size" : "M",
            "streaming" : false
        }
        """,
                request.getUuaaMaster(),
                schemaProcessor.getTag(),
                request.getUuaaMaster(),
                schemaProcessor.getTag(),
                request.getUuaaMaster(),
                schemaProcessor.getDfMasterName(),
                schemaProcessor.getDfMasterName()
        );
    }
}