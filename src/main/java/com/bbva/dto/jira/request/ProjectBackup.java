package com.bbva.dto.jira.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProjectBackup {
    public String self;
    public String id;
    public String key;
    public String name;
    public String projectTypeKey;
    public AvatarUrls avatarUrls;
    public ProjectCategory projectCategory;
}
