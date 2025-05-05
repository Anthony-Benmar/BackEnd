package com.bbva.entities.board;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class JiraTeamBacklogEntity {

    private Long teamBacklogId;
    private String teamBacklogName;

}
