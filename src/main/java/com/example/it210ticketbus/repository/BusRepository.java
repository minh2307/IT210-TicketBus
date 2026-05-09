package com.example.it210ticketbus.repository;

import com.example.it210ticketbus.enums.BusStatus;
import com.example.it210ticketbus.model.Bus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Bus entity operations (CORE-04)
 */
@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    
    /**
     * Find bus by license plate
     */
    Optional<Bus> findByLicensePlate(String licensePlate);
    
    /**
     * Check if license plate already exists
     */
    boolean existsByLicensePlate(String licensePlate);
    
    /**
     * Check if license plate exists for another bus (excluding given bus ID)
     */
    boolean existsByLicensePlateAndIdNot(String licensePlate, Long id);
    
    /**
     * Search buses by keyword and/or status
     */
    @Query("SELECT b FROM Bus b WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "b.licensePlate LIKE %:keyword% OR " +
           "b.companyName LIKE %:keyword% OR " +
           "b.driverName LIKE %:keyword%) AND " +
           "(:status IS NULL OR :status = '' OR b.status = :status)")
    Page<Bus> searchBuses(@Param("keyword") String keyword, 
                          @Param("status") BusStatus status, 
                          Pageable pageable);
    
    /**
     * Count buses by status
     */
    long countByStatus(BusStatus status);
    
    /**
     * Find top 5 most recent buses by ID
     */
    List<Bus> findTop5ByOrderByIdDesc();
}
