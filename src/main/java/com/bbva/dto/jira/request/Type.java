package com.bbva.dto.jira.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Type {
    public String id;
    public String name;
    public String inward;
    public String outward;
    public String self;
}
