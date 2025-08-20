package com.bbva.util.metaknight;

import com.bbva.dto.metaknight.request.IngestaRequestDto;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class SchemaProcessor {
    private static final String PHYSICAL_NAME_FIELD = "Physical Name field";
    private static final String OUTPUT_SCHEMA = ".output.schema\"";
    private static final String SCHEMAS_PE_PATH = "\"/schemas/pe/";
    private static final String LATEST_PATH = "/latest/"; // NOSONAR - Artifactory standard path, fixed value
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
        List<String> partitions = Arrays.asList(request.getParticiones().split(","));

        this.keys = rawData.stream()
                .filter(row -> "True".equals(String.valueOf(row.get("Key"))))
                .map(row -> (String) row.get(PHYSICAL_NAME_FIELD))
                .filter(field -> !partitions.contains(field))
                .toList();

        this.keysDict = rawData.stream()
                .filter(row -> "True".equals(String.valueOf(row.get("Key"))))
                .filter(row -> !partitions.contains(row.get(PHYSICAL_NAME_FIELD)))
                .collect(Collectors.toMap(
                        row -> (String) row.get(PHYSICAL_NAME_FIELD),
                        row -> (String) row.get("Logical Format"),
                        (existing, replacement) -> existing
                ));

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

        this.originalStagingName = rawData.stream()
                .map(row -> (String) row.get("Physical name of source object"))
                .distinct()
                .findFirst()
                .orElse("");

        this.dfStagingName = originalStagingName.replace("${?", "\"${?").replace("}", "}\"");

        String[] dfNameParts = dfRawName.split("_");
        this.tag = String.join("", Arrays.copyOfRange(dfNameParts, 2, dfNameParts.length));

        this.dfUuaa = dfRawName.split("_")[1];

        buildPaths();

        buildJsonIds();

        processKirbyFields();
    }

    private void buildPaths() {
        String artifactoryLink = "${ARTIFACTORY_UNIQUE_CACHE}\"/artifactory/\"${SCHEMAS_REPOSITORY}";

        this.artifactoryPath = artifactoryLink + SCHEMAS_PE_PATH + request.getUuaaMaster() + "/raw/" + dfRawName + LATEST_PATH + dfRawName + OUTPUT_SCHEMA;
        this.masterArtifactoryPath = artifactoryLink + SCHEMAS_PE_PATH + request.getUuaaMaster() + "/master/" + dfMasterName + LATEST_PATH + dfMasterName + OUTPUT_SCHEMA;
        this.rawArtifactoryPath = artifactoryLink + SCHEMAS_PE_PATH + request.getUuaaMaster() + "/raw/" + dfRawName + LATEST_PATH + dfRawName + OUTPUT_SCHEMA;

        this.dfStagingPath = "/in/staging/datax/" + dfUuaa + "/" + dfStagingName;
        this.dfRawPath = "/data/raw/" + dfUuaa + "/data/" + dfRawName;
        this.dfMasterPath = "/data/master/" + request.getUuaaMaster() + "/data/" + dfMasterName;
        this.subset = getSubset(Arrays.asList(request.getParticiones().split(",")));

        this.partitionList = getPartitionList(Arrays.asList(request.getParticiones().split(",")));
    }

    private void buildJsonIds() {
        String uuaa = request.getUuaaMaster();
        this.idJsonMaster = uuaa + PE_HMM_QLT + tag + "m-01";
        this.idJsonRaw = uuaa + PE_HMM_QLT + tag + "r-01";
        this.idJsonStaging = uuaa + PE_HMM_QLT + tag + "s-01";
    }

    private void processKirbyFields() {
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

        this.rawDateColumns = masterData.stream()
                .filter(row -> "date".equals(row.get("Data Type")))
                .filter(row -> !CALCULATED.equals(row.get(SOURCE_FIELD)))
                .map(row -> (String) row.get(SOURCE_FIELD))
                .toList();

        this.rawTimestampColumns = masterData.stream()
                .filter(row -> "timestamp".equals(row.get("Data Type")))
                .filter(row -> !CALCULATED.equals(row.get(SOURCE_FIELD)))
                .map(row -> (String) row.get(SOURCE_FIELD))
                .toList();

        this.masterFieldWithOriginList = masterData.stream()
                .map(row -> Arrays.asList(
                        (String) row.get(PHYSICAL_NAME_FIELD),
                        (String) row.get(SOURCE_FIELD)
                ))
                .toList();

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
            subsetBuilder.append(key).append("='\"").append("${?").append(parameter).append("}").append("\"'");
        }
        return subsetBuilder.toString();
    }

    private String getPartitionList(List<String> partitions) {
        return partitions.stream()
                .map(field -> "\"" + field + "\"")
                .collect(Collectors.joining(", "));
    }
}
