package com.example.it210ticketbus.dto.response;

import com.example.it210ticketbus.enums.Role;
import lombok.*;

/**
 * DTO for returning profile data to the client (CORE-03)
 * Contains user info + profile details
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDTO {
    
    private Long userId;
    private String username;
    private Role role;
    private String fullName;
    private String phone;
    private String email;
    private String address;
}
