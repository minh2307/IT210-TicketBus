// === FILE: com/example/it210ticketbus/repository/RouteRepository.java ===
package com.example.it210ticketbus.repository;

import com.example.it210ticketbus.model.Location;
import com.example.it210ticketbus.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    
    Optional<Route> findByOriginAndDestination(Location origin, Location destination);
    
    @Query("SELECT r FROM Route r WHERE r.origin.id = :originId AND r.destination.id = :destinationId")
    Optional<Route> findByOriginIdAndDestinationId(@Param("originId") Long originId, 
                                                 @Param("destinationId") Long destinationId);
    
    @Query("SELECT r FROM Route r WHERE r.origin = :location OR r.destination = :location")
    List<Route> findAllByOriginOrDestination(@Param("location") Location location);
    
    @Query("SELECT r FROM Route r ORDER BY r.distance ASC")
    List<Route> findAllOrderByDistance();

    @Query("SELECT r FROM Route r LEFT JOIN FETCH r.origin LEFT JOIN FETCH r.destination")
    List<Route> findAllWithLocations();
    
    boolean existsByOriginAndDestination(Location origin, Location destination);
}
