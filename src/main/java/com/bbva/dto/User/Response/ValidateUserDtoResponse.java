package com.bbva.dto.User.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@AllArgsConstructor
@Setter
@Getter
public class ValidateUserDtoResponse {
    @JsonProperty("id")
    public int ID;
    @JsonProperty("google")
    public String Google;
    @JsonProperty("fullName")
    public String FullName;
    @JsonProperty("email")
    public String Email;
    @JsonProperty("photoUrl")
    public String PhotoUrl;
}
