package com.project.ecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "addresses")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;
    @NotBlank
    @Size(min = 3, message = "Building name must be 3 character long")
    private String buildingName;
    @NotBlank
    @Size(min = 3, message = "Street name must be 3 character long")
    private String street;
    @NotBlank
    @Size(min = 3, message = "City name must be 3 character long")
    private String city;
    @NotBlank
    @Size(min = 2, message = "State name must be 2 character long")
    private String state;
    @NotBlank
    @Size(min = 2, message = "State name must be 2 character long")
    private String country;
    @NotBlank
    private String zipcode;

    @ToString.Exclude
    // Many Address can be associated with one User:
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "address", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();
}
