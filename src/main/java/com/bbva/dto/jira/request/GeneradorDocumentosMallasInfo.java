package com.bbva.dto.jira.request;

import com.bbva.dto.project.request.InsertProjectParticipantDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GeneradorDocumentosMallasInfo {
    private List<InsertProjectParticipantDTO> smParticipant;
    private List<InsertProjectParticipantDTO> poParticipant;
    private List<Map.Entry<String, Map<String, String>>> listJobsDetail;
    private List<Map.Entry<String, Map<String, List<String>>>> listJobSummary;
    private Map<String, String> descripcionMallas;
    private Map<String, Map<String, Long>> conteoMallas;
}
