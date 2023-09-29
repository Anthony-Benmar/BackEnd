package com.bbva.dto.jira.request;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

@Setter
@Getter
public class Issuetype {
    public String id;
    public String name;
}
