package com.bbva.dto.jira.request;

import com.bbva.dto.jira.request.*;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class Fields {
    public Issuetype issuetype;
    public Customfield customfield_10270;
    public Project project;
    public List<String> customfield_13300;
    public String description;
    public String summary;
    public String customfield_10004;
    public List<String> labels;
}
