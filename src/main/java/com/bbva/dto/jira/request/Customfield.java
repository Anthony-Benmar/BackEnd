package com.bbva.dto.jira.request;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

@Setter
@Getter
public class Customfield {
    public String name;
    @Nullable
    public Boolean checked;
    @Nullable
    public Boolean mandatory;
    @Nullable
    public Boolean option;
    public String id;
    @Nullable
    public Integer rank;
    public Boolean disabled;
    public String value;
    public String self;

}
