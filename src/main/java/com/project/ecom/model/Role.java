package com.project.ecom.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    private AppRole role;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
