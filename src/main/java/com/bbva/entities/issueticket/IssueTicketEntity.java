package com.bbva.entities.issueticket;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssueTicketEntity {
    @SerializedName("templateId")
    private int templateId;
    @SerializedName("projectId")
    private int projectId;
    @SerializedName("boardId")
    private int boardId;
    @SerializedName("feature")
    private String feature;
    @SerializedName("folio")
    private String folio;
    @SerializedName("source")
    private int source;
    @SerializedName("ingesta")
    private String ingesta;
    @SerializedName("token")
    private String token;
}