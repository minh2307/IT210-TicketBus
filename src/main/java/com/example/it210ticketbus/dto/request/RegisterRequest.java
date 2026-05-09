// === RegisterRequest.java ===
package com.example.it210ticketbus.dto.request;

import com.example.it210ticketbus.enums.Role;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO for user registration request
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    
    @NotBlank(message = "Email là bắt buộc")
    @Email(message = "Định dạng email không hợp lệ")
    private String email;
    
    @NotBlank(message = "Số điện thoại là bắt buộc")
    @Pattern(regexp = "^(0[3|5|7|8|9])[0-9]{8}$", message = "Định dạng số điện thoại Việt Nam không hợp lệ")
    private String phone;
    
    @NotBlank(message = "Họ và tên là bắt buộc")
    @Size(max = 100, message = "Họ và tên không được vượt quá 100 ký tự")
    private String fullName;
    
    @NotBlank(message = "Mật khẩu là bắt buộc")
    @Size(min = 8, max = 100, message = "Mật khẩu phải từ 8 đến 100 ký tự")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).+$", message = "Mật khẩu phải chứa ít nhất một chữ hoa và một số")
    private String password;
    
    @NotBlank(message = "Xác nhận mật khẩu là bắt buộc")
    private String confirmPassword;
    
    @NotBlank(message = "Địa chỉ là bắt buộc")
    @Size(max = 200, message = "Địa chỉ không được vượt quá 200 ký tự")
    private String address;
    
    private String username;
    
    // Default role for regular users
    private Role role = Role.PASSENGER;
}
