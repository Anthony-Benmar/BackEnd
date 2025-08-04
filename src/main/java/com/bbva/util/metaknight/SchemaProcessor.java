package com.bbva.util.metaknight;

import com.bbva.dto.metaknight.request.IngestaRequestDto;
import lombok.Getter;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class SchemaProcessor {

    private static final String PHYSICAL_NAME_FIELD = "Physical Name field";
    private static final String OUTPUT_SCHEMA = ".output.schema\"";
    private static final String SCHEMAS_PE_PATH = "\"/schemas/pe/";

    // NOSONAR - Artifactory standard path, fixed value
    private static final String LATEST_PATH = "/latest/";
    private static final String PE_HMM_QLT = "-pe-hmm-qlt-";
    private static final String SOURCE_FIELD = "Source field";
    private static final String CALCULATED = "Calculated";

    private List<Map<String, Object>> rawData;
    private List<Map<String, Object>> masterData;
    private IngestaRequestDto request;

    private List<String> keys;
    private Map<String, String> keysDict;
    private String dfName;
    private String dfMasterName;
    private String dfRawName;
    private String tag;
    private String dfUuaa;
    private String originalStagingName;
    private String dfStagingName;
    private String subset;
    private String partitionList;

    private String artifactoryPath;
    private String masterArtifactoryPath;
    private String rawArtifactoryPath;
    private String dfStagingPath;
    private String dfRawPath;
    private String dfMasterPath;

    private String idJsonMaster;
    private String idJsonRaw;
    private String idJsonStaging;

    private String trimAllColumns;
    private List<String> rawDateColumns;
    private List<String> rawTimestampColumns;
    private List<List<String>> masterFieldWithOriginList;
    private List<String> masterFieldList;

    public void initialize(List<Map<String, Object>> rawData, List<Map<String, Object>> masterData, IngestaRequestDto request) {
        this.rawData = rawData;
        this.masterData = masterData;
        this.request = request;

        processSchemaData();
    }

    private void processSchemaData() {
        // Procesar particiones
        List<String> partitions = Arrays.asList(request.getParticiones().split(","));

        // Equivalente a: df_filtrado = df_csv[df_csv["Key"] == True][~df_csv["Physical Name field"].isin(partiton_values)]
        this.keys = rawData.stream()
                .filter(row -> "True".equals(String.valueOf(row.get("Key"))))
                .map(row -> (String) row.get(PHYSICAL_NAME_FIELD))
                .filter(field -> !partitions.contains(field))
                .toList();

        // Equivalente a: dict(zip(df_filtrado["Physical Name field"], df_filtrado["Logical Format"]))
        this.keysDict = rawData.stream()
                .filter(row -> "True".equals(String.valueOf(row.get("Key"))))
                .filter(row -> !partitions.contains(row.get(PHYSICAL_NAME_FIELD)))
                .collect(Collectors.toMap(
                        row -> (String) row.get(PHYSICAL_NAME_FIELD),
                        row -> (String) row.get("Logical Format"),
                        (existing, replacement) -> existing
                ));

        // Equivalente a: df_csv["Physical name object"].drop_duplicates().to_list()[0]
        this.dfRawName = rawData.stream()
                .map(row -> (String) row.get("Physical name object"))
                .distinct()
                .findFirst()
                .orElse("");

        this.dfMasterName = masterData.stream()
                .map(row -> (String) row.get("Physical name object"))
                .distinct()
                .findFirst()
                .orElse("");

        // Equivalente a: df_csv["Physical name of source object"].drop_duplicates().to_list()[0]
        this.originalStagingName = rawData.stream()
                .map(row -> (String) row.get("Physical name of source object"))
                .distinct()
                .findFirst()
                .orElse("");

        // Procesar staging name
        this.dfStagingName = "\"" + originalStagingName.replace("$", "\"$");

        // Equivalente a: "".join(df_name.split("_")[2:])
        String[] dfNameParts = dfRawName.split("_");
        this.tag = String.join("", Arrays.copyOfRange(dfNameParts, 2, dfNameParts.length));

        // Obtener UUAA
        this.dfUuaa = dfRawName.split("_")[1];

        // Construir paths
        buildPaths();

        // Construir IDs
        buildJsonIds();

        // Procesar campos para Kirby
        processKirbyFields();
    }

    private void buildPaths() {
        String artifactoryLink = "${ARTIFACTORY_UNIQUE_CACHE}\"/artifactory/\"${SCHEMAS_REPOSITORY}";

        // Artifactory paths
        this.artifactoryPath = artifactoryLink + SCHEMAS_PE_PATH + request.getUuaaMaster() + "/raw/" + dfRawName + LATEST_PATH + dfRawName + OUTPUT_SCHEMA;
        this.masterArtifactoryPath = artifactoryLink + SCHEMAS_PE_PATH + request.getUuaaMaster() + "/master/" + dfMasterName + LATEST_PATH + dfMasterName + OUTPUT_SCHEMA;
        this.rawArtifactoryPath = artifactoryLink + SCHEMAS_PE_PATH + request.getUuaaMaster() + "/raw/" + dfRawName + LATEST_PATH + dfRawName + OUTPUT_SCHEMA;

        // Data paths
        // Usar File.separator en lugar de hard-coded path-delimiter
        this.dfStagingPath = File.separator + "in" + File.separator + "staging" + File.separator + "datax" + File.separator + dfUuaa + File.separator + dfStagingName;
        this.dfRawPath = File.separator + "data" + File.separator + "raw" + File.separator + dfUuaa + File.separator + "data" + File.separator + dfRawName;
        this.dfMasterPath = File.separator + "data" + File.separator + "master" + File.separator + request.getUuaaMaster() + File.separator + "data" + File.separator + dfMasterName;

        // Subset para particiones
        this.subset = getSubset(Arrays.asList(request.getParticiones().split(",")));

        // Partition list
        this.partitionList = getPartitionList(Arrays.asList(request.getParticiones().split(",")));
    }

    private void buildJsonIds() {
        String uuaa = request.getUuaaMaster();
        this.idJsonMaster = uuaa + PE_HMM_QLT + tag + "m-01";
        this.idJsonRaw = uuaa + PE_HMM_QLT + tag + "r-01";
        this.idJsonStaging = uuaa + PE_HMM_QLT + tag + "s-01";
    }

    private void processKirbyFields() {
        // Trim all columns
        StringBuilder trimColumns = new StringBuilder("\"");
        List<String> sourceFields = masterData.stream()
                .filter(row -> !CALCULATED.equals(row.get(SOURCE_FIELD)))
                .map(row -> (String) row.get(SOURCE_FIELD))
                .toList();

        for (int i = 0; i < sourceFields.size(); i++) {
            trimColumns.append(sourceFields.get(i));
            if (i < sourceFields.size() - 1) {
                trimColumns.append("|");
            }
        }
        trimColumns.append("\"");
        this.trimAllColumns = trimColumns.toString();

        // Date columns
        this.rawDateColumns = masterData.stream()
                .filter(row -> "date".equals(row.get("Data Type")))
                .filter(row -> !CALCULATED.equals(row.get(SOURCE_FIELD)))
                .map(row -> (String) row.get(SOURCE_FIELD))
                .toList();

        // Timestamp columns
        this.rawTimestampColumns = masterData.stream()
                .filter(row -> "timestamp".equals(row.get("Data Type")))
                .filter(row -> !CALCULATED.equals(row.get(SOURCE_FIELD)))
                .map(row -> (String) row.get(SOURCE_FIELD))
                .toList();

        // Master field with origin list
        this.masterFieldWithOriginList = masterData.stream()
                .map(row -> Arrays.asList(
                        (String) row.get(PHYSICAL_NAME_FIELD),
                        (String) row.get(SOURCE_FIELD)
                ))
                .toList();

        // Master field list
        this.masterFieldList = masterData.stream()
                .map(row -> (String) row.get(PHYSICAL_NAME_FIELD))
                .toList();
    }

    private String getSubset(List<String> partitions) {
        StringBuilder subsetBuilder = new StringBuilder();
        for (int i = 0; i < partitions.size(); i++) {
            String key = partitions.get(i);
            String parameter = "PARAMETER" + (i + 1);
            if ("cutoff_date".equals(key)) {
                parameter = "DATE";
            }
            if (i > 0) {
                subsetBuilder.append(" and ");
            }
            subsetBuilder.append(key).append("='\"${?").append(parameter).append("}\"'");
        }
        return subsetBuilder.toString();
    }

    private String getPartitionList(List<String> partitions) {
        return partitions.stream()
                .map(field -> "\"" + field + "\"")
                .collect(Collectors.joining(", "));
    }
}
