package com.bbva.dto.jira.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Votes {
    public String self;
    public int votes;
    public boolean hasVoted;
}
