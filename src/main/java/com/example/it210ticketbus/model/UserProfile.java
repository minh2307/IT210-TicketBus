package com.example.it210ticketbus.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String fullName;
    
    @Column(nullable = false, length = 20)
    private String phone;
    
    @Column(nullable = false, length = 100)
    private String email;
    
    @Column(length = 255)
    private String address;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
