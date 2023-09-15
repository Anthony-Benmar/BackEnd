package com.bbva.dto.jira.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProjectCategory {
    public String self;
    public String id;
    public String description;
    public String name;
}
