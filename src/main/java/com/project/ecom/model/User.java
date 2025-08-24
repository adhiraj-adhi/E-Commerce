package com.project.ecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
//        @UniqueConstraint(columnNames = "email")
    })
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    private String password;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",   // join table name
            joinColumns = @JoinColumn(name = "user_id"),        // column for the User
            inverseJoinColumns = @JoinColumn(name = "role_id")  // column for the Role
    )
    private Set<Role> roles = new HashSet<>(); // We are initializing it directly to avoid NullPointerException. This is real world followed practice


    @ToString.Exclude
    // A user can be seller also, so user can have association with multiple products
    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
    orphanRemoval = true)
    private Set<Product> products;


    // One User can have multiple addresses
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "user_address",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "address_id")
    )
    private List<Address> addresses = new ArrayList<>();
}
