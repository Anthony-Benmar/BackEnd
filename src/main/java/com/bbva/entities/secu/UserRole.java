package com.bbva.entities.secu;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@Data
public class UserRole {
    @SerializedName("user_role_id")
    private int userRoleId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("role_id")
    private int roleId;

    @SerializedName("status_type")
    private int statusType;

    @SerializedName("operation_user")
    private int operationUser;
}
