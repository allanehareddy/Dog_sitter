package com.misha.springbootnewswagger.entities;

import com.misha.springbootnewswagger.entities.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import java.sql.Time;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "sitter")
@NoArgsConstructor
public class SitterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contactName;

    private String email;

    private String password;

    private String companyName;

    private String Address;

    private Double latitude;
    private Double longitude;

    private Time timeOfOpening;

    private Time timeOfClosing;

    private Double chargesPerHour;

    private Boolean enable;

    private String logo;

    private Role role;

}
