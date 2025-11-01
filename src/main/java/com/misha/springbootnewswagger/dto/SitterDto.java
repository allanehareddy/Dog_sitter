package com.misha.springbootnewswagger.dto;

import com.misha.springbootnewswagger.annotations.OpeningBeforeClosing;
import com.misha.springbootnewswagger.annotations.RoleValidation;
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
@OpeningBeforeClosing(message = "Opening time must be before closing time")
public class SitterDto {

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 25, message = "Size for name should be between 3-25 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name can only contain letters and spaces")
    private String contactName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,25}$",
            message = "Password must be 8-25 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    @NotBlank(message = "Password is required")
    private String password;

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

    @NotNull(message = "Role is required")
    private Role role;

}
