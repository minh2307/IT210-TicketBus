// === UserProfileResponse.java ===
package com.example.it210ticketbus.dto.response;

import com.example.it210ticketbus.enums.Role;
import lombok.*;

/**
 * DTO for user profile response
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    
    private Long id;
    private String username;
    private Role role;
    private String fullName;
    private String phone;
    private String email;
}
