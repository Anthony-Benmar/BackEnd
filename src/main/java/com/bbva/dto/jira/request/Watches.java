package com.bbva.dto.jira.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Watches {
    public String self;
    public int watchCount;
    public boolean isWatching;
}
