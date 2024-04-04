package com.bbva.dto.project.response;

import com.bbva.dto.project.request.InsertProjectInfoDTO;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectInfoFilterResponse {
    public int count;
    public int pages_amount;
    public List<InsertProjectInfoDTO> data;
}

