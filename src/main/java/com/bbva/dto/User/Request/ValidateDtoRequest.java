package com.bbva.dto.User.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ValidateDtoRequest {
    public String email;
    public String googleId;
    public String imageUrl;
    public String name;
    public String employeeId;
    public int roleId;
}
