package com.bbva.dto.jira.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StatusCategory {
    public String self;
    public int id;
    public String key;
    public String colorName;
    public String name;
}
