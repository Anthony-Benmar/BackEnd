package com.bbva.dto.jira.request;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

@Setter
@Getter
public class IssuetypeBackup {
    public String self;
    public String id;
    public String description;
    public String iconUrl;
    public String name;
    @Nullable
    public Boolean subtask;
    @Nullable
    public Integer avatarId;
}
