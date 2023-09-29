package com.bbva.dto.jira.request;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

@Setter
@Getter
public class Customfield {
    public String name;
    public String id;
    public Boolean disabled;
    public String value;
}
