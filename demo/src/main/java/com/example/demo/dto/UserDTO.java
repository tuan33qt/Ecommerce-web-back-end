package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UserDTO {
    @JsonProperty("fullname")
    private String fullName;
    @JsonProperty("phone_number")
    private String phoneNumber;
    private  String address;
    @JsonProperty("user_name")
    @NotBlank(message = "username is required")
    private String userName;
    @NotBlank(message = "password is required")
    private String password;
    @JsonProperty("date_of_birth")
    private Date dateOfBirth;
    @JsonProperty("google_account_id")
    private Integer googleAccountId;
    @JsonProperty("facebook_account_id")
    private Integer facebookAccountId;
    @JsonProperty("role_id")
    @NotNull(message = "role id is required")
    private  Long roleId;
}
