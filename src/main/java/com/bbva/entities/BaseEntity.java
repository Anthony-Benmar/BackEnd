package com.bbva.entities;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BaseEntity {

    @SerializedName("status_type")
    private int statusType;

    @SerializedName("operation_user")
    private int operationUser;

    @SerializedName("operation_date")
    private Date operationDate;
}
