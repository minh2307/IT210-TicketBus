package com.example.it210ticketbus.dto.request;

import com.example.it210ticketbus.enums.BusStatus;
import com.example.it210ticketbus.enums.BusType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for Bus create/update request (CORE-04)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusRequest {
    
    @NotBlank(message = "Biển số xe không được để trống")
    @Size(max = 20, message = "Biển số xe không được vượt quá 20 ký tự")
    private String licensePlate;
    
    @NotNull(message = "Loại xe không được để trống")
    private BusType busType;
    
    @NotNull(message = "Tổng số ghế không được để trống")
    @Min(value = 1, message = "Tổng số ghế phải lớn hơn 0")
    private Integer totalSeats;
    
    @NotBlank(message = "Hãng xe không được để trống")
    @Size(max = 100, message = "Hãng xe không được vượt quá 100 ký tự")
    private String companyName;
    
    @Size(max = 100, message = "Tên tài xế không được vượt quá 100 ký tự")
    private String driverName;
    
    @Pattern(regexp = "0[0-9]{9}", message = "Số điện thoại phải có 10 số và bắt đầu bằng số 0")
    private String driverPhone;
    
    @NotNull(message = "Trạng thái không được để trống")
    private BusStatus status;
}
