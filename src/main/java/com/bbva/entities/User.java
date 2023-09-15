package com.bbva.entities;

import com.google.gson.annotations.SerializedName;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor 
@Setter
@Getter
@Data
public class User extends BaseEntity {

    @SerializedName("user_id")
    public Integer userId;

    @SerializedName("google_id")
    public String googleId;

    @SerializedName("full_name")
    public String fullName;

    @SerializedName("email")
    public String email;

    @SerializedName("employee_id")
    public String employeeId;

    public String rolesNombre;

    public String rolesId;


}
