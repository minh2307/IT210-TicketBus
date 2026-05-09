package com.example.it210ticketbus.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(name = "provinceCode", nullable = false, unique = true, length = 10)
    private String provinceCode;
    
    @OneToMany(mappedBy = "origin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Route> routesAsOrigin;
    
    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Route> routesAsDestination;
}
