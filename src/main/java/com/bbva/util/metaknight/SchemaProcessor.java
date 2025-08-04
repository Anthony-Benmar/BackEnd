package com.bbva.util.metaknight;

import com.bbva.dto.metaknight.request.IngestaRequestDto;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class SchemaProcessor {

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
                .map(row -> (String) row.get("Physical Name field"))
                .filter(field -> !partitions.contains(field))
                .collect(Collectors.toList());

        // Equivalente a: dict(zip(df_filtrado["Physical Name field"], df_filtrado["Logical Format"]))
        this.keysDict = rawData.stream()
                .filter(row -> "True".equals(String.valueOf(row.get("Key"))))
                .filter(row -> !partitions.contains(row.get("Physical Name field")))
                .collect(Collectors.toMap(
                        row -> (String) row.get("Physical Name field"),
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
        this.artifactoryPath = artifactoryLink + "\"/schemas/pe/" + request.getUuaaMaster() + "/raw/" + dfRawName + "/latest/" + dfRawName + ".output.schema\"";
        this.masterArtifactoryPath = artifactoryLink + "\"/schemas/pe/" + request.getUuaaMaster() + "/master/" + dfMasterName + "/latest/" + dfMasterName + ".output.schema\"";
        this.rawArtifactoryPath = artifactoryLink + "\"/schemas/pe/" + request.getUuaaMaster() + "/raw/" + dfRawName + "/latest/" + dfRawName + ".output.schema\"";

        // Data paths
        this.dfStagingPath = "/in/staging/datax/" + dfUuaa + "/" + dfStagingName;
        this.dfRawPath = "/data/raw/" + dfUuaa + "/data/" + dfRawName;
        this.dfMasterPath = "/data/master/" + request.getUuaaMaster() + "/data/" + dfMasterName;

        // Subset para particiones
        this.subset = getSubset(Arrays.asList(request.getParticiones().split(",")));

        // Partition list
        this.partitionList = getPartitionList(Arrays.asList(request.getParticiones().split(",")));
    }

    private void buildJsonIds() {
        String uuaa = request.getUuaaMaster();
        this.idJsonMaster = uuaa + "-pe-hmm-qlt-" + tag + "m-01";
        this.idJsonRaw = uuaa + "-pe-hmm-qlt-" + tag + "r-01";
        this.idJsonStaging = uuaa + "-pe-hmm-qlt-" + tag + "s-01";
    }

    private void processKirbyFields() {
        // Trim all columns
        StringBuilder trimColumns = new StringBuilder("\"");
        List<String> sourceFields = masterData.stream()
                .filter(row -> !"Calculated".equals(row.get("Source field")))
                .map(row -> (String) row.get("Source field"))
                .collect(Collectors.toList());

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
                .filter(row -> !"Calculated".equals(row.get("Source field")))
                .map(row -> (String) row.get("Source field"))
                .collect(Collectors.toList());

        // Timestamp columns
        this.rawTimestampColumns = masterData.stream()
                .filter(row -> "timestamp".equals(row.get("Data Type")))
                .filter(row -> !"Calculated".equals(row.get("Source field")))
                .map(row -> (String) row.get("Source field"))
                .collect(Collectors.toList());

        // Master field with origin list
        this.masterFieldWithOriginList = masterData.stream()
                .map(row -> Arrays.asList(
                        (String) row.get("Physical Name field"),
                        (String) row.get("Source field")
                ))
                .collect(Collectors.toList());

        // Master field list
        this.masterFieldList = masterData.stream()
                .map(row -> (String) row.get("Physical Name field"))
                .collect(Collectors.toList());
    }

    private String getSubset(List<String> partitions) {
        StringBuilder subset = new StringBuilder();
        for (int i = 0; i < partitions.size(); i++) {
            String key = partitions.get(i);
            String parameter = "PARAMETER" + (i + 1);
            if ("cutoff_date".equals(key)) {
                parameter = "DATE";
            }

            if (i > 0) {
                subset.append(" and ");
            }
            subset.append(key).append("='\"${?").append(parameter).append("}\"'");
        }
        return subset.toString();
    }

    private String getPartitionList(List<String> partitions) {
        return partitions.stream()
                .map(field -> "\"" + field + "\"")
                .collect(Collectors.joining(", "));
    }
}
