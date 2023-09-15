package com.bbva.dto.jira.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Assignee {
    public String self;
    public String name;
    public String key;
    public String emailAddress;
    public AvatarUrls avatarUrls;
    public String displayName;
    public boolean active;
    public String timeZone;
}
