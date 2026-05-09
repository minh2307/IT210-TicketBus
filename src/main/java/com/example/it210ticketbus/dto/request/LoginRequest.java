// === LoginRequest.java ===
package com.example.it210ticketbus.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * DTO for user login request
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    
    @NotBlank(message = "Email hoặc số điện thoại là bắt buộc")
    @Pattern(regexp = "^(^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$|^(0[3|5|7|8|9])[0-9]{8}$)$", 
             message = "Email hoặc số điện thoại không hợp lệ")
    private String emailOrPhone;
    
    @NotBlank(message = "Mật khẩu là bắt buộc")
    private String password;
}
