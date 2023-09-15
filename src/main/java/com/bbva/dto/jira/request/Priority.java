package com.bbva.dto.jira.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Priority {
    public String self;
    public String iconUrl;
    public String name;
    public String id;
}
