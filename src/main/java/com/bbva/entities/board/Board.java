package com.bbva.entities.board;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class Board {
    public Integer board_id;
    public String board_code;
    public String name;
    public String description;
    public int project_id;
    public String board_jira_id;
    public String project_jira_id;
    public String project_jira_key;
}
