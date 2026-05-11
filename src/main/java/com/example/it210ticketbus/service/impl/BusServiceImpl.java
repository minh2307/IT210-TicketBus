package com.example.it210ticketbus.service.impl;

import com.example.it210ticketbus.dto.request.BusRequest;
import com.example.it210ticketbus.dto.response.BusDTO;
import com.example.it210ticketbus.enums.BusStatus;
import com.example.it210ticketbus.enums.BusType;
import com.example.it210ticketbus.exception.BusNotFoundException;
import com.example.it210ticketbus.exception.LicensePlateAlreadyExistsException;
import com.example.it210ticketbus.model.Bus;
import com.example.it210ticketbus.repository.BusRepository;
import com.example.it210ticketbus.service.BusService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of BusService (CORE-04)
 * Handles CRUD operations for Bus entity
 */
@Service
@RequiredArgsConstructor
public class BusServiceImpl implements BusService {
    
    private final BusRepository busRepository;
    private final com.example.it210ticketbus.repository.RouteRepository routeRepository;
    
    @Transactional(readOnly = true)
    public Page<BusDTO> getAllBuses(String keyword, BusStatus status, BusType busType, Pageable pageable) {
        Page<Bus> busPage = busRepository.searchBuses(keyword, status, busType, pageable);
        return busPage.map(this::mapToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BusDTO getBusById(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new BusNotFoundException(id));
        return mapToDTO(bus);
    }
    
    @Override
    @Transactional
    public BusDTO createBus(BusRequest request) {
        // Check license plate uniqueness
        if (busRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new LicensePlateAlreadyExistsException(request.getLicensePlate());
        }
        
        Bus bus = Bus.builder()
                .licensePlate(request.getLicensePlate())
                .busType(request.getBusType())
                .totalSeats(request.getTotalSeats())
                .companyName(request.getCompanyName())
                .driverName(request.getDriverName())
                .driverPhone(request.getDriverPhone())
                .status(request.getStatus())
                .build();
        
        Bus savedBus = busRepository.save(bus);
        
        // Handle assigned routes
        if (request.getRouteIds() != null && !request.getRouteIds().isEmpty()) {
            List<com.example.it210ticketbus.model.Route> routes = routeRepository.findAllById(request.getRouteIds());
            savedBus.setAssignedRoutes(routes);
            savedBus = busRepository.save(savedBus);
        }
        
        return mapToDTO(savedBus);
    }
    
    @Override
    @Transactional
    public BusDTO updateBus(Long id, BusRequest request) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new BusNotFoundException(id));
        
        // Check license plate uniqueness (exclude current bus)
        if (busRepository.existsByLicensePlateAndIdNot(request.getLicensePlate(), id)) {
            throw new LicensePlateAlreadyExistsException(request.getLicensePlate());
        }
        
        bus.setLicensePlate(request.getLicensePlate());
        bus.setBusType(request.getBusType());
        bus.setTotalSeats(request.getTotalSeats());
        bus.setCompanyName(request.getCompanyName());
        bus.setDriverName(request.getDriverName());
        bus.setDriverPhone(request.getDriverPhone());
        bus.setStatus(request.getStatus());
        
        // Handle assigned routes
        if (request.getRouteIds() != null) {
            List<com.example.it210ticketbus.model.Route> routes = routeRepository.findAllById(request.getRouteIds());
            bus.setAssignedRoutes(routes);
        }
        
        Bus updatedBus = busRepository.save(bus);
        return mapToDTO(updatedBus);
    }
    
    @Override
    @Transactional
    public void deleteBus(Long id) {
        if (!busRepository.existsById(id)) {
            throw new BusNotFoundException(id);
        }
        busRepository.deleteById(id);
    }
    
    /**
     * Helper: Map Bus entity to BusDTO
     */
    private BusDTO mapToDTO(Bus bus) {
        return BusDTO.builder()
                .id(bus.getId())
                .licensePlate(bus.getLicensePlate())
                .busType(bus.getBusType())
                .totalSeats(bus.getTotalSeats())
                .companyName(bus.getCompanyName())
                .driverName(bus.getDriverName())
                .driverPhone(bus.getDriverPhone())
                .status(bus.getStatus())
                .routes(bus.getAssignedRoutes() != null && !bus.getAssignedRoutes().isEmpty() ? 
                        bus.getAssignedRoutes() : routeRepository.findDistinctRoutesByBusId(bus.getId()))
                .routeIds((bus.getAssignedRoutes() != null && !bus.getAssignedRoutes().isEmpty()) ? 
                        bus.getAssignedRoutes().stream().map(com.example.it210ticketbus.model.Route::getId).collect(Collectors.toList()) :
                        routeRepository.findDistinctRoutesByBusId(bus.getId()).stream().map(com.example.it210ticketbus.model.Route::getId).collect(Collectors.toList()))
                .createdAt(bus.getCreatedAt())
                .updatedAt(bus.getUpdatedAt())
                .build();
    }
}
