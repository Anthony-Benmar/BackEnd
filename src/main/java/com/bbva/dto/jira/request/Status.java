package com.bbva.dto.jira.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Status {
    public String self;
    public String description;
    public String iconUrl;
    public String name;
    public String id;
    public StatusCategory statusCategory;
}
