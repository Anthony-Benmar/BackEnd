package com.bbva.entities.external;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ExternalEntity {
    @SerializedName("dataObject")
    GobiernoEntity dataObject;
    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;
}