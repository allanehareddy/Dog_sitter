package com.misha.springbootnewswagger.dto;

import com.misha.springbootnewswagger.entities.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SitterResponse {

    private Long id;
    private String contactName;
    private String email;
    private String companyName;
    private String Address;
    private PointDto location;
    private Time timeOfOpening;
    private Time timeOfClosing;
    private Double chargesPerHour;
    private Boolean enable;
    private String logo;
    private Role role;

}
