package com.misha.springbootnewswagger.dto;

import com.misha.springbootnewswagger.entities.enums.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDto {

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 25, message = "Size for name should be between 3-25 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name can only contain letters and spaces")
    private String contactName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Address is required")
    private String Address;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    @NotNull(message = "Time of opening is required")
    private Time timeOfOpening;

    @NotNull(message = "Time of closing is required")
    private Time timeOfClosing;

    @NotNull(message = "Charges per hour are required")
    private Double chargesPerHour;

    @NotNull(message = "Enable is required")
    private Boolean enable;

    @NotBlank(message = "Logo is required")
    private String logo;

    @NotNull(message = "Role is required")
    private Role role;

}
