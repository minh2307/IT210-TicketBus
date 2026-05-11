package com.example.it210ticketbus.service;

import com.example.it210ticketbus.dto.request.BusRequest;
import com.example.it210ticketbus.dto.response.BusDTO;
import com.example.it210ticketbus.enums.BusStatus;
import com.example.it210ticketbus.enums.BusType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for Bus management (CORE-04)
 * Only ADMIN role should access these operations
 */
public interface BusService {

    /**
     * Get all buses with pagination and filtering
     * @param keyword search keyword
     * @param status filter by status
     * @param busType filter by bus type
     * @param pageable pagination parameters
     * @return page of BusDTO
     */
    Page<BusDTO> getAllBuses(String keyword, BusStatus status, BusType busType, Pageable pageable);
    
    /**
     * Get a bus by ID
     * @param id the bus ID
     * @return BusDTO
     */
    BusDTO getBusById(Long id);
    
    /**
     * Create a new bus
     * @param request the bus data
     * @return created BusDTO
     */
    BusDTO createBus(BusRequest request);
    
    /**
     * Update an existing bus
     * @param id the bus ID
     * @param request the updated bus data
     * @return updated BusDTO
     */
    BusDTO updateBus(Long id, BusRequest request);
    
    /**
     * Delete a bus by ID
     * @param id the bus ID
     */
    void deleteBus(Long id);
}
