package com.bbva.entities.issueticket;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;

@Getter
@Setter
public class IssueTicketEntityList {
    @SerializedName("issueTicketLista")
    private ArrayList<IssueTicketEntity> issueTicketLista;
}